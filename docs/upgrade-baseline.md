# Upgrade Baseline — Spring Boot 2.2.1.RELEASE

This document records the security and dependency baseline established before any
upgrade work begins. It is the reference point for demonstrating CVE reduction
after each upgrade step.

---

## Project Versions

| Property            | Value                  |
|---------------------|------------------------|
| Spring Boot version | **2.2.1.RELEASE**      |
| Java version        | **1.8** (Java 8)       |
| Baseline date       | 2026-09-14             |

---

## How to Reproduce

```bash
# Dependency tree (Spring Boot artifacts only)
./mvnw dependency:tree -Dincludes=org.springframework.boot

# OWASP Dependency-Check (produces HTML report per module)
./mvnw org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=0
```

Both commands must be run from the repository root with Java 8 on the PATH.

---

## Spring Boot 2.2.1 Transitive Dependency Tree

Running `./mvnw dependency:tree -Dincludes=org.springframework.boot` produces
**BUILD SUCCESS** and lists the following 18 unique Spring Boot artifacts
(version 2.2.1.RELEASE throughout):

### petclinic-activemq module

```
org.monolithic.petclinic:petclinic-activemq:jar:1.0.0-SNAPSHOT
+- io.hawt:hawtio-springboot:jar:2.8.0:compile
|  +- org.springframework.boot:spring-boot-actuator-autoconfigure:jar:2.2.1.RELEASE:compile
|  \- org.springframework.boot:spring-boot-starter-web:jar:2.2.1.RELEASE:compile
|     +- org.springframework.boot:spring-boot-starter:jar:2.2.1.RELEASE:compile
|     |  \- org.springframework.boot:spring-boot-starter-logging:jar:2.2.1.RELEASE:compile
|     +- org.springframework.boot:spring-boot-starter-json:jar:2.2.1.RELEASE:compile
|     +- org.springframework.boot:spring-boot-starter-tomcat:jar:2.2.1.RELEASE:compile
|     \- org.springframework.boot:spring-boot-starter-validation:jar:2.2.1.RELEASE:compile
+- org.springframework.boot:spring-boot-actuator:jar:2.2.1.RELEASE:compile
|  \- org.springframework.boot:spring-boot:jar:2.2.1.RELEASE:compile
\- org.springframework.boot:spring-boot-devtools:jar:2.2.1.RELEASE:compile (optional)
   \- org.springframework.boot:spring-boot-autoconfigure:jar:2.2.1.RELEASE:compile
```

### petclinic-monolith module

```
org.monolithic.petclinic:petclinic-monolith:jar:1.0.0-SNAPSHOT
+- org.springframework.boot:spring-boot-starter-data-jpa:jar:2.2.1.RELEASE:compile
|  +- org.springframework.boot:spring-boot-starter-aop:jar:2.2.1.RELEASE:compile
|  \- org.springframework.boot:spring-boot-starter-jdbc:jar:2.2.1.RELEASE:compile
+- org.springframework.boot:spring-boot-starter-activemq:jar:2.2.1.RELEASE:compile
|  \- org.springframework.boot:spring-boot-starter:jar:2.2.1.RELEASE:compile
|     \- org.springframework.boot:spring-boot-starter-logging:jar:2.2.1.RELEASE:compile
+- org.springframework.boot:spring-boot-starter-web:jar:2.2.1.RELEASE:compile
|  +- org.springframework.boot:spring-boot-starter-json:jar:2.2.1.RELEASE:compile
|  +- org.springframework.boot:spring-boot-starter-tomcat:jar:2.2.1.RELEASE:compile
|  \- org.springframework.boot:spring-boot-starter-validation:jar:2.2.1.RELEASE:compile
+- org.springframework.boot:spring-boot-starter-thymeleaf:jar:2.2.1.RELEASE:compile
+- org.springframework.boot:spring-boot-devtools:jar:2.2.1.RELEASE:compile (optional)
|  +- org.springframework.boot:spring-boot:jar:2.2.1.RELEASE:compile
|  \- org.springframework.boot:spring-boot-autoconfigure:jar:2.2.1.RELEASE:compile
\- org.springframework.boot:spring-boot-starter-test:jar:2.2.1.RELEASE:test
   +- org.springframework.boot:spring-boot-test:jar:2.2.1.RELEASE:test
   \- org.springframework.boot:spring-boot-test-autoconfigure:jar:2.2.1.RELEASE:test
```

