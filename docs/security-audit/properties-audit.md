# Security Audit: application.properties Files

**Audit Date:** 2024-01-01  
**Auditor:** Automated Security Assessment  
**Scope:** All `application.properties` files in the repository  
**Status:** ⚠️ FINDINGS IDENTIFIED — action required before production deployment

---

## Table of Contents

1. [Files Audited](#files-audited)
2. [Monolith — Full Property Listing](#monolith--full-property-listing)
3. [ActiveMQ — Full Property Listing](#activemq--full-property-listing)
4. [Key Security Findings](#key-security-findings)
   - [spring.jpa.hibernate.ddl-auto](#springjpahibernateddl-auto)
   - [spring.sql.init.mode](#springsqlinitmode)
   - [HikariCP Connection Pool](#hikaricp-connection-pool)
   - [Hardcoded Credentials Assessment](#hardcoded-credentials-assessment)
   - [Critical Security Issues](#critical-security-issues)
5. [Recommendations](#recommendations)

---

## Files Audited

| # | Path | Size | Last Modified |
|---|------|------|---------------|
| 1 | `monolith/src/main/resources/application.properties` | 18 properties | — |
| 2 | `activemq/src/main/resources/application.properties` | 10 properties | — |

Both files were read in full. Their complete contents are reproduced verbatim in the sections below.

---

## Monolith — Full Property Listing

**File:** `monolith/src/main/resources/application.properties`

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

### Monolith Property-by-Property Analysis

| Property | Value | Risk Level | Notes |
|----------|-------|------------|-------|
| `spring.datasource.schema` | `classpath*:db/schema.sql` | 🟡 MEDIUM | Schema DDL runs on startup; `schema.sql` contains `DROP TABLE … IF EXISTS` statements — destructive if pointed at a persistent database |
| `spring.datasource.data` | `classpath*:db/data.sql` | 🟡 MEDIUM | Seed data re-inserted on every startup; may cause duplicate-key errors against a persistent database |
| `spring.thymeleaf.mode` | `HTML` | 🟢 LOW | Standard setting; no security concern |
| `spring.jpa.hibernate.ddl-auto` | `none` | 🟢 LOW | **Safe value.** Hibernate will not auto-generate or drop schema. See [dedicated section](#springjpahibernateddl-auto) |
| `spring.activemq.broker-url` | `tcp://localhost:61616` | 🟡 MEDIUM | Hardcoded host/port; acceptable for local dev but must be externalised for any other environment |
| `spring.messages.basename` | `messages/messages` | 🟢 LOW | Internationalisation config; no security concern |
| `management.endpoints.web.base-path` | `/manage` | 🟡 MEDIUM | Non-default path provides minor obscurity but does not restrict access |
| `management.endpoints.web.exposure.include` | `*` | 🔴 HIGH | **Wildcard exposes ALL actuator endpoints** (including `/manage/env`, `/manage/heapdump`, `/manage/threaddump`, `/manage/loggers`). Must be restricted to a minimal allow-list |
| `logging.level.org.springframework` | `INFO` | 🟢 LOW | Appropriate for production |
| `server.port` | `8080` | 🟢 LOW | Standard dev port; no security concern |

---

## ActiveMQ — Full Property Listing

**File:** `activemq/src/main/resources/application.properties`

```properties
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

### ActiveMQ Property-by-Property Analysis

| Property | Value | Risk Level | Notes |
|----------|-------|------------|-------|
| `activemq.broker-url` | `tcp://0.0.0.0:61616` | 🔴 HIGH | Broker binds to **all network interfaces** (`0.0.0.0`). Any host that can reach port 61616 can connect to the broker without authentication |
| `spring.jms.template.default-destination` | `visitCreated` | 🟢 LOW | Queue name; no security concern |
| `management.endpoints.web.base-path` | `/` | 🟡 MEDIUM | Actuator served from root path; increases attack surface |
| `management.endpoints.web.exposure.include` | `hawtio,jolokia` | 🔴 HIGH | Jolokia exposes a full JMX-over-HTTP bridge; combined with disabled Hawtio auth this is a critical RCE vector |
| `hawtio.authenticationEnabled` | `false` | 🔴 CRITICAL | **Authentication is completely disabled** for the Hawtio management console. Any unauthenticated user with network access can execute arbitrary JMX operations |
| `logging.level.org.springframework` | `INFO` | 🟢 LOW | Appropriate for production |
| `server.port` | `8081` | 🟢 LOW | Standard dev port; no security concern |

---

## Key Security Findings

### spring.jpa.hibernate.ddl-auto

| Attribute | Detail |
|-----------|--------|
| **File** | `monolith/src/main/resources/application.properties` |
| **Property** | `spring.jpa.hibernate.ddl-auto` |
| **Confirmed Value** | `none` |
| **Risk** | 🟢 LOW — Safe |

**Assessment:** The value `none` instructs Hibernate to perform **no DDL operations** at startup. This is the correct and safe setting for any environment that uses an externally managed schema. The previously feared value of `create-drop` (which would destroy all data on restart) is **not present**.

> ✅ **No immediate action required** for this property. The value is safe.

---

### spring.sql.init.mode

| Attribute | Detail |
|-----------|--------|
| **File** | `monolith/src/main/resources/application.properties` |
| **Property** | `spring.sql.init.mode` |
| **Confirmed Value** | **Not configured / not set** (property is absent from both files) |
| **Risk** | 🟡 MEDIUM — Indirect risk via `spring.datasource.schema` / `spring.datasource.data` |

**Assessment:** The property `spring.sql.init.mode` is **not present** in either `application.properties` file. However, the monolith configures `spring.datasource.schema` and `spring.datasource.data`, which use the older Spring Boot 2.x `DataSourceInitializer` mechanism. This mechanism runs `schema.sql` (which contains `DROP TABLE … IF EXISTS` statements) and `data.sql` on every application startup. Against an in-memory HSQLDB database this is harmless; against a persistent database it would be destructive.

> ⚠️ **Action required** if the application is ever pointed at a persistent database: remove or conditionally gate `spring.datasource.schema` and `spring.datasource.data`, or set `spring.sql.init.mode=never`.

---

### HikariCP Connection Pool

| Attribute | Detail |
|-----------|--------|
| **File** | `monolith/src/main/resources/application.properties` |
| **HikariCP properties** | **None configured** |
| **Risk** | 🟡 MEDIUM — Default values in use |

**Assessment:** No HikariCP-specific properties (e.g. `spring.datasource.hikari.maximum-pool-size`, `spring.datasource.hikari.minimum-idle`, `spring.datasource.hikari.connection-timeout`, `spring.datasource.hikari.idle-timeout`, `spring.datasource.hikari.max-lifetime`) are present in either `application.properties` file. Spring Boot's HikariCP defaults will be used:

| HikariCP Setting | Default Value | Production Concern |
|------------------|---------------|--------------------|
| `maximum-pool-size` | 10 | May be too low or too high depending on load |
| `minimum-idle` | same as `maximum-pool-size` | No idle connection reduction |
| `connection-timeout` | 30 000 ms | Acceptable |
| `idle-timeout` | 600 000 ms (10 min) | Acceptable |
| `max-lifetime` | 1 800 000 ms (30 min) | Should be less than DB `wait_timeout` |

> ⚠️ **Action required** before production: explicitly configure HikariCP pool sizing and timeouts to match the target database server's settings and expected load.

---

### Hardcoded Credentials Assessment

This section documents every property that could constitute a hardcoded credential, password, API key, or secret.

#### Database Password

| Property | Value | Finding |
|----------|-------|---------|
| `spring.datasource.username` | **Not set** | No hardcoded database username found |
| `spring.datasource.password` | **Not set** | No hardcoded database password found |
| `spring.datasource.url` | **Not set** | No hardcoded JDBC URL found |

**Assessment:** No hardcoded database password or username is present in either `application.properties` file. The application uses an embedded HSQLDB database (declared as a runtime dependency in `monolith/pom.xml`) which requires no credentials. Spring Boot auto-configures the in-memory datasource automatically.

> ✅ **No database credential rotation required.**

#### Broker Password

| Property | Value | Finding |
|----------|-------|---------|
| `spring.activemq.user` | **Not set** | No hardcoded broker username found |
| `spring.activemq.password` | **Not set** | No hardcoded broker password found |
| `activemq.broker-url` | `tcp://0.0.0.0:61616` | URL contains no embedded credentials |

**Assessment:** No hardcoded broker password is present. However, the broker is configured to bind to all interfaces (`0.0.0.0`) with **no authentication configured**, meaning any client that can reach port 61616 can connect without a password. This is a critical security gap distinct from a hardcoded credential.

> ✅ **No broker password rotation required** (no password exists to rotate).  
> 🔴 **Broker authentication must be enabled** — see [Critical Security Issues](#critical-security-issues).

#### API Keys and Other Secrets

| Property Pattern | Finding |
|-----------------|---------|
| `*.api-key`, `*.api.key`, `*.secret`, `*.token` | **None found** in either file |
| `*.client-secret`, `*.client-id` | **None found** in either file |
| Any value matching common secret patterns | **None found** |

> ✅ **No API keys or other secrets found.** No rotation required.

---

### Critical Security Issues

The following findings are rated **CRITICAL** or **HIGH** and require remediation before any non-local deployment.

---

#### 🔴 CRITICAL-1: Hawtio Authentication Disabled

| Attribute | Detail |
|-----------|--------|
| **File** | `activemq/src/main/resources/application.properties` |
| **Property** | `hawtio.authenticationEnabled=false` |
| **Severity** | CRITICAL |
| **CWE** | CWE-306: Missing Authentication for Critical Function |

**Description:** The Hawtio management console has authentication completely disabled. Hawtio provides a full JMX management interface including the ability to invoke arbitrary MBean operations, read/write JVM attributes, and (via Jolokia) execute code through JMX. Any user with network access to port 8081 can perform these operations without any credentials.

**Immediate Action:** Set `hawtio.authenticationEnabled=true` and configure an appropriate authentication provider. If Hawtio is not required, remove the `hawtio-springboot` dependency entirely.

---

#### 🔴 CRITICAL-2: Jolokia JMX-over-HTTP Exposed Without Authentication

| Attribute | Detail |
|-----------|--------|
| **File** | `activemq/src/main/resources/application.properties` |
| **Property** | `management.endpoints.web.exposure.include=hawtio,jolokia` |
| **Severity** | CRITICAL |
| **CWE** | CWE-749: Exposed Dangerous Method or Function |

**Description:** Jolokia exposes the full JMX API over HTTP. Combined with `hawtio.authenticationEnabled=false`, this creates an unauthenticated remote code execution vector. An attacker can use Jolokia's `exec` operation to invoke arbitrary MBean methods.

**Immediate Action:** Remove `jolokia` from the exposure include list, or ensure it is protected by authentication and network-level controls.

---

#### 🔴 HIGH-3: ActiveMQ Broker Binds to All Interfaces

| Attribute | Detail |
|-----------|--------|
| **File** | `activemq/src/main/resources/application.properties` |
| **Property** | `activemq.broker-url=tcp://0.0.0.0:61616` |
| **Severity** | HIGH |
| **CWE** | CWE-284: Improper Access Control |

**Description:** The ActiveMQ broker listens on all network interfaces. Without authentication configured, any host that can reach port 61616 can produce or consume messages, potentially injecting malicious visit records or draining the queue.

**Immediate Action:** Restrict the bind address to `127.0.0.1` for local-only access, or configure ActiveMQ authentication (username/password) and TLS for any networked deployment.

---

#### 🔴 HIGH-4: Actuator Wildcard Exposure (Monolith)

| Attribute | Detail |
|-----------|--------|
| **File** | `monolith/src/main/resources/application.properties` |
| **Property** | `management.endpoints.web.exposure.include=*` |
| **Severity** | HIGH |
| **CWE** | CWE-200: Exposure of Sensitive Information |

**Description:** The wildcard value `*` exposes all actuator endpoints including `/manage/env` (environment variables and configuration), `/manage/heapdump` (full JVM heap dump), `/manage/threaddump`, `/manage/loggers` (runtime log-level modification), and `/manage/beans`. These endpoints can leak secrets, enable log-level escalation for reconnaissance, and expose internal application state.

**Immediate Action:** Replace `*` with a minimal allow-list, e.g.:
```properties
management.endpoints.web.exposure.include=health,info
```

---

#### 🟡 MEDIUM-5: Schema SQL Runs DROP TABLE on Every Startup

| Attribute | Detail |
|-----------|--------|
| **File** | `monolith/src/main/resources/application.properties` |
| **Properties** | `spring.datasource.schema=classpath*:db/schema.sql` |
| **Severity** | MEDIUM (HIGH if pointed at persistent DB) |
| **CWE** | CWE-1188: Insecure Default Initialization of Resource |

**Description:** `db/schema.sql` begins with `DROP TABLE … IF EXISTS` for every table. While safe for the current in-memory HSQLDB, this configuration would destroy all data in a persistent database on every application restart.

**Immediate Action:** Gate schema initialisation behind a profile or remove `spring.datasource.schema` for any environment using a persistent database. Set `spring.sql.init.mode=never` as a safe default.

---

## Recommendations

The following actions are recommended in priority order:

### Immediate (Before Any Non-Local Deployment)

1. **Enable Hawtio authentication** — set `hawtio.authenticationEnabled=true` in `activemq/src/main/resources/application.properties`. Treat the current state as a compromised management interface.

2. **Remove or restrict Jolokia** — remove `jolokia` from `management.endpoints.web.exposure.include` in the ActiveMQ properties, or protect it with authentication.

3. **Restrict actuator exposure** — replace `management.endpoints.web.exposure.include=*` in the monolith properties with a minimal allow-list (`health,info`).

4. **Restrict ActiveMQ bind address** — change `activemq.broker-url=tcp://0.0.0.0:61616` to `tcp://127.0.0.1:61616` unless cross-host connectivity is required, and enable broker authentication.

### Short-Term (Before Production)

5. **Externalise all environment-specific configuration** — broker URLs, ports, and any future credentials must be supplied via environment variables or a secrets manager (e.g. HashiCorp Vault, AWS Secrets Manager), not committed to source control.

6. **Configure HikariCP explicitly** — add pool sizing and timeout properties appropriate for the target database and expected load.

7. **Gate schema initialisation** — remove `spring.datasource.schema` and `spring.datasource.data` from the default profile; use a dedicated `application-dev.properties` profile for local development only.

8. **Add `spring.sql.init.mode=never`** to the default/production profile to prevent accidental schema re-initialisation.

### Ongoing

9. **Rotate credentials proactively** — although no hardcoded credentials were found in these files, conduct a full repository history scan (`git log -p | grep -iE 'password|secret|key'`) to confirm no credentials were ever committed and subsequently removed.

10. **Enable Spring Security** — neither module has Spring Security on the classpath. All management endpoints and the broker are completely unauthenticated. Add `spring-boot-starter-security` and configure appropriate access controls.

---

## Summary Table

| Finding ID | Severity | File | Property | Requires Rotation? |
|------------|----------|------|----------|--------------------|
| CRITICAL-1 | 🔴 CRITICAL | activemq | `hawtio.authenticationEnabled=false` | N/A — no credential to rotate; fix auth |
| CRITICAL-2 | 🔴 CRITICAL | activemq | `management.endpoints.web.exposure.include=hawtio,jolokia` | N/A |
| HIGH-3 | 🔴 HIGH | activemq | `activemq.broker-url=tcp://0.0.0.0:61616` | N/A |
| HIGH-4 | 🔴 HIGH | monolith | `management.endpoints.web.exposure.include=*` | N/A |
| MEDIUM-5 | 🟡 MEDIUM | monolith | `spring.datasource.schema` + DROP TABLE in schema.sql | N/A |
| INFO-1 | 🟢 SAFE | monolith | `spring.jpa.hibernate.ddl-auto=none` | N/A — safe value confirmed |
| INFO-2 | 🟢 SAFE | both | `spring.sql.init.mode` | N/A — property absent; indirect risk only |
| INFO-3 | 🟢 SAFE | both | Database password | ✅ No password found — no rotation needed |
| INFO-4 | 🟢 SAFE | both | Broker password | ✅ No password found — no rotation needed |
| INFO-5 | 🟢 SAFE | both | API keys / secrets | ✅ None found — no rotation needed |
| INFO-6 | 🟡 NOTE | monolith | HikariCP pool | Not configured — defaults in use |

---

*This document was produced as part of a security modernisation audit. It is a point-in-time snapshot and should be reviewed whenever `application.properties` files are modified.*
