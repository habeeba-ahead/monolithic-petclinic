#!/usr/bin/env bash
# TDD test: verify pom.xml has the correct Spring Boot and Java versions
set -euo pipefail

FAILURES=0

# Test 1: Spring Boot parent version must be 2.7.18
SB_VERSION=$(grep -A2 'spring-boot-starter-parent' pom.xml | grep '<version>' | sed 's/.*<version>\(.*\)<\/version>.*/\1/' | tr -d '[:space:]')
if [ "$SB_VERSION" = "2.7.18" ]; then
    echo "PASS: spring-boot-starter-parent version is 2.7.18"
else
    echo "FAIL: expected spring-boot-starter-parent version 2.7.18, got '${SB_VERSION}'"
    FAILURES=$((FAILURES + 1))
fi

# Test 2: java.version must be 17
JAVA_VER=$(grep '<java.version>' pom.xml | sed 's/.*<java.version>\(.*\)<\/java.version>.*/\1/' | tr -d '[:space:]')
if [ "$JAVA_VER" = "17" ]; then
    echo "PASS: java.version is 17"
else
    echo "FAIL: expected java.version 17, got '${JAVA_VER}'"
    FAILURES=$((FAILURES + 1))
fi

# Test 3: pom.xml must contain <version>2.7.18</version> (acceptance criteria line 9 content)
if grep -q '<version>2\.7\.18</version>' pom.xml; then
    echo "PASS: pom.xml contains <version>2.7.18</version>"
else
    echo "FAIL: pom.xml does not contain <version>2.7.18</version>"
    FAILURES=$((FAILURES + 1))
fi

# Test 4: pom.xml must contain <java.version>17</java.version> (acceptance criteria line 22 content)
if grep -q '<java.version>17</java.version>' pom.xml; then
    echo "PASS: pom.xml contains <java.version>17</java.version>"
else
    echo "FAIL: pom.xml does not contain <java.version>17</java.version>"
    FAILURES=$((FAILURES + 1))
fi

# Test 5: old version 2.2.1.RELEASE must not appear
if grep -q '2\.2\.1\.RELEASE' pom.xml; then
    echo "FAIL: pom.xml still contains old version 2.2.1.RELEASE"
    FAILURES=$((FAILURES + 1))
else
    echo "PASS: pom.xml does not contain old version 2.2.1.RELEASE"
fi

# Test 6: old java version 1.8 must not appear in java.version property
if grep -q '<java.version>1\.8</java.version>' pom.xml; then
    echo "FAIL: pom.xml still contains old java.version 1.8"
    FAILURES=$((FAILURES + 1))
else
    echo "PASS: pom.xml does not contain old java.version 1.8"
fi

# Test 7: ./mvnw validate must exit 0 and print no ERROR lines
echo ""
echo "Running ./mvnw validate..."

# Find available Java
if [ -n "${JAVA_HOME:-}" ] && [ -x "${JAVA_HOME}/bin/java" ]; then
    JAVA_BIN="${JAVA_HOME}/bin/java"
elif [ -x "/workspace/tools/jdk-11.0.21+9/bin/java" ]; then
    export JAVA_HOME="/workspace/tools/jdk-11.0.21+9"
    export PATH="${JAVA_HOME}/bin:${PATH}"
elif [ -x "/workspace/tools/jdk-11.0.2/bin/java" ]; then
    export JAVA_HOME="/workspace/tools/jdk-11.0.2"
    export PATH="${JAVA_HOME}/bin:${PATH}"
fi

VALIDATE_OUTPUT=$(./mvnw validate 2>&1)
VALIDATE_EXIT=$?
if [ $VALIDATE_EXIT -ne 0 ]; then
    echo "FAIL: ./mvnw validate exited with code $VALIDATE_EXIT"
    echo "$VALIDATE_OUTPUT" | tail -20
    FAILURES=$((FAILURES + 1))
else
    ERROR_LINES=$(echo "$VALIDATE_OUTPUT" | grep -i '\[ERROR\]' || true)
    if [ -n "$ERROR_LINES" ]; then
        echo "FAIL: ./mvnw validate printed ERROR lines:"
        echo "$ERROR_LINES"
        FAILURES=$((FAILURES + 1))
    else
        echo "PASS: ./mvnw validate exited 0 with no ERROR lines"
    fi
fi

echo ""
if [ "$FAILURES" -gt 0 ]; then
    echo "TOTAL FAILURES: $FAILURES"
    exit 1
else
    echo "All tests passed."
    exit 0
fi
