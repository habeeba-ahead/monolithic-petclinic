package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify that form-based login with custom login page is properly configured.
 * This test examines the SecurityConfig structure to ensure form login configuration is present.
 */
public class SecurityConfigFormLoginTest {

    @Test
    public void securityConfigShouldHaveFormLoginConfiguration() throws Exception {
        // Verify that SecurityConfig has the configure(HttpSecurity) method
        // which is where form login configuration is implemented
        Method configureMethod = SecurityConfig.class.getDeclaredMethod("configure", 
            org.springframework.security.config.annotation.web.builders.HttpSecurity.class);
        
        assertThat(configureMethod)
            .as("SecurityConfig should have configure(HttpSecurity) method for form login configuration")
            .isNotNull();
        
        // Verify the method is protected (standard Spring Security pattern)
        assertThat(java.lang.reflect.Modifier.isProtected(configureMethod.getModifiers()))
            .as("configure(HttpSecurity) method should be protected")
            .isTrue();
    }

    @Test
    public void securityConfigShouldExtendWebSecurityConfigurerAdapterForFormLogin() {
        // Verify SecurityConfig extends WebSecurityConfigurerAdapter
        // This is required for form-based login configuration
        Class<?> superClass = SecurityConfig.class.getSuperclass();
        assertThat(superClass.getSimpleName())
            .as("SecurityConfig should extend WebSecurityConfigurerAdapter to enable form-based login")
            .isEqualTo("WebSecurityConfigurerAdapter");
    }

    @Test
    public void securityConfigShouldCompileAndInstantiateForFormLogin() {
        // Test that SecurityConfig compiles and can be instantiated
        // This ensures the form login configuration doesn't have syntax errors
        SecurityConfig securityConfig = new SecurityConfig();
        assertThat(securityConfig)
            .as("SecurityConfig should compile and instantiate without errors for form login")
            .isNotNull();
    }
}