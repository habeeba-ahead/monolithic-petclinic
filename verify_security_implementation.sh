#!/bin/bash

echo "=== Spring Security Implementation Verification ==="
echo

# Check 1: Spring Security dependency in pom.xml
echo "1. Checking Spring Security dependency in monolith/pom.xml..."
if grep -q "spring-boot-starter-security" monolith/pom.xml; then
    echo "✓ PASS: spring-boot-starter-security dependency found"
else
    echo "✗ FAIL: spring-boot-starter-security dependency not found"
    exit 1
fi
echo

# Check 2: SecurityConfig class exists
echo "2. Checking SecurityConfig class exists..."
if [ -f "monolith/src/main/java/org/springframework/samples/petclinic/config/SecurityConfig.java" ]; then
    echo "✓ PASS: SecurityConfig.java file exists"
else
    echo "✗ FAIL: SecurityConfig.java file not found"
    exit 1
fi
echo

# Check 3: @EnableWebSecurity annotation
echo "3. Checking @EnableWebSecurity annotation..."
if grep -q "@EnableWebSecurity" monolith/src/main/java/org/springframework/samples/petclinic/config/SecurityConfig.java; then
    echo "✓ PASS: @EnableWebSecurity annotation found"
else
    echo "✗ FAIL: @EnableWebSecurity annotation not found"
    exit 1
fi
echo

# Check 4: @Configuration annotation
echo "4. Checking @Configuration annotation..."
if grep -q "@Configuration" monolith/src/main/java/org/springframework/samples/petclinic/config/SecurityConfig.java; then
    echo "✓ PASS: @Configuration annotation found"
else
    echo "✗ FAIL: @Configuration annotation not found"
    exit 1
fi
echo

# Check 5: HTTP Basic authentication configuration
echo "5. Checking HTTP Basic authentication configuration..."
if grep -q "httpBasic" monolith/src/main/java/org/springframework/samples/petclinic/config/SecurityConfig.java; then
    echo "✓ PASS: httpBasic() configuration found"
else
    echo "✗ FAIL: httpBasic() configuration not found"
    exit 1
fi
echo

# Check 6: All endpoints require authentication
echo "6. Checking all endpoints require authentication..."
if grep -q "anyRequest.*authenticated" monolith/src/main/java/org/springframework/samples/petclinic/config/SecurityConfig.java; then
    echo "✓ PASS: anyRequest().authenticated() configuration found"
else
    echo "✗ FAIL: anyRequest().authenticated() configuration not found"
    exit 1
fi
echo

# Check 7: SecurityFilterChain bean
echo "7. Checking SecurityFilterChain bean configuration..."
if grep -q "SecurityFilterChain" monolith/src/main/java/org/springframework/samples/petclinic/config/SecurityConfig.java; then
    echo "✓ PASS: SecurityFilterChain bean configuration found"
else
    echo "✗ FAIL: SecurityFilterChain bean configuration not found"
    exit 1
fi
echo

# Check 8: UserDetailsService bean
echo "8. Checking UserDetailsService bean configuration..."
if grep -q "UserDetailsService" monolith/src/main/java/org/springframework/samples/petclinic/config/SecurityConfig.java; then
    echo "✓ PASS: UserDetailsService bean configuration found"
else
    echo "✗ FAIL: UserDetailsService bean configuration not found"
    exit 1
fi
echo

echo "=== All Security Implementation Checks Passed! ==="
echo
echo "Summary:"
echo "- Spring Security dependency added to monolith/pom.xml ✓"
echo "- SecurityConfig class created with @EnableWebSecurity annotation ✓"
echo "- Basic HTTP authentication enabled for all endpoints ✓"
echo "- Additional security beans properly configured ✓"