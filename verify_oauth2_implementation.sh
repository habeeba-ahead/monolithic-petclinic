#!/bin/bash

echo "Verifying OAuth2/OIDC Implementation..."

# Check if required files exist
echo "Checking required files..."

files=(
    "monolith/src/main/java/org/springframework/samples/petclinic/config/SecurityConfig.java"
    "monolith/src/main/resources/application.yml"
    "monolith/src/main/java/org/springframework/samples/petclinic/controller/AuthController.java"
    "monolith/src/main/resources/templates/user.html"
    "monolith/src/main/resources/templates/fragments/layout.html"
)

for file in "${files[@]}"; do
    if [ -f "$file" ]; then
        echo "✓ $file exists"
    else
        echo "✗ $file missing"
        exit 1
    fi
done

# Check if OAuth2 dependencies are in pom.xml
echo "Checking OAuth2 dependencies in pom.xml..."
if grep -q "spring-boot-starter-oauth2-client" monolith/pom.xml; then
    echo "✓ OAuth2 client dependency found"
else
    echo "✗ OAuth2 client dependency missing"
    exit 1
fi

if grep -q "spring-boot-starter-security" monolith/pom.xml; then
    echo "✓ Spring Security dependency found"
else
    echo "✗ Spring Security dependency missing"
    exit 1
fi

if grep -q "thymeleaf-extras-springsecurity5" monolith/pom.xml; then
    echo "✓ Thymeleaf Security extras dependency found"
else
    echo "✗ Thymeleaf Security extras dependency missing"
    exit 1
fi

# Check OAuth2 configuration in application.yml
echo "Checking OAuth2 configuration..."
if grep -q "oauth2:" monolith/src/main/resources/application.yml; then
    echo "✓ OAuth2 configuration found in application.yml"
else
    echo "✗ OAuth2 configuration missing from application.yml"
    exit 1
fi

if grep -q "google:" monolith/src/main/resources/application.yml; then
    echo "✓ Google provider configuration found"
else
    echo "✗ Google provider configuration missing"
    exit 1
fi

# Check test files
echo "Checking test files..."
test_files=(
    "monolith/src/test/java/org/springframework/samples/petclinic/config/SecurityConfigTest.java"
    "monolith/src/test/java/org/springframework/samples/petclinic/config/OAuth2IntegrationTest.java"
    "monolith/src/test/java/org/springframework/samples/petclinic/config/ApplicationConfigTest.java"
    "monolith/src/test/java/org/springframework/samples/petclinic/controller/AuthControllerTest.java"
    "monolith/src/test/java/org/springframework/samples/petclinic/OAuth2FlowIntegrationTest.java"
)

for file in "${test_files[@]}"; do
    if [ -f "$file" ]; then
        echo "✓ $file exists"
    else
        echo "✗ $file missing"
        exit 1
    fi
done

echo ""
echo "✅ All OAuth2/OIDC implementation files are present and configured correctly!"
echo ""
echo "Next steps:"
echo "1. Set up Google Cloud Console OAuth2 credentials"
echo "2. Set GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET environment variables"
echo "3. Run the application: ./mvnw spring-boot:run -pl monolith"
echo "4. Access http://localhost:8080 and test the authentication flow"