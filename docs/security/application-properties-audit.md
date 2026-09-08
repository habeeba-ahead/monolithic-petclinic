# Application Properties Security Audit

**Date:** 2024 (Step 13 – pre-migration security gate)  
**Status:** COMPLETE – no hardcoded credentials found; no git-filter-repo action required  
**Reviewer:** Automated audit (coding agent)

---

## 1. Files Reviewed

| File | Module |
|------|--------|
| `monolith/src/main/resources/application.properties` | Spring Boot monolith |
| `activemq/src/main/resources/application.properties` | ActiveMQ broker sidecar |

---

## 2. File Contents

### 2.1 `monolith/src/main/resources/application.properties`

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

### 2.2 `activemq/src/main/resources/application.properties`

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

---

## 3. Git History Review

Command executed:

```bash
git log --all --full-history -- '**/application.properties'
```

**Output:**

```
commit 804f1e073b3159f57f830bb4f8a333072c1dea85
Author: Gregor Riegler <gregor.riegler@ebcont.com>
Date:   Mon Apr 18 19:56:39 2022 +0200

    add MockMvcDemoTest
```

**Finding:** Only a single commit in the entire git history touches these files.
The commit message ("add MockMvcDemoTest") is unrelated to the properties files,
indicating they were committed as part of the initial repository snapshot.
There are **no additional historical revisions** of either file to inspect.
No secrets were introduced and later removed in a prior commit.

---

## 4. Hardcoded Credential Assessment

A grep for common secret patterns (`password`, `secret`, `credential`, `passwd`,
`api.key`, `apikey`, `token`) across both files returned **zero matches**.

| Pattern searched | Matches |
|-----------------|---------|
| `password` | 0 |
| `secret` | 0 |
| `credential` | 0 |
| `passwd` | 0 |
| `api.key` / `apikey` | 0 |
| `token` | 0 |

**Conclusion:** No hardcoded credentials or secrets are present in either file.
**No credential rotation is required at this time.**
**No `git-filter-repo` purge is required.**

### Notable non-secret observations

- `hawtio.authenticationEnabled=false` in the ActiveMQ module disables Hawtio
  console authentication. This is a **security misconfiguration** that must be
  remediated before any deployment to a non-local environment (set to `true` and
  configure credentials via environment variables or a secrets manager).
- `management.endpoints.web.exposure.include=*` in the monolith exposes all
  Actuator endpoints. Restrict this to only required endpoints in production.
- `management.endpoints.web.exposure.include=hawtio,jolokia` in the ActiveMQ
  module exposes the Jolokia JMX-over-HTTP endpoint. Restrict or remove in
  production.

---

## 5. Environment-Specific Values (for Step 14)

The following values are environment-specific and **must be externalised** via
environment variables, Kubernetes Secrets, or a secrets manager before the
application is deployed to any non-local environment.

| Property key | Current hardcoded value | Module | Notes |
|---|---|---|---|
| `spring.activemq.broker-url` | `tcp://localhost:61616` | monolith | Used by Spring's auto-configured JMS client to connect to the broker |
| `activemq.broker-url` | `tcp://0.0.0.0:61616` | activemq | Injected via `@Value("${activemq.broker-url}")` in `JmsConfig.java`; sets the broker's listen address |
| `spring.jms.template.default-destination` | `visitCreated` | activemq | JMS queue/topic name; may differ per environment |
| `server.port` | `8080` | monolith | HTTP listen port |
| `server.port` | `8081` | activemq | HTTP listen port |
| `management.endpoints.web.base-path` | `/manage` | monolith | Actuator base path |
| `management.endpoints.web.base-path` | `/` | activemq | Actuator base path |
| `management.endpoints.web.exposure.include` | `*` | monolith | **Restrict in production** |
| `management.endpoints.web.exposure.include` | `hawtio,jolokia` | activemq | **Restrict in production** |
| `hawtio.authenticationEnabled` | `false` | activemq | **Must be `true` in production** |
| `spring.datasource.schema` | `classpath*:db/schema.sql` | monolith | Schema init script; production should use Flyway/Liquibase |
| `spring.datasource.data` | `classpath*:db/data.sql` | monolith | Seed data script; disable in production |
| `spring.jpa.hibernate.ddl-auto` | `none` | monolith | Correct for production; verify per environment |

### Recommended externalisation pattern (Spring Boot)

```properties
# application.properties (committed – safe defaults / placeholders)
spring.activemq.broker-url=${ACTIVEMQ_BROKER_URL:tcp://localhost:61616}
activemq.broker-url=${ACTIVEMQ_BROKER_URL:tcp://0.0.0.0:61616}
server.port=${SERVER_PORT:8080}
hawtio.authenticationEnabled=${HAWTIO_AUTH_ENABLED:true}
```

Environment variables are then injected at runtime via Kubernetes Secrets,
Docker Compose `env_file`, or a secrets manager (Vault, AWS Secrets Manager, etc.).

---

## 6. Remediation Checklist

- [ ] Enable Hawtio authentication (`hawtio.authenticationEnabled=true`) and
      supply credentials via environment variable / secret.
- [ ] Restrict Actuator exposure (`management.endpoints.web.exposure.include`)
      to only required endpoints in production profiles.
- [ ] Restrict or remove the Jolokia endpoint in production.
- [ ] Replace all hardcoded URLs and ports with `${ENV_VAR:default}` placeholders
      (see table in Section 5).
- [ ] Replace schema/data init scripts with Flyway or Liquibase migrations.
- [ ] Add `application.properties` (or environment-specific variants) to
      `.gitignore` if they ever contain real credentials in the future.
- [ ] No `git-filter-repo` action required (no secrets found in history).

---

## 7. Summary

| Item | Result |
|------|--------|
| Hardcoded passwords / secrets | **None found** |
| Credential rotation required | **No** |
| `git-filter-repo` purge required | **No** |
| Security misconfigurations found | **Yes** (Hawtio auth disabled, Actuator wildcard exposure) |
| Environment-specific values identified | **13 properties** (see Section 5) |
| Git history commits reviewed | **1** (single snapshot commit) |
