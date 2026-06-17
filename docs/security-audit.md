# Security Audit — application.properties Inventory

**Date:** 2025-01-01  
**Scope:** Hardcoded secrets, credentials, JDBC URLs, broker URLs, and API keys  
**Files audited:**
- `monolith/src/main/resources/application.properties`
- `activemq/src/main/resources/application.properties`

---

## Grep Command Used to Detect Sensitive Properties

The following command was run to identify any properties containing passwords, secrets,
credentials, or API keys:

```
grep -rn 'password\|secret\|credential\|api.key' \
  monolith/src/main/resources/application.properties \
  activemq/src/main/resources/application.properties
```

**Result:** Zero matches — no lines containing `password`, `secret`, `credential`, or `api.key`
were found in either file. No hardcoded secrets of those types are present.

---

## File 1: `monolith/src/main/resources/application.properties`

Complete property key inventory (all active, non-commented lines):

| # | Property Key | Value | Security Flag |
|---|---|---|---|
| 1 | `spring.datasource.schema` | `classpath*:db/schema.sql` | None |
| 2 | `spring.datasource.data` | `classpath*:db/data.sql` | None |
| 3 | `spring.thymeleaf.mode` | `HTML` | None |
| 4 | `spring.jpa.hibernate.ddl-auto` | `none` | None |
| 5 | `spring.activemq.broker-url` | `tcp://localhost:61616` | ⚠ See note below |
| 6 | `spring.messages.basename` | `messages/messages` | None |
| 7 | `management.endpoints.web.base-path` | `/manage` | None |
| 8 | `management.endpoints.web.exposure.include` | `*` | ⚠ See note below |
| 9 | `logging.level.org.springframework` | `INFO` | None |
| 10 | `server.port` | `8080` | None |

### Commented-out / inactive keys (monolith)

- `# logging.level.org.springframework.web=DEBUG`
- `# logging.level.org.springframework.context.annotation=TRACE`

### Security Notes — Monolith

**`spring.activemq.broker-url = tcp://localhost:61616`**  
The broker URL is hardcoded to `tcp://localhost:61616`. This is not a credential, but it is an
environment-specific value that should be externalized via an environment variable or config
server before deploying to non-local environments.
<!-- TODO: Externalize spring.activemq.broker-url to an environment variable (e.g. ACTIVEMQ_BROKER_URL) -->

**`management.endpoints.web.exposure.include = *`**  
All actuator endpoints are exposed. This is a security risk in production environments because
it exposes heap dumps, environment variables, and other sensitive runtime data.
<!-- TODO: Restrict management.endpoints.web.exposure.include to only required endpoints (e.g. health,info) in production -->

---

## File 2: `activemq/src/main/resources/application.properties`

Complete property key inventory (all active, non-commented lines):

| # | Property Key | Value | Security Flag |
|---|---|---|---|
| 1 | `activemq.broker-url` | `tcp://0.0.0.0:61616` | ⚠ See note below |
| 2 | `spring.jms.template.default-destination` | `visitCreated` | None |
| 3 | `management.endpoints.web.base-path` | `/` | None |
| 4 | `management.endpoints.web.exposure.include` | `hawtio,jolokia` | ⚠ See note below |
| 5 | `hawtio.authenticationEnabled` | `false` | ⚠ See note below |
| 6 | `logging.level.org.springframework` | `INFO` | None |
| 7 | `server.port` | `8081` | None |

### Commented-out / inactive keys (activemq)

- `# logging.level.org.springframework.web=DEBUG`
- `# logging.level.org.springframework.context.annotation=TRACE`

### Security Notes — ActiveMQ

**`activemq.broker-url = tcp://0.0.0.0:61616`**  
The broker is bound to all interfaces (`0.0.0.0`). This is an environment-specific value that
should be externalized and restricted to the appropriate network interface in production.
<!-- TODO: Externalize activemq.broker-url to an environment variable (e.g. ACTIVEMQ_BROKER_URL) and restrict the bind address -->

**`management.endpoints.web.exposure.include = hawtio,jolokia`**  
Hawtio and Jolokia are exposed via the management endpoint. Jolokia in particular provides a
REST-to-JMX bridge that can expose sensitive JVM internals. Access must be secured.
<!-- TODO: Secure or disable Jolokia and Hawtio endpoints before production deployment -->

**`hawtio.authenticationEnabled = false`**  
Authentication for the Hawtio management console is explicitly disabled. This means anyone
with network access to port 8081 can access the full Hawtio UI without credentials.
<!-- TODO: Set hawtio.authenticationEnabled=true and configure credentials via environment variables, not in source -->

---

## Flagged Items Summary

The following properties are flagged for remediation. None contain literal passwords, secrets,
or API keys (the grep command above returned zero matches), but they represent security
misconfigurations or hardcoded environment-specific values that must be addressed before
production deployment.

| File | Property Key | Issue | Priority |
|---|---|---|---|
| `monolith/src/main/resources/application.properties` | `spring.activemq.broker-url` | Hardcoded broker URL; environment-specific | Medium |
| `monolith/src/main/resources/application.properties` | `management.endpoints.web.exposure.include` | All actuator endpoints exposed (`*`) | High |
| `activemq/src/main/resources/application.properties` | `activemq.broker-url` | Hardcoded broker URL bound to all interfaces | Medium |
| `activemq/src/main/resources/application.properties` | `management.endpoints.web.exposure.include` | Hawtio and Jolokia exposed | High |
| `activemq/src/main/resources/application.properties` | `hawtio.authenticationEnabled` | Authentication disabled (`false`) | **Critical** |

---

## Credential Rotation Assessment

The grep scan for `password`, `secret`, `credential`, and `api.key` returned **zero matches**.
No credentials requiring immediate rotation were found hardcoded in either properties file.

However, the following risks remain:

1. **Hawtio authentication is disabled** — any user on the network can access the management
   console. This must be fixed before any network-accessible deployment.
2. **All actuator endpoints are exposed** in the monolith — environment variables visible via
   `/manage/env` could leak secrets injected at runtime.
3. **No datasource username/password** is configured in `application.properties` — this implies
   the application is using an embedded or default-credential datasource. The datasource
   configuration should be audited to confirm credentials are not defaulting to well-known
   values (e.g. H2 `sa` with empty password).

---

## TODO Checklist

- [ ] **TODO:** Externalize `spring.activemq.broker-url` to an environment variable (`ACTIVEMQ_BROKER_URL`) in `monolith/src/main/resources/application.properties`
- [ ] **TODO:** Restrict `management.endpoints.web.exposure.include` to only required endpoints in `monolith/src/main/resources/application.properties`
- [ ] **TODO:** Externalize `activemq.broker-url` to an environment variable and restrict bind address in `activemq/src/main/resources/application.properties`
- [ ] **TODO:** Secure or disable Jolokia and Hawtio endpoints in `activemq/src/main/resources/application.properties`
- [ ] **TODO:** Set `hawtio.authenticationEnabled=true` and configure credentials via environment variables in `activemq/src/main/resources/application.properties`
- [ ] **TODO:** Audit datasource configuration to confirm no default/empty credentials are in use
- [ ] **TODO:** Add `spring.datasource.username` and `spring.datasource.password` as environment variable references (e.g. `${DB_USERNAME}`, `${DB_PASSWORD}`) once a real datasource is configured

---

*This document was produced as a prerequisite for Step 10 (configuration externalization).
It must be reviewed and the TODO items resolved before production deployment.*
