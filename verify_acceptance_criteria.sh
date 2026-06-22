#!/bin/bash
# Verification script for Spring Boot 3.2.x + Java 17 upgrade acceptance criteria

REPO_DIR="/app/output/d2787eaf-10a3-40da-8017-24f8cb61fda7/worker-1/repo"
POM="$REPO_DIR/pom.xml"
PASS=0
FAIL=0

echo "=== Acceptance Criteria Verification ==="
echo ""

# Criterion 1: spring-boot-starter-parent version is 3.2.x
echo "--- Criterion 1: spring-boot-starter-parent version is 3.2.x ---"
PARENT_VERSION=$(grep -A2 "spring-boot-starter-parent" "$POM" | grep "<version>" | sed 's/.*<version>\(.*\)<\/version>.*/\1/' | tr -d ' ')
echo "Found version: $PARENT_VERSION"
if echo "$PARENT_VERSION" | grep -qE "^3\.2\.[0-9]+$"; then
    echo "PASS: spring-boot-starter-parent version is $PARENT_VERSION (matches 3.2.x)"
    PASS=$((PASS+1))
else
    echo "FAIL: spring-boot-starter-parent version '$PARENT_VERSION' does not match 3.2.x"
    FAIL=$((FAIL+1))
fi
echo ""

# Criterion 2: <java.version>17</java.version> in <properties> block
echo "--- Criterion 2: <java.version>17</java.version> in <properties> block ---"
JAVA_VERSION=$(grep "<java.version>" "$POM" | sed 's/.*<java.version>\(.*\)<\/java.version>.*/\1/' | tr -d ' ')
echo "Found java.version: $JAVA_VERSION"
if [ "$JAVA_VERSION" = "17" ]; then
    echo "PASS: <java.version>17</java.version> found in pom.xml"
    PASS=$((PASS+1))
else
    echo "FAIL: <java.version> is '$JAVA_VERSION', expected '17'"
    FAIL=$((FAIL+1))
fi
echo ""

# Criterion 3: ./mvnw dependency:tree completes without resolution errors
echo "--- Criterion 3: ./mvnw dependency:tree completes without resolution errors ---"
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-arm64
cd "$REPO_DIR"
MVN_OUTPUT=$(./mvnw dependency:tree 2>&1)
MVN_EXIT=$?
if [ $MVN_EXIT -eq 0 ] && echo "$MVN_OUTPUT" | grep -q "BUILD SUCCESS"; then
    echo "PASS: ./mvnw dependency:tree completed with BUILD SUCCESS (exit code: $MVN_EXIT)"
    PASS=$((PASS+1))
else
    echo "FAIL: ./mvnw dependency:tree failed (exit code: $MVN_EXIT)"
    echo "Last 20 lines of output:"
    echo "$MVN_OUTPUT" | tail -20
    FAIL=$((FAIL+1))
fi
echo ""

echo "=== Summary ==="
echo "PASSED: $PASS"
echo "FAILED: $FAIL"
echo ""
if [ $FAIL -eq 0 ]; then
    echo "ALL CRITERIA MET"
    exit 0
else
    echo "SOME CRITERIA FAILED"
    exit 1
fi
