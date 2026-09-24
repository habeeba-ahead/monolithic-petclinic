#!/usr/bin/env bash
# TDD tests for DevTools Maven profile migration
# These tests verify the acceptance criteria before and after implementation.

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
MONOLITH_DIR="$REPO_ROOT/monolith"
ACTIVEMQ_DIR="$REPO_ROOT/activemq"
PASS=0
FAIL=0

green() { echo -e "\033[32m[PASS]\033[0m $1"; }
red()   { echo -e "\033[31m[FAIL]\033[0m $1"; }

run_test() {
    local desc="$1"
    local result="$2"   # "pass" or "fail"
    if [ "$result" = "pass" ]; then
        green "$desc"
        PASS=$((PASS+1))
    else
        red "$desc"
        FAIL=$((FAIL+1))
    fi
}

echo "=== Test 1: monolith/pom.xml has a <profile> with id 'dev' ==="
if grep -A5 '<profiles>' "$MONOLITH_DIR/pom.xml" 2>/dev/null | grep -q '<id>dev</id>'; then
    run_test "monolith/pom.xml contains a 'dev' profile" "pass"
else
    run_test "monolith/pom.xml contains a 'dev' profile" "fail"
fi

echo "=== Test 2: activemq/pom.xml has a <profile> with id 'dev' ==="
if grep -A5 '<profiles>' "$ACTIVEMQ_DIR/pom.xml" 2>/dev/null | grep -q '<id>dev</id>'; then
    run_test "activemq/pom.xml contains a 'dev' profile" "pass"
else
    run_test "activemq/pom.xml contains a 'dev' profile" "fail"
fi

echo "=== Test 3: spring-boot-devtools is NOT in monolith default dependencies ==="
# The devtools dependency should only appear inside a <profiles> block
MONOLITH_POM="$MONOLITH_DIR/pom.xml"
# Extract content before <profiles> tag and check devtools is absent there
BEFORE_PROFILES=$(awk '/<profiles>/{ exit } { print }' "$MONOLITH_POM")
if echo "$BEFORE_PROFILES" | grep -q 'spring-boot-devtools'; then
    run_test "spring-boot-devtools absent from monolith default dependencies" "fail"
else
    run_test "spring-boot-devtools absent from monolith default dependencies" "pass"
fi

echo "=== Test 4: spring-boot-devtools is NOT in activemq default dependencies ==="
ACTIVEMQ_POM="$ACTIVEMQ_DIR/pom.xml"
BEFORE_PROFILES_AMQ=$(awk '/<profiles>/{ exit } { print }' "$ACTIVEMQ_POM")
if echo "$BEFORE_PROFILES_AMQ" | grep -q 'spring-boot-devtools'; then
    run_test "spring-boot-devtools absent from activemq default dependencies" "fail"
else
    run_test "spring-boot-devtools absent from activemq default dependencies" "pass"
fi

echo "=== Test 5: spring-boot-devtools IS inside the dev profile in monolith/pom.xml ==="
INSIDE_PROFILES=$(awk '/<profiles>/,/<\/profiles>/' "$MONOLITH_POM")
if echo "$INSIDE_PROFILES" | grep -q 'spring-boot-devtools'; then
    run_test "spring-boot-devtools present inside monolith dev profile" "pass"
else
    run_test "spring-boot-devtools present inside monolith dev profile" "fail"
fi

echo "=== Test 6: spring-boot-devtools IS inside the dev profile in activemq/pom.xml ==="
INSIDE_PROFILES_AMQ=$(awk '/<profiles>/,/<\/profiles>/' "$ACTIVEMQ_POM")
if echo "$INSIDE_PROFILES_AMQ" | grep -q 'spring-boot-devtools'; then
    run_test "spring-boot-devtools present inside activemq dev profile" "pass"
else
    run_test "spring-boot-devtools present inside activemq dev profile" "fail"
fi

