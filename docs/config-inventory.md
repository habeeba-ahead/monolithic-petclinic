# Configuration Inventory

> **Purpose:** Catalogue every property key from both `application.properties` files, document current values, flag environment-specific settings, and identify credentials requiring Secret Manager migration.
>
> **Sources audited:**
> - `monolith/src/main/resources/application.properties`
> - `activemq/src/main/resources/application.properties`
>
> **Legend**
>
> | Symbol | Meaning |
> |--------|---------|
> | ✅ Yes | The column condition applies |
> | ❌ No  | The column condition does not apply |
> | ⚠️ FLAG | Requires action before production deployment |

---

## 1. Monolith — `monolith/src/main/resources/application.properties`

| # | Property Key | Current Value | Env-Specific? | Credential? | Notes |
|---|-------------|---------------|:-------------:|:-----------:|-------|
| 1 | `spring.datasource.schema` | `classpath*:db/schema.sql` | ❌ No | ❌ No | Classpath reference; safe for all environments |
| 2 | `spring.datasource.data` | `classpath*:db/data.sql` | ❌ No | ❌ No | Classpath reference; safe for all environments |
| 3 | `spring.thymeleaf.mode` | `HTML` | ❌ No | ❌ No | Template engine mode; not environment-specific |
| 4 | `spring.jpa.hibernate.ddl-auto` | `none` | ❌ No | ❌ No | DDL management disabled; appropriate for production |
| 5 | `spring.activemq.broker-url` | `tcp://localhost:61616` | ✅ Yes ⚠️ FLAG | ❌ No | **localhost assumption** — hardcoded to local broker; must be replaced with the actual broker hostname/IP per environment (dev/staging/prod) |
| 6 | `spring.messages.basename` | `messages/messages` | ❌ No | ❌ No | i18n message bundle path; not environment-specific |
| 7 | `management.endpoints.web.base-path` | `/manage` | ✅ Yes | ❌ No | Management endpoint base path; may differ per environment or be restricted |
| 8 | `management.endpoints.web.exposure.include` | `*` | ✅ Yes ⚠️ FLAG | ❌ No | Exposes **all** actuator endpoints; should be restricted in production |
| 9 | `logging.level.org.springframework` | `INFO` | ✅ Yes | ❌ No | Log verbosity; typically differs between dev (`DEBUG`) and prod (`WARN`/`ERROR`) |
| 10 | `server.port` | `8080` | ✅ Yes | ❌ No | HTTP listen port; environment-specific in containerised/cloud deployments |

### Monolith — Commented-out / Inactive Properties

The following keys are present but commented out. They are documented here for completeness:

| Property Key | Commented Value | Purpose |
|-------------|-----------------|---------|
| `logging.level.org.springframework.web` | `DEBUG` | Verbose web-layer logging (dev only) |
| `logging.level.org.springframework.context.annotation` | `TRACE` | Trace-level context logging (dev only) |

---

## 2. ActiveMQ Broker — `activemq/src/main/resources/application.properties`

| # | Property Key | Current Value | Env-Specific? | Credential? | Notes |
|---|-------------|---------------|:-------------:|:-----------:|-------|
| 1 | `activemq.broker-url` | `tcp://0.0.0.0:61616` | ✅ Yes ⚠️ FLAG | ❌ No | **Bind-all (0.0.0.0) assumption** — binds the broker to every network interface; environment-specific and potentially insecure in production; should be restricted to a specific interface or use a managed broker (e.g., Amazon MQ) |
| 2 | `spring.jms.template.default-destination` | `visitCreated` | ✅ Yes | ❌ No | JMS destination name; should match the queue/topic name configured in the target broker per environment |
| 3 | `management.endpoints.web.base-path` | `/` | ✅ Yes | ❌ No | Management endpoint base path set to root; differs from monolith; may conflict with application routes |
| 4 | `management.endpoints.web.exposure.include` | `hawtio,jolokia` | ✅ Yes ⚠️ FLAG | ❌ No | Exposes Hawtio and Jolokia management consoles; these provide deep JVM/broker access and must be secured or disabled in production |
| 5 | `hawtio.authenticationEnabled` | `false` | ✅ Yes ⚠️ FLAG | ✅ Yes ⚠️ FLAG | **Authentication disabled** — Hawtio console is accessible without credentials; this is a **critical security misconfiguration**; must be enabled and credentials stored in Secret Manager before any non-local deployment |
| 6 | `logging.level.org.springframework` | `INFO` | ✅ Yes | ❌ No | Log verbosity; typically differs between dev and prod |
| 7 | `server.port` | `8081` | ✅ Yes | ❌ No | HTTP listen port for the ActiveMQ broker service; environment-specific in containerised/cloud deployments |

