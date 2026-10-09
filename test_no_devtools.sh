#!/usr/bin/env bash
# TDD test: verify spring-boot-devtools is NOT present in either pom.xml
# This test should FAIL before the fix and PASS after the fix.

set -euo pipefail

PASS=0
FAIL=0

check() {
  local description="$1"
  local result="$2"   # 0 = pass, non-zero = fail
  if [ "$result" -eq 0 ]; then
    echo "PASS: $description"
    PASS=$((PASS + 1))
  else
    echo "FAIL: $description"
    FAIL=$((FAIL + 1))
  fi
}

# Test 1: monolith/pom.xml must NOT contain spring-boot-devtools
grep -q 'spring-boot-devtools' monolith/pom.xml && FOUND_MONOLITH=1 || FOUND_MONOLITH=0
check "monolith/pom.xml does not contain spring-boot-devtools" "$FOUND_MONOLITH"

# Test 2: activemq/pom.xml must NOT contain spring-boot-devtools
grep -q 'spring-boot-devtools' activemq/pom.xml && FOUND_ACTIVEMQ=1 || FOUND_ACTIVEMQ=0
check "activemq/pom.xml does not contain spring-boot-devtools" "$FOUND_ACTIVEMQ"

# Test 3: grep -r across both files returns no results (exit code 1 means no match = good)
grep -r 'spring-boot-devtools' monolith/pom.xml activemq/pom.xml && FOUND_GREP=1 || FOUND_GREP=0
check "grep -r 'spring-boot-devtools' monolith/pom.xml activemq/pom.xml returns no results" "$FOUND_GREP"

echo ""
echo "Results: $PASS passed, $FAIL failed"

if [ "$FAIL" -gt 0 ]; then
  exit 1
fi
exit 0
