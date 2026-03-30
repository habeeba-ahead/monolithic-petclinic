#!/bin/bash

echo "=== Verifying Java 17 Maven Configuration ==="
echo

# Test 1: Check java.version property in root pom.xml
echo "1. Checking java.version property..."
JAVA_VERSION=$(grep -o '<java.version>[^<]*</java.version>' pom.xml | sed 's/<[^>]*>//g')
if [ "$JAVA_VERSION" = "17" ]; then
    echo "✓ java.version property is set to 17"
    JAVA_VERSION_OK=true
else
    echo "✗ java.version property is NOT set to 17 (found: $JAVA_VERSION)"
    JAVA_VERSION_OK=false
fi

# Test 2: Check maven.compiler.source and target properties
echo "2. Checking maven.compiler.source and target properties..."
COMPILER_SOURCE=$(grep -o '<maven.compiler.source>[^<]*</maven.compiler.source>' pom.xml | sed 's/<[^>]*>//g')
COMPILER_TARGET=$(grep -o '<maven.compiler.target>[^<]*</maven.compiler.target>' pom.xml | sed 's/<[^>]*>//g')

if [ "$COMPILER_SOURCE" = "17" ] && [ "$COMPILER_TARGET" = "17" ]; then
    echo "✓ maven.compiler.source and target are set to 17"
    COMPILER_PROPS_OK=true
else
    echo "✗ maven.compiler properties not set to 17 (source: $COMPILER_SOURCE, target: $COMPILER_TARGET)"
    COMPILER_PROPS_OK=false
fi

# Test 3: Check maven-compiler-plugin configuration
echo "3. Checking maven-compiler-plugin configuration..."
PLUGIN_SOURCE=$(grep -A 10 '<artifactId>maven-compiler-plugin</artifactId>' pom.xml | grep -o '<source>[^<]*</source>' | sed 's/<[^>]*>//g')
PLUGIN_TARGET=$(grep -A 10 '<artifactId>maven-compiler-plugin</artifactId>' pom.xml | grep -o '<target>[^<]*</target>' | sed 's/<[^>]*>//g')

if [ "$PLUGIN_SOURCE" = "17" ] && [ "$PLUGIN_TARGET" = "17" ]; then
    echo "✓ maven-compiler-plugin source and target are set to 17"
    PLUGIN_CONFIG_OK=true
else
    echo "✗ maven-compiler-plugin not configured for Java 17 (source: $PLUGIN_SOURCE, target: $PLUGIN_TARGET)"
    PLUGIN_CONFIG_OK=false
fi

# Test 4: Check Java version being used
echo "4. Checking Java runtime version..."
java -version 2>&1 | head -1
JAVA_RUNTIME_VERSION=$(java -version 2>&1 | head -1 | grep -o '"[^"]*"' | sed 's/"//g')
echo "Java runtime version: $JAVA_RUNTIME_VERSION"

# Test 5: Attempt compilation using Maven wrapper
echo "5. Testing compilation with Maven wrapper..."
./mvnw clean compile -q
COMPILE_EXIT_CODE=$?

if [ $COMPILE_EXIT_CODE -eq 0 ]; then
    echo "✓ Project compiles successfully"
    COMPILATION_OK=true
else
    echo "✗ Project compilation failed (exit code: $COMPILE_EXIT_CODE)"
    COMPILATION_OK=false
fi

echo
echo "=== Summary ==="
if [ "$JAVA_VERSION_OK" = true ] && [ "$COMPILER_PROPS_OK" = true ] && [ "$PLUGIN_CONFIG_OK" = true ] && [ "$COMPILATION_OK" = true ]; then
    echo "✓ All acceptance criteria met!"
    exit 0
else
    echo "✗ Some acceptance criteria failed"
    exit 1
fi