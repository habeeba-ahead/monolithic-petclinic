#!/bin/bash
# TDD test script to verify Spring Boot 3.2.x and Java 17 upgrade acceptance criteria
# This script should FAIL before the upgrade and PASS after

set -e

PASS=0
FAIL=0
ERRORS=()

check() {
    local description="$1"
    local result="$2"
    if [ "$result" = "true" ]; then
        echo "  PASS: $description"
        PASS=$((PASS + 1))
    else
        echo "  FAIL: $description"
        FAIL=$((FAIL + 1))
        ERRORS+=("$description")
    fi
}

echo "=== Spring Boot 3.2.x + Java 17 Upgrade Tests ==="
echo ""

# Test 1: pom.xml parent version is 3.2.x
echo "--- Test 1: Spring Boot parent version ---"
PARENT_VERSION=$(grep -A3 '<parent>' pom.xml | grep '<version>' | sed 's/.*<version>\(.*\)<\/version>.*/\1/' | tr -d ' ')
echo "  Found parent version: $PARENT_VERSION"
if echo "$PARENT_VERSION" | grep -qE '^3\.2\.[0-9]+$'; then
    check "Spring Boot parent version is 3.2.x" "true"
else
    check "Spring Boot parent version is 3.2.x (found: $PARENT_VERSION)" "false"
fi

# Test 2: java.version property is 17
echo "--- Test 2: java.version property ---"
JAVA_VERSION_PROP=$(grep '<java.version>' pom.xml | sed 's/.*<java.version>\(.*\)<\/java.version>.*/\1/' | tr -d ' ')
echo "  Found java.version property: $JAVA_VERSION_PROP"
if [ "$JAVA_VERSION_PROP" = "17" ]; then
    check "java.version property is 17" "true"
else
    check "java.version property is 17 (found: $JAVA_VERSION_PROP)" "false"
fi

# Test 3: maven-compiler-plugin source/target is 17 (via Spring Boot's java.version property)
echo "--- Test 3: Compiler source/target ---"
# Spring Boot's parent pom uses java.version for maven.compiler.source and maven.compiler.target
# So if java.version=17, the compiler will use 17
if [ "$JAVA_VERSION_PROP" = "17" ]; then
    check "Compiler source/target set to 17 via java.version" "true"
else
    check "Compiler source/target set to 17 via java.version" "false"
fi

echo ""
echo "=== Results: $PASS passed, $FAIL failed ==="

if [ $FAIL -gt 0 ]; then
    echo "FAILED tests:"
    for err in "${ERRORS[@]}"; do
        echo "  - $err"
    done
    exit 1
fi

echo "All tests passed!"
exit 0
