#!/usr/bin/env bash
# TDD test script: verifies pom.xml has been upgraded to Spring Boot 3.2.x and Java 17
# Run BEFORE and AFTER the change to confirm red→green

set -euo pipefail

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
    echo "        Expected to find: '$pattern' in $file"
    FAIL=$((FAIL + 1))
  fi
}

assert_not_contains() {
  local description="$1"
  local pattern="$2"
  local file="$3"
  if ! grep -qF "$pattern" "$file"; then
    echo "  PASS: $description"
    PASS=$((PASS + 1))
  else
    echo "  FAIL: $description"
    echo "        Expected NOT to find: '$pattern' in $file"
    FAIL=$((FAIL + 1))
  fi
}

echo "=== Running pom.xml upgrade tests ==="

# Test 1: Spring Boot parent version must be 3.2.x (specifically 3.2.5)
assert_contains \
  "spring-boot-starter-parent version is 3.2.5" \
  "<version>3.2.5</version>" \
  "pom.xml"

# Test 2: Old Spring Boot 2.2.1.RELEASE version must be gone
assert_not_contains \
  "Old Spring Boot 2.2.1.RELEASE version is removed" \
  "<version>2.2.1.RELEASE</version>" \
  "pom.xml"

# Test 3: java.version property must be 17
assert_contains \
  "java.version property is set to 17" \
  "<java.version>17</java.version>" \
  "pom.xml"

# Test 4: Old java.version 1.8 must be gone
assert_not_contains \
  "Old java.version 1.8 is removed" \
  "<java.version>1.8</java.version>" \
  "pom.xml"

# Test 5: The parent groupId/artifactId must still be correct (no regression)
assert_contains \
  "spring-boot-starter-parent groupId is present" \
  "<groupId>org.springframework.boot</groupId>" \
  "pom.xml"

assert_contains \
  "spring-boot-starter-parent artifactId is present" \
  "<artifactId>spring-boot-starter-parent</artifactId>" \
  "pom.xml"

echo ""
echo "=== Results: $PASS passed, $FAIL failed ==="

if [ "$FAIL" -gt 0 ]; then
  exit 1
fi
exit 0
