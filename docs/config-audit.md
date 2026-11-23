# Configuration Audit

**Date:** 2024-01-01  
**Scope:** All `application.properties` files in the repository  
**Files audited:**
- `monolith/src/main/resources/application.properties`
- `activemq/src/main/resources/application.properties`

---

## 1. Property Inventory

### 1.1 Monolith (`monolith/src/main/resources/application.properties`)

| Property Key | Value | Category | Risk |
|---|---|---|---|
| `spring.datasource.schema` | `classpath*:db/schema.sql` | Database | Low |
| `spring.datasource.data` | `classpath*:db/data.sql` | Database | Low |
| `spring.thymeleaf.mode` | `HTML` | Web | Low |
| `spring.jpa.hibernate.ddl-auto` | `none` | JPA | Low |
| `spring.activemq.broker-url` | `tcp://localhost:61616` | JMS / Messaging | **Medium** – hardcoded host |
| `spring.messages.basename` | `messages/messages` | i18n | Low |
| `management.endpoints.web.base-path` | `/manage` | Actuator | Low |
| `management.endpoints.web.exposure.include` | `*` | Actuator | **HIGH** – all endpoints exposed |
| `logging.level.org.springframework` | `INFO` | Logging | Low |
| `server.port` | `8080` | Server | Low |

**Datasource URL:** No explicit `spring.datasource.url` is set. Spring Boot auto-configures an
in-memory HSQLDB instance using the `hsqldb` runtime dependency. There are no hardcoded database
credentials in this file.

### 1.2 ActiveMQ Module (`activemq/src/main/resources/application.properties`)

| Property Key | Value | Category | Risk |
|---|---|---|---|
| `activemq.broker-url` | `tcp://0.0.0.0:61616` | JMS Broker | **HIGH** – binds to all interfaces |
| `spring.jms.template.default-destination` | `visitCreated` | JMS / Messaging | Low |
| `management.endpoints.web.base-path` | `/` | Actuator | Low |
| `management.endpoints.web.exposure.include` | `hawtio,jolokia` | Actuator | **Medium** – Jolokia exposes JMX over HTTP |
| `hawtio.authenticationEnabled` | `false` | Security | **CRITICAL** – auth disabled |
| `logging.level.org.springframework` | `INFO` | Logging | Low |
| `server.port` | `8081` | Server | Low |

**JMS Broker URL:** `tcp://0.0.0.0:61616` — the broker binds to all network interfaces with no
authentication configured.

---

## 2. Hardcoded Credentials & Security Findings

### 2.1 CRITICAL — Hawtio Authentication Disabled

```properties
# activemq/src/main/resources/application.properties
# TODO: hawtio.authenticationEnabled=false disables all Hawtio UI authentication
#       MUST be enabled or removed before production; inject credentials via Secret Manager
hawtio.authenticationEnabled=false
```

**Risk:** The Hawtio management console is accessible to anyone who can reach port 8081.
**Remediation:** Set `hawtio.authenticationEnabled=true` and supply credentials via
Google Cloud Secret Manager (e.g., `${sm://projects/PROJECT/secrets/hawtio-password}`).

### 2.2 HIGH — Actuator Wildcard Exposure

```properties
# monolith/src/main/resources/application.properties
# TODO: exposure.include=* exposes ALL actuator endpoints - restrict before production
management.endpoints.web.exposure.include=*
```

**Risk:** Endpoints such as `/manage/env`, `/manage/heapdump`, `/manage/shutdown` are publicly
reachable, leaking environment variables and potentially allowing remote shutdown.
**Remediation:** Restrict to a minimal allow-list, e.g. `health,info,metrics`.

### 2.3 MEDIUM — Hardcoded ActiveMQ Broker URLs

```properties
# monolith/src/main/resources/application.properties
# TODO: spring.activemq.broker-url should be injected via Secret Manager / env var, not hardcoded
spring.activemq.broker-url=tcp://localhost:61616

# activemq/src/main/resources/application.properties
# TODO: activemq.broker-url should be injected via Secret Manager / env var, not hardcoded
activemq.broker-url=tcp://0.0.0.0:61616
```

