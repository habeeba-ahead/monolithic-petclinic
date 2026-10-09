# Security Audit: application.properties Files

**Audit Date:** 2025  
**Auditor:** Automated Security Assessment Agent  
**Scope:** `monolith/src/main/resources/application.properties` and `activemq/src/main/resources/application.properties`  
**Priority:** HIGHEST — identified as the single most important missing artifact by all five assessment agents

---

## Table of Contents

1. [Monolith application.properties — Full Contents](#1-monolith-applicationproperties--full-contents)
2. [ActiveMQ application.properties — Full Contents](#2-activemq-applicationproperties--full-contents)
3. [Hardcoded Credentials Assessment](#3-hardcoded-credentials-assessment)
4. [spring.jpa.hibernate.ddl-auto Confirmation](#4-springjpahibernatedll-auto-confirmation)
5. [spring.sql.init.mode Confirmation](#5-springsqlinitmode-confirmation)
6. [HikariCP Connection Pool Configuration](#6-hikaricp-connection-pool-configuration)
7. [Security Findings](#7-security-findings)
8. [Recommendations and Remediation Actions](#8-recommendations-and-remediation-actions)

---

## 1. Monolith application.properties — Full Contents

**File path:** `monolith/src/main/resources/application.properties`

```properties
# Database
spring.datasource.schema=classpath*:db/schema.sql
spring.datasource.data=classpath*:db/data.sql

# Web
spring.thymeleaf.mode=HTML

# JPA
spring.jpa.hibernate.ddl-auto=none

# ActiveMQ
spring.activemq.broker-url=tcp://localhost:61616

# Internationalization
spring.messages.basename=messages/messages

# Actuator / Management
management.endpoints.web.base-path=/manage
management.endpoints.web.exposure.include=*

# Logging
logging.level.org.springframework=INFO
# logging.level.org.springframework.web=DEBUG
# logging.level.org.springframework.context.annotation=TRACE

# Server endpoint
server.port= 8080
```

### Property-by-Property Breakdown

| Property | Value | Notes |
|---|---|---|
| `spring.datasource.schema` | `classpath*:db/schema.sql` | Points to schema init script containing DROP TABLE statements |
| `spring.datasource.data` | `classpath*:db/data.sql` | Points to seed data script |
| `spring.thymeleaf.mode` | `HTML` | Standard Thymeleaf HTML5 mode |
| `spring.jpa.hibernate.ddl-auto` | `none` | Hibernate does NOT manage DDL — schema managed by SQL scripts |
| `spring.activemq.broker-url` | `tcp://localhost:61616` | Plaintext TCP, no credentials, no SSL |
| `spring.messages.basename` | `messages/messages` | i18n message bundle location |
| `management.endpoints.web.base-path` | `/manage` | Actuator base path |
| `management.endpoints.web.exposure.include` | `*` | **ALL actuator endpoints exposed** — see Finding M-02 |
| `logging.level.org.springframework` | `INFO` | Standard log level |
| `server.port` | `8080` | HTTP only, no TLS configured |

**Notable absences:**
- `spring.datasource.url` — not set; defaults to embedded HSQLDB (confirmed by `hsqldb` dependency in `monolith/pom.xml`)
- `spring.datasource.username` — not set
- `spring.datasource.password` — not set
- `spring.sql.init.mode` — not set (see Section 5)
- No HikariCP pool properties set (see Section 6)

---

## 2. ActiveMQ application.properties — Full Contents

**File path:** `activemq/src/main/resources/application.properties`

```properties
# ActiveMQ
activemq.broker-url=tcp://0.0.0.0:61616
spring.jms.template.default-destination=visitCreated

# Actuator / Management
management.endpoints.web.base-path=/
management.endpoints.web.exposure.include=hawtio,jolokia

# Logging
logging.level.org.springframework=INFO
# logging.level.org.springframework.web=DEBUG
# logging.level.org.springframework.context.annotation=TRACE

# Server endpoint
server.port= 8081

# Hawtio
hawtio.authenticationEnabled=false
```

### Property-by-Property Breakdown

| Property | Value | Notes |
|---|---|---|
| `activemq.broker-url` | `tcp://0.0.0.0:61616` | Broker binds to ALL interfaces, plaintext TCP, no credentials |
| `spring.jms.template.default-destination` | `visitCreated` | JMS queue/topic name |
| `management.endpoints.web.base-path` | `/` | Actuator at root path |
| `management.endpoints.web.exposure.include` | `hawtio,jolokia` | Hawtio and Jolokia exposed — see Finding A-01 |
| `logging.level.org.springframework` | `INFO` | Standard log level |
| `server.port` | `8081` | HTTP only, no TLS configured |
| `hawtio.authenticationEnabled` | `false` | **Authentication DISABLED** — see Finding A-01 (CRITICAL) |

**Notable absences:**
- No broker username/password set
- No SSL/TLS configuration for broker
- No `spring.activemq.user` / `spring.activemq.password`

---

## 3. Hardcoded Credentials Assessment

### 3.1 Database Credentials

**Finding:** No hardcoded database password is present in either file.

- `spring.datasource.password` is **absent** from `monolith/src/main/resources/application.properties`
- The monolith uses an **embedded HSQLDB** in-memory database (dependency: `org.hsqldb:hsqldb` with `scope=runtime` in `monolith/pom.xml`)
- Embedded HSQLDB requires no password by default
- **Status:** No credential rotation required for database password — none found, none committed

### 3.2 ActiveMQ Broker Credentials

**Finding:** No hardcoded broker password is present in either file.

- `spring.activemq.password` is **absent** from `monolith/src/main/resources/application.properties`
- `activemq.password` is **absent** from `activemq/src/main/resources/application.properties`
- The broker runs with **no authentication** (default ActiveMQ behaviour when no credentials are configured)
- **Status:** No credential rotation required — however, the absence of credentials means the broker is unauthenticated (see Finding A-02)

### 3.3 API Keys and Other Secrets

**Finding:** No API keys, JWT secrets, OAuth client secrets, or other credentials are present in either file.

- **Status:** No secrets found, no rotation required

### 3.4 Summary Table

| Secret Type | Monolith | ActiveMQ | Action Required |
|---|---|---|---|
| Database password | **Not present** | N/A | None — no hardcoded secret |
| Broker password | Not present | **Not present** | None — but broker is unauthenticated (Finding A-02) |
| API key | Not present | Not present | None |
| JWT / OAuth secret | Not present | Not present | None |

> **Conclusion:** No credentials were found hardcoded in either properties file. No immediate rotation is required. However, the *absence* of authentication on the ActiveMQ broker is itself a critical security finding (see Section 7).

---

## 4. spring.jpa.hibernate.ddl-auto Confirmation

**File:** `monolith/src/main/resources/application.properties`  
**Property:** `spring.jpa.hibernate.ddl-auto`  
**Value:** `none`

### Analysis

| Concern from Assessment | Actual Value | Risk |
|---|---|---|
| Feared value: `create-drop` | **Actual: `none`** | ✅ NOT present |
| Feared value: `create` | **Actual: `none`** | ✅ NOT present |
| Feared value: `update` | **Actual: `none`** | ✅ NOT present |

**`ddl-auto=none`** means Hibernate will **not** attempt to create, update, or drop the database schema. DDL management is fully delegated to the SQL scripts referenced by `spring.datasource.schema` and `spring.datasource.data`.

**Verdict:** The feared `create-drop` scenario via Hibernate DDL auto is **NOT present**. This is the correct setting for a production-oriented configuration.

> ⚠️ **However:** The schema.sql script itself contains `DROP TABLE ... IF EXISTS` statements at the top (see Finding M-01). The risk of data destruction is real but comes from the SQL init scripts, not from Hibernate DDL auto.

---

## 5. spring.sql.init.mode Confirmation

**File:** `monolith/src/main/resources/application.properties`  
**Property:** `spring.sql.init.mode`  
**Value:** **NOT SET** (property is absent from the file)

### Analysis

The property `spring.sql.init.mode` does not appear in either properties file.

**Default behaviour depends on Spring Boot version:**

- **Spring Boot 2.4 and earlier:** The equivalent property was `spring.datasource.initialization-mode`. Default was `embedded` — scripts only run against embedded databases (HSQLDB, H2, Derby).
- **Spring Boot 2.5+:** `spring.sql.init.mode` defaults to `embedded`.

Since the monolith uses an embedded HSQLDB database, the default `embedded` mode means:
- `schema.sql` **will** be executed on startup (because the datasource is embedded)
- `data.sql` **will** be executed on startup

**The `spring.sql.init.mode=always` feared by the assessment is NOT present.**

> ⚠️ **However:** Because the database IS embedded, the scripts DO run on every startup. The `schema.sql` contains `DROP TABLE ... IF EXISTS` statements, which means all data is wiped on every restart. This is expected behaviour for an in-memory embedded database but would be catastrophic if the datasource were ever pointed at a persistent database without changing this configuration.

---

## 6. HikariCP Connection Pool Configuration

**File:** `monolith/src/main/resources/application.properties`  
**HikariCP properties:** **NONE SET** — all values are Spring Boot / HikariCP defaults

### Default HikariCP Values (Spring Boot 2.x)

| HikariCP Property | Default Value | Notes |
|---|---|---|
| `spring.datasource.hikari.maximum-pool-size` | `10` | Max connections in pool |
| `spring.datasource.hikari.minimum-idle` | Same as `maximum-pool-size` (10) | Min idle connections |
| `spring.datasource.hikari.connection-timeout` | `30000` ms (30 seconds) | Max wait for connection |
| `spring.datasource.hikari.idle-timeout` | `600000` ms (10 minutes) | Max idle connection lifetime |
| `spring.datasource.hikari.max-lifetime` | `1800000` ms (30 minutes) | Max connection lifetime |
| `spring.datasource.hikari.keepalive-time` | `0` (disabled) | Keepalive ping interval |
| `spring.datasource.hikari.pool-name` | `HikariPool-1` | Pool name in logs/JMX |
| `spring.datasource.hikari.auto-commit` | `true` | Auto-commit mode |

**Verdict:** No custom HikariCP configuration is present. The application runs with all Spring Boot defaults. For an embedded in-memory HSQLDB used in development/demo, this is acceptable. For a production deployment against a persistent database, explicit pool sizing and timeout tuning would be required.

---

## 7. Security Findings

### Finding M-01 — MEDIUM: schema.sql Contains DROP TABLE Statements

**File:** `monolith/src/main/resources/db/schema.sql`  
**Severity:** MEDIUM (currently mitigated by embedded DB; HIGH if pointed at persistent DB)

The schema initialization script begins with:
```sql
DROP TABLE vet_specialties IF EXISTS;
DROP TABLE vets IF EXISTS;
DROP TABLE specialties IF EXISTS;
DROP TABLE visits IF EXISTS;
DROP TABLE pets IF EXISTS;
DROP TABLE types IF EXISTS;
DROP TABLE owners IF EXISTS;
```

These DROP statements execute on every application startup because the datasource is embedded (HSQLDB). If the application is ever reconfigured to use a persistent database (PostgreSQL, MySQL, etc.) without also changing `spring.sql.init.mode` to `never`, **all data will be destroyed on every restart**.

**Immediate action:** None required for current embedded DB setup. Before any migration to a persistent database, `spring.sql.init.mode=never` must be set and the DROP statements must be removed or guarded.

---

### Finding M-02 — HIGH: Actuator Wildcard Exposure (Monolith)

**File:** `monolith/src/main/resources/application.properties`  
**Property:** `management.endpoints.web.exposure.include=*`  
**Severity:** HIGH

All actuator endpoints are exposed, including:
- `/manage/env` — exposes all environment variables and configuration properties
- `/manage/heapdump` — allows heap dump extraction (memory contents)
- `/manage/threaddump` — thread state information
- `/manage/shutdown` — can shut down the application (if enabled)
- `/manage/loggers` — allows runtime log level changes
- `/manage/beans` — exposes Spring bean graph

No authentication is configured for the actuator endpoints. Any network-accessible client can call these endpoints.

**Recommended fix:**
```properties
management.endpoints.web.exposure.include=health,info
```

---

### Finding A-01 — CRITICAL: Hawtio Authentication Disabled

**File:** `activemq/src/main/resources/application.properties`  
**Property:** `hawtio.authenticationEnabled=false`  
**Severity:** CRITICAL

Hawtio is a web-based JMX console that provides full administrative access to the ActiveMQ broker, including:
- Viewing and purging all message queues
- Sending arbitrary messages to queues
- Stopping/starting the broker
- Viewing all JMX MBeans

With `hawtio.authenticationEnabled=false`, **anyone who can reach port 8081 has full administrative control of the broker with no credentials required**.

Jolokia (also exposed) provides a REST API to all JMX operations, compounding this risk.

**Recommended fix:**
```properties
hawtio.authenticationEnabled=true
# Configure JAAS or Spring Security for Hawtio
```

---

### Finding A-02 — HIGH: ActiveMQ Broker Binds to All Interfaces Without Authentication

**File:** `activemq/src/main/resources/application.properties`  
**Property:** `activemq.broker-url=tcp://0.0.0.0:61616`  
**Severity:** HIGH

The broker listens on `0.0.0.0` (all network interfaces) with no username/password configured. Any host that can reach port 61616 can:
- Connect to the broker
- Publish messages to any queue/topic
- Consume messages from any queue/topic (including `visitCreated`)

**Recommended fix:**
```properties
activemq.broker-url=tcp://127.0.0.1:61616
spring.activemq.user=petclinic
spring.activemq.password=${ACTIVEMQ_PASSWORD}
```

---

### Finding A-03 — MEDIUM: Jolokia Exposed Without Authentication

**File:** `activemq/src/main/resources/application.properties`  
**Property:** `management.endpoints.web.exposure.include=hawtio,jolokia`  
**Severity:** MEDIUM (compounded by Finding A-01)

Jolokia provides a REST-to-JMX bridge. Combined with disabled Hawtio authentication, this creates an unauthenticated JMX attack surface accessible over HTTP.

---

### Finding M-03 — LOW: No TLS/HTTPS Configured on Either Service

**Severity:** LOW (acceptable for local development; unacceptable for any network-exposed deployment)

Neither service configures `server.ssl.*` properties. All traffic (including actuator endpoints and Hawtio) is transmitted in plaintext HTTP.

---

### Finding M-04 — INFORMATIONAL: ActiveMQ Connection Uses Plaintext TCP

**File:** `monolith/src/main/resources/application.properties`  
**Property:** `spring.activemq.broker-url=tcp://localhost:61616`

The monolith connects to the broker over plaintext TCP with no credentials. If the broker were network-accessible, message content would be visible to network observers.

---

## 8. Recommendations and Remediation Actions

### Immediate Actions (Before Any Production Deployment)

| Priority | Finding | Recommended Action |
|---|---|---|
| 🔴 CRITICAL | A-01: Hawtio auth disabled | Set `hawtio.authenticationEnabled=true` and configure JAAS/Spring Security |
| 🔴 HIGH | A-02: Broker on all interfaces, no auth | Bind to `127.0.0.1`, add `spring.activemq.user/password` via environment variables |
| 🔴 HIGH | M-02: Actuator wildcard exposure | Restrict to `health,info`; add Spring Security to actuator endpoints |
| 🟡 MEDIUM | M-01: DROP TABLE in schema.sql | Set `spring.sql.init.mode=never` before any persistent DB migration; remove DROP statements |
| 🟡 MEDIUM | A-03: Jolokia unauthenticated | Remove Jolokia from exposure or add authentication |
| 🟢 LOW | M-03/M-04: No TLS | Add `server.ssl.*` configuration for any network-exposed deployment |

### Credential Rotation Status

> **No hardcoded credentials were found in either properties file. No rotation is required at this time.**

The absence of credentials is partially positive (no secrets committed to the repository) but also means services are running without authentication where authentication is expected (broker, Hawtio console).

### Environment Variable Pattern for Secrets

When credentials are added, they must use environment variable substitution, never hardcoded values:

```properties
# CORRECT — use environment variable references
spring.datasource.password=${DB_PASSWORD}
spring.activemq.password=${ACTIVEMQ_PASSWORD}

# WRONG — never hardcode secrets
spring.datasource.password=mysecretpassword
```

### Schema Initialization Safety Checklist

Before migrating from embedded HSQLDB to any persistent database:

- [ ] Set `spring.sql.init.mode=never` (or remove `spring.datasource.schema/data` properties)
- [ ] Remove `DROP TABLE` statements from `schema.sql` or use a migration tool (Flyway/Liquibase)
- [ ] Verify `spring.jpa.hibernate.ddl-auto=none` remains set (currently correct)
- [ ] Set `spring.datasource.url`, `username`, and `password` via environment variables
- [ ] Configure HikariCP pool size appropriate for the target database

---

## Appendix: Raw File Contents for Reference

### monolith/src/main/resources/application.properties (verbatim)

```
# Database
spring.datasource.schema=classpath*:db/schema.sql
spring.datasource.data=classpath*:db/data.sql

# Web
spring.thymeleaf.mode=HTML

# JPA
spring.jpa.hibernate.ddl-auto=none

# ActiveMQ
spring.activemq.broker-url=tcp://localhost:61616

# Internationalization
spring.messages.basename=messages/messages

# Actuator / Management
management.endpoints.web.base-path=/manage
management.endpoints.web.exposure.include=*

# Logging
logging.level.org.springframework=INFO
# logging.level.org.springframework.web=DEBUG
# logging.level.org.springframework.context.annotation=TRACE

# Server endpoint
server.port= 8080
```

### activemq/src/main/resources/application.properties (verbatim)

```
# ActiveMQ
activemq.broker-url=tcp://0.0.0.0:61616
spring.jms.template.default-destination=visitCreated

# Actuator / Management
management.endpoints.web.base-path=/
management.endpoints.web.exposure.include=hawtio,jolokia
hawtio.authenticationEnabled=false

# Logging
logging.level.org.springframework=INFO
# logging.level.org.springframework.web=DEBUG
# logging.level.org.springframework.context.annotation=TRACE

# Server endpoint
server.port= 8081
```

---

*End of audit. This document should be reviewed and updated whenever either properties file is modified.*