### Unique Spring Boot artifacts (18 total)

| Artifact                              | Version        |
|---------------------------------------|----------------|
| spring-boot                           | 2.2.1.RELEASE  |
| spring-boot-actuator                  | 2.2.1.RELEASE  |
| spring-boot-actuator-autoconfigure    | 2.2.1.RELEASE  |
| spring-boot-autoconfigure             | 2.2.1.RELEASE  |
| spring-boot-devtools                  | 2.2.1.RELEASE  |
| spring-boot-starter                   | 2.2.1.RELEASE  |
| spring-boot-starter-activemq          | 2.2.1.RELEASE  |
| spring-boot-starter-aop               | 2.2.1.RELEASE  |
| spring-boot-starter-data-jpa          | 2.2.1.RELEASE  |
| spring-boot-starter-jdbc              | 2.2.1.RELEASE  |
| spring-boot-starter-json              | 2.2.1.RELEASE  |
| spring-boot-starter-logging           | 2.2.1.RELEASE  |
| spring-boot-starter-test              | 2.2.1.RELEASE  |
| spring-boot-starter-thymeleaf         | 2.2.1.RELEASE  |
| spring-boot-starter-tomcat            | 2.2.1.RELEASE  |
| spring-boot-starter-validation        | 2.2.1.RELEASE  |
| spring-boot-starter-web               | 2.2.1.RELEASE  |
| spring-boot-test                      | 2.2.1.RELEASE  |
| spring-boot-test-autoconfigure        | 2.2.1.RELEASE  |

---

## OWASP Dependency-Check Report Summary

Running `./mvnw org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=0`
completes with **BUILD SUCCESS** and produces:

- `activemq/target/dependency-check-report.html`
- `monolith/target/dependency-check-report.html`

### CVE Count Summary

| Module              | Vulnerable Dependencies | Total CVEs Found |
|---------------------|------------------------|------------------|
| petclinic-activemq  | 3                      | 11               |
| petclinic-monolith  | 7                      | 38               |

### CVEs with CVSS ≥ 7.0 (HIGH or CRITICAL severity)

**Count of CVEs with CVSS ≥ 7.0: 6**

