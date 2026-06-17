#!/bin/bash
# Verification script for Spring Boot 3.2.x and Java 17 upgrade acceptance criteria

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

echo "=== Acceptance Criteria Verification ==="
echo ""

# Criterion 1: mvn dependency:tree | grep spring-boot-starter-parent shows version 3.2.x
echo "--- Criterion 1: Spring Boot parent version 3.2.x ---"
PARENT_VERSION=$(grep -A3 '<parent>' pom.xml | grep '<version>' | sed 's/.*<version>\(.*\)<\/version>.*/\1/' | tr -d ' ')
echo "  pom.xml parent version: $PARENT_VERSION"
if echo "$PARENT_VERSION" | grep -qE '^3\.2\.[0-9]+$'; then
    check "Spring Boot parent version is 3.2.x (found: $PARENT_VERSION)" "true"
else
    check "Spring Boot parent version is 3.2.x (found: $PARENT_VERSION)" "false"
fi

# Also verify via dependency:tree output
DEP_TREE_OUTPUT=$(mvn dependency:tree -pl monolith 2>&1)
if echo "$DEP_TREE_OUTPUT" | grep -qE "spring-boot.*3\.2\.[0-9]+"; then
    check "dependency:tree shows Spring Boot 3.2.x artifacts" "true"
else
    check "dependency:tree shows Spring Boot 3.2.x artifacts" "false"
fi

echo ""

# Criterion 2: mvn --version and compiler plugin output confirm Java 17 is used for compilation
echo "--- Criterion 2: Java 17 used for compilation ---"
JAVA_VERSION_PROP=$(grep '<java.version>' pom.xml | sed 's/.*<java.version>\(.*\)<\/java.version>.*/\1/' | tr -d ' ')
echo "  java.version property: $JAVA_VERSION_PROP"
if [ "$JAVA_VERSION_PROP" = "17" ]; then
    check "java.version property is 17" "true"
else
    check "java.version property is 17 (found: $JAVA_VERSION_PROP)" "false"
fi

# Check effective POM for maven.compiler.release=17
EFFECTIVE_POM=$(mvn help:effective-pom 2>&1)
if echo "$EFFECTIVE_POM" | grep -q "maven.compiler.release>17"; then
    check "maven.compiler.release is set to 17 in effective POM" "true"
else
    check "maven.compiler.release is set to 17 in effective POM" "false"
fi

# Check that compiler actually uses release 17 (from build output)
BUILD_OUTPUT=$(mvn compile -pl monolith 2>&1)
if echo "$BUILD_OUTPUT" | grep -qE "javac.*release 17|debug release 17"; then
    check "Compiler invoked with release 17" "true"
else
    check "Compiler invoked with release 17" "false"
fi

echo ""

# Criterion 3: mvn test completes without compilation errors
echo "--- Criterion 3: mvn test completes without compilation errors ---"
TEST_OUTPUT=$(mvn test 2>&1)
if echo "$TEST_OUTPUT" | grep -q "COMPILATION ERROR"; then
    check "mvn test completes without compilation errors" "false"
    echo "  Compilation errors found:"
    echo "$TEST_OUTPUT" | grep "COMPILATION ERROR" | head -5
else
    check "mvn test completes without compilation errors" "true"
fi

echo ""
echo "=== Results: $PASS passed, $FAIL failed ==="

if [ $FAIL -gt 0 ]; then
    echo "FAILED criteria:"
    for err in "${ERRORS[@]}"; do
        echo "  - $err"
    done
    exit 1
fi

echo "All acceptance criteria met!"
exit 0
