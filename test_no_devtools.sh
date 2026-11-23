#!/usr/bin/env bash
# TDD acceptance test: spring-boot-devtools must not appear in production POMs
set -e

REPO_ROOT="$(cd "$(dirname "$0")" && pwd)"
MONOLITH_POM="$REPO_ROOT/monolith/pom.xml"
ACTIVEMQ_POM="$REPO_ROOT/activemq/pom.xml"

echo "=== Test 1: grep for spring-boot-devtools in monolith/pom.xml and activemq/pom.xml ==="
if grep -r 'spring-boot-devtools' "$MONOLITH_POM" "$ACTIVEMQ_POM"; then
    echo "FAIL: spring-boot-devtools still present in one or both POMs"
    exit 1
else
    echo "PASS: No spring-boot-devtools found in production POMs"
fi
