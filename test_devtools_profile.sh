#!/usr/bin/env bash
# TDD test script: verifies devtools is only present when -Pdev is active
set -e

export JAVA_HOME=/workspace/tools/jdk-11.0.21+9
export PATH=$JAVA_HOME/bin:$PATH

REPO_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$REPO_DIR"

PASS=0
FAIL=0

run_test() {
    local desc="$1"
    local cmd="$2"
    local expect_empty="$3"  # "true" means we expect no output (devtools absent), "false" means we expect output (devtools present)

    echo "TEST: $desc"
    actual=$(eval "$cmd" 2>/dev/null || true)
    if [ "$expect_empty" = "true" ]; then
        if [ -z "$actual" ]; then
            echo "  PASS: devtools absent as expected"
            PASS=$((PASS+1))
        else
            echo "  FAIL: expected devtools absent but found: $actual"
            FAIL=$((FAIL+1))
        fi
    else
        if [ -n "$actual" ]; then
            echo "  PASS: devtools present as expected"
            PASS=$((PASS+1))
        else
            echo "  FAIL: expected devtools present but found nothing"
            FAIL=$((FAIL+1))
        fi
    fi
}

echo "=== Building monolith without -Pdev ==="
./mvnw package -pl monolith -q -DskipTests

run_test "devtools absent in JAR without -Pdev" \
    "jar tf monolith/target/*.jar | grep devtools" \
    "true"

echo ""
echo "=== Building monolith with -Pdev ==="
./mvnw package -pl monolith -Pdev -q -DskipTests

run_test "devtools present in JAR with -Pdev" \
    "jar tf monolith/target/*.jar | grep devtools" \
    "false"

echo ""
echo "=== Results: $PASS passed, $FAIL failed ==="
if [ $FAIL -gt 0 ]; then
    exit 1
fi
exit 0