**Risk:** Broker host/port are baked into source control. Any rotation or environment change
requires a code change.
**Remediation:** Replace with environment variable references:
```properties
# TODO: inject via Secret Manager
spring.activemq.broker-url=${ACTIVEMQ_BROKER_URL:tcp://localhost:61616}
```

### 2.4 MEDIUM — No Datasource Credentials (HSQLDB in-memory)

No `spring.datasource.url`, `spring.datasource.username`, or `spring.datasource.password` keys
are present. The monolith currently uses an **in-memory HSQLDB** database (auto-configured by
Spring Boot). All data is lost on restart.

**Remediation:** When migrating to Cloud SQL, inject credentials via Secret Manager:
```properties
# TODO: inject via Secret Manager
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
```

### 2.5 MEDIUM — Jolokia JMX-over-HTTP Exposed

```properties
# activemq/src/main/resources/application.properties
management.endpoints.web.exposure.include=hawtio,jolokia
```

**Risk:** Jolokia exposes the full JMX API over HTTP, allowing remote invocation of MBeans.
**Remediation:** Remove `jolokia` from the exposure list unless explicitly required, and add
network-level access controls.

---

## 3. JMS Usage Scan

**Command run:**
```bash
grep -r '@JmsListener\|JmsTemplate\|MessageConverter' monolith/src/
```

**Result:** `NO_MATCHES` — no `@JmsListener`, `JmsTemplate`, or `MessageConverter` usages found
in `monolith/src/`.

**Additional finding:**
```bash
grep -r 'JmsListener\|JmsTemplate\|MessageConverter\|@EnableJms' monolith/src/main/java/
```

`monolith/src/main/java/org/springframework/samples/petclinic/Application.java` contains:
```java
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@SpringBootApplication(proxyBeanMethods = false)
public class Application { ... }
```

**Interpretation:** The monolith has `@EnableJms` declared on the main application class and the
`spring-boot-starter-activemq` dependency in `pom.xml`, but **no `@JmsListener` consumers,
no `JmsTemplate` producers, and no `MessageConverter` beans exist in the source tree**.
The JMS infrastructure is wired up but unused at the application code level — the monolith
likely *sends* messages via a direct `JmsTemplate` call that has not yet been implemented, or
the feature is scaffolded for future use.

**Impact on ActiveMQ-to-Pub/Sub migration effort:**
- No listener migration work is required in the monolith (zero `@JmsListener` methods).
- No `JmsTemplate` send-side calls need to be replaced in the monolith.
- The `@EnableJms` annotation and `spring-boot-starter-activemq` dependency can be removed
  from the monolith once the ActiveMQ broker module is decommissioned.
- The `activemq` module itself acts as a standalone broker; its migration scope is limited to
  replacing the embedded broker with a Pub/Sub publisher/subscriber adapter.

**Estimated migration effort reduction:** Low — the absence of JMS usage in the monolith
significantly reduces the migration surface area.

---

## 4. Summary Table

| Finding | Severity | File | Remediation |
|---|---|---|---|
| `hawtio.authenticationEnabled=false` | CRITICAL | activemq props | Enable auth; inject creds via Secret Manager |
| `management.endpoints.web.exposure.include=*` | HIGH | monolith props | Restrict to `health,info,metrics` |
| `activemq.broker-url` hardcoded | MEDIUM | both props | Inject via Secret Manager / env var |
| No datasource URL (HSQLDB in-memory) | MEDIUM | monolith props | Migrate to Cloud SQL; inject via Secret Manager |
| Jolokia exposed over HTTP | MEDIUM | activemq props | Remove from exposure list |
| `@EnableJms` with no listeners/producers | INFO | monolith Application.java | Remove after broker decommission |

---

## 5. Next Steps

1. **Immediate:** Disable Hawtio unauthenticated access (`hawtio.authenticationEnabled=true`).
2. **Before production:** Restrict Actuator exposure in the monolith to a minimal allow-list.
3. **Secret Manager integration:** Replace all hardcoded URLs and credentials with Secret Manager
   references using the Spring Cloud GCP Secret Manager starter.
4. **Database migration:** Replace HSQLDB with Cloud SQL (PostgreSQL); inject connection string
   and credentials via Secret Manager.
5. **JMS migration:** The monolith requires only dependency cleanup; the `activemq` module broker
   needs a Pub/Sub adapter implementation.