### ActiveMQ — Commented-out / Inactive Properties

| Property Key | Commented Value | Purpose |
|-------------|-----------------|---------|
| `logging.level.org.springframework.web` | `DEBUG` | Verbose web-layer logging (dev only) |
| `logging.level.org.springframework.context.annotation` | `TRACE` | Trace-level context logging (dev only) |

---

## 3. Cross-Cutting Findings

### 3.1 localhost Assumptions ⚠️

The following values hardcode `localhost`, which will break in any multi-host, containerised, or cloud environment:

| Source File | Property Key | Hardcoded Value |
|-------------|-------------|-----------------|
| `monolith/src/main/resources/application.properties` | `spring.activemq.broker-url` | `tcp://localhost:61616` |

**Required action:** Replace with an environment variable or externalized config (e.g., `${ACTIVEMQ_BROKER_URL:tcp://localhost:61616}`) so that each deployment environment can supply the correct broker address.

### 3.2 Bind-All Address Assumptions ⚠️

The following values use `0.0.0.0`, binding to all network interfaces:

| Source File | Property Key | Hardcoded Value |
|-------------|-------------|-----------------|
| `activemq/src/main/resources/application.properties` | `activemq.broker-url` | `tcp://0.0.0.0:61616` |

**Required action:** Restrict to a specific interface in production, or migrate to a managed broker service (e.g., Amazon MQ) and remove this property entirely.

### 3.3 Hardcoded Credentials — Secret Manager Migration Required ⚠️

| Source File | Property Key | Current Value | Risk | Required Action |
|-------------|-------------|---------------|------|-----------------|
| `activemq/src/main/resources/application.properties` | `hawtio.authenticationEnabled` | `false` | **CRITICAL** — Hawtio admin console is open to unauthenticated access | Enable authentication (`true`), store Hawtio credentials in **Secret Manager**, and inject at runtime via environment variables |

> **Note:** No username/password credentials were found hardcoded in plain text in either file. However, the disabled Hawtio authentication (`hawtio.authenticationEnabled=false`) is treated as a credential-class finding because it removes the authentication gate entirely. Any production deployment must enable this and manage the resulting credentials through Secret Manager.

### 3.4 Actuator Exposure ⚠️

| Source File | Property Key | Current Value | Risk |
|-------------|-------------|---------------|------|
| `monolith/src/main/resources/application.properties` | `management.endpoints.web.exposure.include` | `*` | All actuator endpoints exposed (including `/manage/env`, `/manage/heapdump`, `/manage/shutdown`) |
| `activemq/src/main/resources/application.properties` | `management.endpoints.web.exposure.include` | `hawtio,jolokia` | Jolokia provides full JMX-over-HTTP access; Hawtio provides a full broker management UI |

---

## 4. Summary Statistics

| Category | Count |
|----------|-------|
| Total property keys (monolith) | 10 active + 2 commented |
| Total property keys (activemq) | 7 active + 2 commented |
| Environment-specific properties | 9 |
| localhost assumptions flagged | 1 |
| Bind-all (0.0.0.0) assumptions flagged | 1 |
| Credential / security findings | 1 (hawtio auth disabled) |
| Properties requiring Secret Manager migration | 1 |

---

## 5. Recommended Next Steps (Configuration Externalization — Step 2)

1. **Externalize all environment-specific values** using Spring Boot's `${ENV_VAR:default}` placeholder syntax so that `application.properties` contains only safe defaults and all environment-specific values are injected at runtime.
2. **Enable Hawtio authentication** and store credentials in Secret Manager (e.g., AWS Secrets Manager or GCP Secret Manager); inject via environment variables at container startup.
3. **Restrict actuator exposure** in the monolith: replace `*` with an explicit allowlist appropriate for production (e.g., `health,info,metrics`).
4. **Replace the self-hosted ActiveMQ broker** with a managed service (Amazon MQ / Google Cloud Pub/Sub) to eliminate the `0.0.0.0` bind-all concern and the need to manage broker credentials manually.
5. **Add a datasource URL property** to the monolith — currently absent from `application.properties`, which means the application relies on Spring Boot auto-configuration defaults or an in-memory database. The actual JDBC URL, username, and password must be explicitly configured and stored in Secret Manager for production.