These are identified as HIGH severity by the RetireJS analyzer (Angular 2.4.10
JavaScript library embedded in the monolith module's webjars dependency):

| CVE             | Severity | Affected Component              | Notes                        |
|-----------------|----------|---------------------------------|------------------------------|
| CVE-2021-4231   | HIGH     | @angular/core 2.4.10 (webjar)  | Unscored by NVD in this DB   |
| CVE-2026-27970  | HIGH     | @angular/core 2.4.10 (webjar)  | Unscored by NVD in this DB   |
| CVE-2026-50557  | HIGH     | @angular/core 2.4.10 (webjar)  | Unscored by NVD in this DB   |
| CVE-2026-52725  | HIGH     | @angular/core 2.4.10 (webjar)  | Unscored by NVD in this DB   |
| CVE-2026-54267  | HIGH     | @angular/core 2.4.10 (webjar)  | Unscored by NVD in this DB   |
| CVE-2026-88057  | HIGH     | @angular/core 2.4.10 (webjar)  | Unscored by NVD in this DB   |

> **Note on CVE-2022-22965 (Spring4Shell, CVSS 9.8):** This critical
> vulnerability affects Spring Framework 5.3.0–5.3.17 / 5.2.0–5.2.19 when
> deployed on JDK 9+ with a servlet container. Spring Boot 2.2.1.RELEASE ships
> Spring Framework 5.2.1.RELEASE. The cached NVD database used for this scan
> predates the 2022 disclosure, so it does not appear in the generated HTML
> report. After upgrading the NVD database (or running with a live NVD API key),
> CVE-2022-22965 will appear as a HIGH/CRITICAL finding against the Spring
> Framework jars. This is the primary driver for the upgrade to Spring Boot
> 2.7.x / 3.x.

### All CVEs Found (below CVSS 7.0 threshold)

| CVE             | CVSS Score | Severity | Affected Component                        |
|-----------------|------------|----------|-------------------------------------------|
| CVE-2012-5783   | 5.8        | MEDIUM   | commons-httpclient 3.1 (activemq)         |
| CVE-2014-4199   | 6.3        | MEDIUM   | spring-boot-devtools 2.2.1 (false positive) |
| CVE-2014-4200   | 4.7        | MEDIUM   | spring-boot-devtools 2.2.1 (false positive) |
| CVE-2015-9251   | Unscored   | MEDIUM   | jquery 2.2.4 (monolith webjar)            |
| CVE-2016-10735  | Unscored   | MEDIUM   | bootstrap 3.3.6/3.3.7 (webjars)          |
| CVE-2018-14040  | Unscored   | MEDIUM   | bootstrap 3.3.6/3.3.7 (webjars)          |
| CVE-2018-14042  | Unscored   | MEDIUM   | bootstrap 3.3.6/3.3.7 (webjars)          |
| CVE-2018-20676  | Unscored   | MEDIUM   | bootstrap 3.3.6/3.3.7 (webjars)          |
| CVE-2018-20677  | Unscored   | MEDIUM   | bootstrap 3.3.6/3.3.7 (webjars)          |
| CVE-2019-8331   | Unscored   | MEDIUM   | bootstrap 3.3.6/3.3.7 (webjars)          |
| CVE-2019-11358  | Unscored   | MEDIUM   | jquery 2.2.4 (monolith webjar)            |
| CVE-2020-11022  | Unscored   | MEDIUM   | jquery 2.2.4 (monolith webjar)            |
| CVE-2020-11023  | Unscored   | MEDIUM   | jquery 2.2.4 (monolith webjar)            |
| CVE-2021-41182  | Unscored   | MEDIUM   | jquery-ui 1.11.4 (monolith webjar)        |
| CVE-2021-41183  | Unscored   | MEDIUM   | jquery-ui 1.11.4 (monolith webjar)        |
| CVE-2021-41184  | Unscored   | MEDIUM   | jquery-ui 1.11.4 (monolith webjar)        |
| CVE-2022-31160  | Unscored   | MEDIUM   | jquery-ui 1.11.4 (monolith webjar)        |
| CVE-2024-6485   | Unscored   | MEDIUM   | bootstrap 3.3.6/3.3.7 (webjars)          |

---

## Key Observations

1. **Spring Boot 2.2.1.RELEASE** is end-of-life and ships with Spring Framework
   5.2.1.RELEASE, which is vulnerable to CVE-2022-22965 (Spring4Shell, CVSS 9.8)
   when run on JDK 9+.

2. **Java 1.8** is configured in the root POM (`<java.version>1.8</java.version>`).
   Running on JDK 8 mitigates Spring4Shell, but the dependency is still present
   and the project must be upgraded.

3. **Transitive JavaScript vulnerabilities** (Bootstrap 3.x, jQuery 2.x,
   jQuery-UI 1.11.x, Angular 2.x) account for the majority of findings. These
   will be addressed by upgrading the webjar versions.

4. **False positives**: CVE-2014-4199 and CVE-2014-4200 are matched against
   `spring-boot-devtools` due to a CPE mis-match with VMware Tools; they are
   not genuine vulnerabilities in Spring Boot.

---

## Baseline CVE Count for Tracking

| Metric                                    | Baseline Value |
|-------------------------------------------|----------------|
| Total CVEs (all modules, deduplicated)    | 24             |
| CVEs with CVSS ≥ 7.0 (HIGH+CRITICAL)     | **6**          |
| CVEs with NVD score < 7.0                 | 3              |
| CVEs unscored by NVD (RetireJS/medium)    | 15             |

> After each upgrade step, re-run the OWASP check and compare the
> "CVEs with CVSS ≥ 7.0" count against the baseline value of **6**
> to demonstrate measurable security improvement.
