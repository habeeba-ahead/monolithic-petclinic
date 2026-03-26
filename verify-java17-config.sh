#!/bin/bash

echo "=== Verifying Java 17 Maven Configuration ==="
echo

# Test 1: Check java.version property in root pom.xml
echo "1. Checking java.version property in pom.xml..."
JAVA_VERSION=$(grep -o '<java.version>[^<]*</java.version>' pom.xml | sed 's/<[^>]*>//g')
if [ "$JAVA_VERSION" = "17" ]; then
    echo "✓ PASS: java.version property is set to 17"
    JAVA_VERSION_TEST="PASS"
else
    echo "✗ FAIL: java.version property is not set to 17 (found: $JAVA_VERSION)"
    JAVA_VERSION_TEST="FAIL"
fi
echo

# Test 2: Check Maven compiler plugin configuration
echo "2. Checking Maven compiler plugin configuration..."
SOURCE_VERSION=$(grep -A 10 'maven-compiler-plugin' pom.xml | grep -o '<source>[^<]*</source>' | sed 's/<[^>]*>//g')
TARGET_VERSION=$(grep -A 10 'maven-compiler-plugin' pom.xml | grep -o '<target>[^<]*</target>' | sed 's/<[^>]*>//g')
RELEASE_VERSION=$(grep -A 10 'maven-compiler-plugin' pom.xml | grep -o '<release>[^<]*</release>' | sed 's/<[^>]*>//g')

if [ "$SOURCE_VERSION" = "17" ] && [ "$TARGET_VERSION" = "17" ] && [ "$RELEASE_VERSION" = "17" ]; then
    echo "✓ PASS: Maven compiler plugin configured for Java 17 (source: $SOURCE_VERSION, target: $TARGET_VERSION, release: $RELEASE_VERSION)"
    COMPILER_CONFIG_TEST="PASS"
else
    echo "✗ FAIL: Maven compiler plugin not properly configured for Java 17"
    echo "  Source: $SOURCE_VERSION, Target: $TARGET_VERSION, Release: $RELEASE_VERSION"
    COMPILER_CONFIG_TEST="FAIL"
fi
echo

# Test 3: Check if project compiles successfully
echo "3. Testing project compilation with Java 17..."
mvn clean compile -q
COMPILE_EXIT_CODE=$?

if [ $COMPILE_EXIT_CODE -eq 0 ]; then
    echo "✓ PASS: Project compiles successfully with Java 17"
    COMPILATION_TEST="PASS"
else
    echo "✗ FAIL: Project compilation failed with Java 17"
    COMPILATION_TEST="FAIL"
fi
echo

# Summary
echo "=== Test Summary ==="
echo "Java version property: $JAVA_VERSION_TEST"
echo "Compiler configuration: $COMPILER_CONFIG_TEST"
echo "Compilation test: $COMPILATION_TEST"
echo

# Exit with error if any test failed
if [ "$JAVA_VERSION_TEST" = "FAIL" ] || [ "$COMPILER_CONFIG_TEST" = "FAIL" ] || [ "$COMPILATION_TEST" = "FAIL" ]; then
    echo "❌ Some tests failed"
    exit 1
else
    echo "✅ All tests passed"
    exit 0
fi