echo "=== Test 7: dev profile is NOT activated by default in monolith/pom.xml ==="
# There should be no <activeByDefault>true</activeByDefault> in the dev profile
DEV_PROFILE_MONOLITH=$(awk '/<id>dev<\/id>/,/<\/profile>/' "$MONOLITH_POM" 2>/dev/null || true)
if echo "$DEV_PROFILE_MONOLITH" | grep -q '<activeByDefault>true</activeByDefault>'; then
    run_test "monolith dev profile is NOT activated by default" "fail"
else
    run_test "monolith dev profile is NOT activated by default" "pass"
fi

echo "=== Test 8: dev profile is NOT activated by default in activemq/pom.xml ==="
DEV_PROFILE_AMQ=$(awk '/<id>dev<\/id>/,/<\/profile>/' "$ACTIVEMQ_POM" 2>/dev/null || true)
if echo "$DEV_PROFILE_AMQ" | grep -q '<activeByDefault>true</activeByDefault>'; then
    run_test "activemq dev profile is NOT activated by default" "fail"
else
    run_test "activemq dev profile is NOT activated by default" "pass"
fi

echo "=== Test 9: application.properties has spring.thymeleaf.cache=true ==="
APP_PROPS="$MONOLITH_DIR/src/main/resources/application.properties"
if grep -q '^spring\.thymeleaf\.cache=true' "$APP_PROPS"; then
    run_test "application.properties has spring.thymeleaf.cache=true" "pass"
else
    run_test "application.properties has spring.thymeleaf.cache=true" "fail"
fi

echo "=== Test 10: Build monolith without -P dev and verify devtools absent from JAR ==="
cd "$MONOLITH_DIR"
echo "Building monolith (default profile, no devtools expected)..."
if "$REPO_ROOT/mvnw" package -DskipTests -q 2>/dev/null; then
    JAR_FILE=$(ls target/petclinic-monolith-*.jar 2>/dev/null | head -1)
    if [ -z "$JAR_FILE" ]; then
        run_test "Default build: devtools absent from JAR" "fail"
        echo "  ERROR: No JAR found in target/"
    else
        DEVTOOLS_IN_JAR=$(jar tf "$JAR_FILE" 2>/dev/null | grep devtools || true)
        if [ -z "$DEVTOOLS_IN_JAR" ]; then
            run_test "Default build: devtools absent from JAR" "pass"
        else
            run_test "Default build: devtools absent from JAR" "fail"
            echo "  Found devtools entries: $DEVTOOLS_IN_JAR"
        fi
    fi
else
    run_test "Default build: devtools absent from JAR" "fail"
    echo "  ERROR: Build failed"
fi

echo "=== Test 11: Build monolith with -P dev and verify devtools present in JAR ==="
cd "$MONOLITH_DIR"
echo "Building monolith (dev profile, devtools expected)..."
if "$REPO_ROOT/mvnw" package -P dev -DskipTests -q 2>/dev/null; then
    JAR_FILE=$(ls target/petclinic-monolith-*.jar 2>/dev/null | head -1)
    if [ -z "$JAR_FILE" ]; then
        run_test "Dev profile build: devtools present in JAR" "fail"
        echo "  ERROR: No JAR found in target/"
    else
        DEVTOOLS_IN_JAR=$(jar tf "$JAR_FILE" 2>/dev/null | grep devtools || true)
        if [ -n "$DEVTOOLS_IN_JAR" ]; then
            run_test "Dev profile build: devtools present in JAR" "pass"
        else
            run_test "Dev profile build: devtools present in JAR" "fail"
            echo "  No devtools entries found in JAR"
        fi
    fi
else
    run_test "Dev profile build: devtools present in JAR" "fail"
    echo "  ERROR: Build failed"
fi

echo ""
echo "=== Results: $PASS passed, $FAIL failed ==="
if [ "$FAIL" -gt 0 ]; then
    exit 1
fi
exit 0
