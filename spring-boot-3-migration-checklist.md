# Spring Boot 3.2.x Migration Checklist

## Pre-Migration Phase

### ✅ Environment Preparation
- [ ] Upgrade development environment to Java 17+
- [ ] Upgrade CI/CD pipeline to Java 17+
- [ ] Update IDE settings for Java 17
- [ ] Verify all team members have Java 17+ installed

### ✅ Code Analysis
- [ ] Run dependency vulnerability scan
- [ ] Identify all javax.* imports in codebase
- [ ] Document custom configurations that may break
- [ ] Review deprecated Spring Boot 2.x features in use

### ✅ Testing Preparation
- [ ] Ensure comprehensive test coverage (>80%)
- [ ] Create integration test suite
- [ ] Document manual testing procedures
- [ ] Set up performance baseline metrics

## Migration Phase 1: Java 17 Upgrade

### ✅ Java Version Update
- [ ] Update `java.version` property in root pom.xml to 17
- [ ] Update Maven compiler plugin to 3.11.0+
- [ ] Update Maven wrapper to latest version
- [ ] Test compilation with Java 17

### ✅ Code Compatibility
- [ ] Fix any Java 17 compilation issues
- [ ] Update deprecated Java API usage
- [ ] Test application startup with Java 17
- [ ] Run full test suite with Java 17

## Migration Phase 2: Spring Boot Version Upgrade

### ✅ Version Updates
- [ ] Update Spring Boot parent version to 3.2.x
- [ ] Remove explicit version declarations for managed dependencies
- [ ] Update spring-boot-maven-plugin version
- [ ] Update any Spring Boot specific configurations

### ✅ Namespace Migration (javax → jakarta)
- [ ] Replace `javax.persistence.*` with `jakarta.persistence.*`
- [ ] Replace `javax.servlet.*` with `jakarta.servlet.*`
- [ ] Replace `javax.validation.*` with `jakarta.validation.*`
- [ ] Replace `javax.jms.*` with `jakarta.jms.*`
- [ ] Update import statements in all Java files
- [ ] Update XML configuration files
- [ ] Update annotation processor configurations

### ✅ Configuration Updates
- [ ] Update application.properties for Spring Boot 3.x
- [ ] Review and update actuator endpoint configurations
- [ ] Update security configurations for Spring Security 6.x
- [ ] Update data source configurations if needed
- [ ] Review logging configurations

## Migration Phase 3: Dependency Updates

### ✅ High-Priority Dependencies
- [ ] Update jQuery from 2.2.4 to 3.6.0+
- [ ] Update jQuery UI from 1.11.4 to 1.13.0+
- [ ] Update Bootstrap from 3.3.6 to 5.3.0+ (Major UI changes expected)
- [ ] Update WireMock from 2.26.0 to 3.0.0+
- [ ] Evaluate WRO4J replacement (consider Webpack/Vite)

### ✅ ActiveMQ Dependencies
- [ ] Test ActiveMQ compatibility with Spring Boot 3.x
- [ ] Update Hawtio SpringBoot to 3.0.0+ if available
- [ ] Verify JMS configurations work with jakarta namespace
- [ ] Test message queue functionality

### ✅ Testing Dependencies
- [ ] Update test dependencies to Spring Boot 3.x versions
- [ ] Verify JUnit 5 compatibility
- [ ] Update Mockito if needed
- [ ] Test all test slices (@WebMvcTest, @DataJpaTest, etc.)

## Migration Phase 4: Application-Specific Updates

### ✅ Entity and Repository Updates
- [ ] Update JPA entity annotations (jakarta.persistence.*)
- [ ] Verify repository interfaces work with Spring Data 3.x
- [ ] Test database operations
- [ ] Verify HSQLDB compatibility

### ✅ Web Layer Updates
- [ ] Update servlet-related code (jakarta.servlet.*)
- [ ] Verify controller mappings work
- [ ] Test Thymeleaf template rendering
- [ ] Update web security configurations

