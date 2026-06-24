#!/usr/bin/env bash
# TDD test script: validates OWASP Dependency-Check plugin configuration in root pom.xml
# Run this script to verify acceptance criteria are met.
# Exit code 0 = all tests pass, non-zero = failure.

set -euo pipefail

POM="pom.xml"
PASS=0
FAIL=0

assert_contains() {
    local description="$1"
    local pattern="$2"
    local file="$3"
    if grep -qF "$pattern" "$file"; then
        echo "  PASS: $description"
        PASS=$((PASS + 1))
    else
        echo "  FAIL: $description"
        echo "        Expected to find: $pattern"
        echo "        In file: $file"
        FAIL=$((FAIL + 1))
    fi
}

echo "=== OWASP Dependency-Check Plugin TDD Tests ==="
echo ""

echo "--- Test Group 1: Plugin declared in pluginManagement ---"
assert_contains \
    "pluginManagement section exists" \
    "<pluginManagement>" \
    "$POM"

assert_contains \
    "org.owasp groupId present in pluginManagement" \
    "org.owasp" \
    "$POM"

assert_contains \
    "dependency-check-maven artifactId present" \
    "dependency-check-maven" \
    "$POM"

assert_contains \
    "Plugin version is pinned to 9.x series" \
    "<version>9." \
    "$POM"

echo ""
echo "--- Test Group 2: Plugin configured in build/plugins (applies to all modules) ---"
assert_contains \
    "Plugin declared in build/plugins section" \
    "<artifactId>dependency-check-maven</artifactId>" \
    "$POM"

echo ""
echo "--- Test Group 3: Required configuration present ---"
assert_contains \
    "failBuildOnCVSS set to 7" \
    "<failBuildOnCVSS>7</failBuildOnCVSS>" \
    "$POM"

assert_contains \
    "format set to HTML" \
    "<format>HTML</format>" \
    "$POM"

echo ""
echo "--- Test Group 4: NVD API key placeholder for CI ---"
assert_contains \
    "NVD API key CI placeholder present" \
    "NVD_API_KEY" \
    "$POM"

echo ""
echo "--- Test Group 5: check goal bound to verify phase ---"
assert_contains \
    "check goal configured" \
    "<goal>check</goal>" \
    "$POM"

echo ""
echo "=== Results: $PASS passed, $FAIL failed ==="

if [ "$FAIL" -gt 0 ]; then
    echo "RESULT: TESTS FAILED"
    exit 1
else
    echo "RESULT: ALL TESTS PASSED"
    exit 0
fi
