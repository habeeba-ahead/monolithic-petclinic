#!/usr/bin/env bash
# TDD test: verifies spring-boot-devtools is absent from both pom.xml files.
# This script exits with a non-zero code (failing) until the dependency is removed.

set -euo pipefail

PASS=0
FAIL=0

check() {
  local description="$1"
  local result="$2"   # 0 = pass, non-zero = fail
  if [ "$result" -eq 0 ]; then
    echo "  PASS: $description"
    PASS=$((PASS + 1))
  else
    echo "  FAIL: $description"
    FAIL=$((FAIL + 1))
  fi
}

echo "=== Test: spring-boot-devtools absent from monolith/pom.xml ==="
grep -q 'spring-boot-devtools' monolith/pom.xml && check "No devtools in monolith/pom.xml" 1 || check "No devtools in monolith/pom.xml" 0

echo "=== Test: spring-boot-devtools absent from activemq/pom.xml ==="
grep -q 'spring-boot-devtools' activemq/pom.xml && check "No devtools in activemq/pom.xml" 1 || check "No devtools in activemq/pom.xml" 0

echo ""
echo "Results: $PASS passed, $FAIL failed"
if [ "$FAIL" -gt 0 ]; then
  exit 1
fi
exit 0