### ✅ Service Layer Updates
- [ ] Update validation annotations (jakarta.validation.*)
- [ ] Verify business logic functionality
- [ ] Test transaction management
- [ ] Update JMS message handling if applicable

## Migration Phase 5: Frontend Updates

### ✅ Bootstrap 5 Migration (High Impact)
- [ ] Update CSS classes (btn-default → btn-secondary, etc.)
- [ ] Update JavaScript components
- [ ] Test responsive design
- [ ] Update custom CSS to work with Bootstrap 5

### ✅ jQuery Updates
- [ ] Test jQuery functionality with 3.6.0+
- [ ] Update jQuery UI components
- [ ] Verify AJAX calls still work
- [ ] Test form validations

### ✅ Build Process Updates
- [ ] Evaluate WRO4J replacement
- [ ] Set up modern build tools (Webpack/Vite) if replacing WRO4J
- [ ] Update CSS/JS minification process
- [ ] Test asset compilation and serving

## Migration Phase 6: Testing and Validation

### ✅ Unit Testing
- [ ] Run all unit tests
- [ ] Fix any test failures
- [ ] Verify test coverage maintained
- [ ] Update test configurations if needed

### ✅ Integration Testing
- [ ] Test application startup
- [ ] Test database connectivity
- [ ] Test web endpoints
- [ ] Test JMS functionality
- [ ] Test actuator endpoints

### ✅ Performance Testing
- [ ] Compare startup time with baseline
- [ ] Test memory usage
- [ ] Test response times
- [ ] Verify no performance regressions

### ✅ Security Testing
- [ ] Run security vulnerability scan
- [ ] Test authentication/authorization
- [ ] Verify HTTPS configurations
- [ ] Test CSRF protection

## Migration Phase 7: Deployment and Monitoring

### ✅ Deployment Preparation
- [ ] Update deployment scripts for Java 17
- [ ] Update Docker images if used
- [ ] Update environment configurations
- [ ] Prepare rollback plan

### ✅ Production Deployment
- [ ] Deploy to staging environment
- [ ] Run smoke tests in staging
- [ ] Monitor application metrics
- [ ] Deploy to production with monitoring

### ✅ Post-Deployment Monitoring
- [ ] Monitor application logs
- [ ] Monitor performance metrics
- [ ] Monitor error rates
- [ ] Verify all functionality works as expected

## Risk Mitigation

### ✅ High-Risk Items
- [ ] **WRO4J Replacement**: Plan for modern build tool migration
- [ ] **Bootstrap 5 Upgrade**: Extensive UI testing required
- [ ] **Hawtio Compatibility**: Verify monitoring tool works or find alternative
- [ ] **Custom Configurations**: Review all custom Spring configurations

### ✅ Rollback Preparation
- [ ] Document rollback procedures
- [ ] Test rollback in staging environment
- [ ] Prepare database rollback scripts if needed
- [ ] Ensure team knows rollback process

## Success Criteria

### ✅ Technical Criteria
- [ ] Application starts successfully with Spring Boot 3.2.x
- [ ] All tests pass
- [ ] No security vulnerabilities in dependencies
- [ ] Performance meets baseline requirements
- [ ] All functionality works as expected

### ✅ Business Criteria
- [ ] No user-facing functionality broken
- [ ] UI/UX maintains expected behavior
- [ ] System availability maintained during migration
- [ ] Monitoring and alerting functional

## Estimated Timeline

- **Phase 1 (Java 17)**: 1-2 weeks
- **Phase 2 (Spring Boot 3.x)**: 2-3 weeks  
- **Phase 3 (Dependencies)**: 1-2 weeks
- **Phase 4 (Application Updates)**: 1-2 weeks
- **Phase 5 (Frontend Updates)**: 2-3 weeks
- **Phase 6 (Testing)**: 1-2 weeks
- **Phase 7 (Deployment)**: 1 week

**Total Estimated Time**: 9-15 weeks

## Notes

- This checklist should be adapted based on specific application requirements
- Some items may not apply to all applications
- Consider doing migration in smaller increments if possible
- Maintain good communication with stakeholders throughout the process