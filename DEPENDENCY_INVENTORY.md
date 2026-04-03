# Current Dependency Inventory

## Overview
This document provides a complete inventory of all dependencies in the Pet Clinic application as of the current state (Spring Boot 2.2.1.RELEASE).

## Root Project Configuration
- **Spring Boot Version**: 2.2.1.RELEASE (November 2019)
- **Java Version**: 1.8
- **Maven Parent**: spring-boot-starter-parent

## Monolith Module Dependencies

### Core Spring Boot Starters
| Dependency | Current Version | Scope | Notes |
|------------|----------------|-------|-------|
| spring-boot-starter-data-jpa | 2.2.1.RELEASE | compile | JPA/Hibernate support |
| spring-boot-starter-web | 2.2.1.RELEASE | compile | Web MVC support |
| spring-boot-starter-thymeleaf | 2.2.1.RELEASE | compile | Template engine |
| spring-boot-starter-activemq | 2.2.1.RELEASE | compile | JMS messaging |
| spring-boot-starter-test | 2.2.1.RELEASE | test | Testing framework |
| spring-boot-devtools | 2.2.1.RELEASE | optional | Development tools |

### Database Dependencies
| Dependency | Current Version | Scope | Notes |
|------------|----------------|-------|-------|
| hsqldb | Managed by Spring Boot | runtime | In-memory database |

### Frontend Dependencies (WebJars)
| Dependency | Current Version | Scope | Security Risk |
|------------|----------------|-------|---------------|
| webjars-locator-core | Managed by Spring Boot | compile | Low |
| jquery | 2.2.4 | compile | **HIGH** - Multiple CVEs |
| jquery-ui | 1.11.4 | compile | **HIGH** - Multiple CVEs |
| bootstrap | 3.3.6 | compile | **MEDIUM** - Outdated |

### Build/Test Dependencies
| Dependency | Current Version | Scope | Notes |
|------------|----------------|-------|-------|
| wiremock-jre8 | 2.26.0 | test | HTTP service mocking |
| wro4j-maven-plugin | 1.8.0 | build | Web resource optimization |

## ActiveMQ Module Dependencies

### Core Dependencies
| Dependency | Current Version | Scope | Notes |
|------------|----------------|-------|-------|
| activemq-broker | Managed by Spring Boot | compile | Message broker |
| activemq-pool | Managed by Spring Boot | compile | Connection pooling |
| hawtio-springboot | 2.8.0 | compile | Management console |
| spring-boot-actuator | 2.2.1.RELEASE | compile | Monitoring endpoints |
| spring-boot-devtools | 2.2.1.RELEASE | optional | Development tools |

## Critical Issues Identified

### Security Vulnerabilities
1. **jQuery 2.2.4** - Contains multiple XSS vulnerabilities (CVE-2020-11022, CVE-2020-11023)
2. **jQuery UI 1.11.4** - Contains XSS vulnerabilities (CVE-2016-7103)
3. **Bootstrap 3.3.6** - Missing security patches from newer versions

### Compatibility Issues
1. **Java 8** - Spring Boot 3.x requires Java 17+
2. **Spring Boot 2.2.1** - End of life, no security updates
3. **Frontend libraries** - Severely outdated, potential compatibility issues

### Performance Issues
1. **Old Hibernate version** - Missing performance optimizations
2. **Outdated Thymeleaf** - Missing template caching improvements
3. **Legacy WebJars** - Larger bundle sizes, slower loading

## Transitive Dependencies Analysis
Based on Spring Boot 2.2.1.RELEASE, key transitive dependencies include:
- Spring Framework 5.2.1.RELEASE
- Hibernate 5.4.8.Final
- Jackson 2.10.0
- Tomcat 9.0.27
- Logback 1.2.3

## Risk Assessment
- **Critical**: Frontend security vulnerabilities
- **High**: Outdated Spring Boot version
- **Medium**: Java version compatibility
- **Low**: Build tool versions

## Next Steps
1. Review Spring Boot 3.x Compatibility Matrix
2. Plan incremental upgrade through Spring Boot 2.7.x
3. Address security vulnerabilities in frontend dependencies
4. Upgrade Java to version 17+