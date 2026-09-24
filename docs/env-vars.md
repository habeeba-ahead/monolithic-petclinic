# Environment Variables

This document lists every environment variable used by the Pet Clinic application, its purpose, and its default value. All variables follow the `${VAR_NAME:default_value}` Spring Boot placeholder pattern, so the application starts successfully with no environment variables set (using the documented defaults).

---

## Monolith (`monolith/`)

| Variable | Purpose | Default Value |
|---|---|---|
| `DB_URL` | JDBC URL for the application datasource | `jdbc:hsqldb:mem:petclinic` |
| `DB_USERNAME` | Database username | `sa` |
| `DB_PASSWORD` | Database password | *(empty string)* |
| `ACTIVEMQ_BROKER_URL` | ActiveMQ broker connection URL used by the monolith JMS client | `tcp://localhost:61616` |
| `SERVER_PORT` | HTTP port the monolith listens on | `8080` |

### Details

- **`DB_URL`** — Full JDBC connection string. The default uses an in-memory HSQLDB instance (`jdbc:hsqldb:mem:petclinic`) so no external database is required for local development. Override with a real JDBC URL (e.g., `jdbc:mysql://db-host:3306/petclinic`) when running against an external database.
- **`DB_USERNAME`** — Username for the datasource connection. The HSQLDB default user is `sa`.
- **`DB_PASSWORD`** — Password for the datasource connection. HSQLDB requires no password by default (empty string).
- **`ACTIVEMQ_BROKER_URL`** — TCP URL of the ActiveMQ broker the monolith connects to as a JMS client. Defaults to `tcp://localhost:61616` for local development where the broker runs on the same host.
- **`SERVER_PORT`** — The port on which the embedded Tomcat server listens. Defaults to `8080`.

---

## ActiveMQ Broker (`activemq/`)

| Variable | Purpose | Default Value |
|---|---|---|
| `ACTIVEMQ_BROKER_URL` | Transport connector URL the embedded ActiveMQ broker binds to | `tcp://0.0.0.0:61616` |
| `SERVER_PORT` | HTTP port the ActiveMQ management/actuator server listens on | `8081` |

### Details

- **`ACTIVEMQ_BROKER_URL`** — The URL the embedded ActiveMQ broker uses to accept incoming connections. Defaults to `tcp://0.0.0.0:61616` (all interfaces, port 61616). In a containerised environment, set this to the appropriate bind address.
- **`SERVER_PORT`** — The port on which the embedded Tomcat server (hosting Hawtio and Actuator endpoints) listens. Defaults to `8081` to avoid conflicting with the monolith on `8080`.

---

## Quick-start (local development)

No environment variables need to be set. Run each module with its defaults:

```bash
# Terminal 1 – start the ActiveMQ broker
./mvnw spring-boot:run -pl activemq

# Terminal 2 – start the monolith
./mvnw spring-boot:run -pl monolith
```

## Container / Production overrides example

```bash
export DB_URL=jdbc:mysql://db:3306/petclinic
export DB_USERNAME=petclinic
export DB_PASSWORD=s3cr3t
export ACTIVEMQ_BROKER_URL=tcp://activemq:61616
export SERVER_PORT=8080
```
