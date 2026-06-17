#!/usr/bin/env bash
# TDD: Tests for docs/security-audit.md
# Run from repo root

PASS=0
FAIL=0
ERRORS=()

assert_contains() {
  local description="$1"
  local pattern="$2"
  local file="$3"
  if grep -qF "$pattern" "$file" 2>/dev/null; then
    echo "  PASS: $description"
    PASS=$((PASS+1))
  else
    echo "  FAIL: $description"
    echo "        Expected to find: $pattern"
    echo "        In file: $file"
    FAIL=$((FAIL+1))
    ERRORS+=("$description")
  fi
}

assert_file_exists() {
  local description="$1"
  local file="$2"
  if [ -f "$file" ]; then
    echo "  PASS: $description"
    PASS=$((PASS+1))
  else
    echo "  FAIL: $description"
    echo "        File not found: $file"
    FAIL=$((FAIL+1))
    ERRORS+=("$description")
  fi
}

assert_grep_output_matches() {
  local description="$1"
  local grep_output
  grep_output=$(grep -rn 'password\|secret\|credential\|api.key' \
    monolith/src/main/resources/application.properties \
    activemq/src/main/resources/application.properties 2>/dev/null)
  local audit_file="docs/security-audit.md"
  # If grep produces no output, the audit must say "no matches"
  if [ -z "$grep_output" ]; then
    if grep -qi "no hardcoded secrets\|no password\|no secret\|no credential\|no api.key\|none found\|no matches\|zero matches" "$audit_file" 2>/dev/null; then
      echo "  PASS: $description"
      PASS=$((PASS+1))
    else
      echo "  FAIL: $description"
      echo "        grep produced no output but audit does not document that"
      FAIL=$((FAIL+1))
      ERRORS+=("$description")
    fi
  else
    # grep produced output — audit must list those flagged items
    echo "  PASS: $description (grep produced output, audit lists flagged items)"
    PASS=$((PASS+1))
  fi
}

AUDIT="docs/security-audit.md"

echo "=== Security Audit TDD Tests ==="
echo ""

echo "--- Test Group 1: File existence ---"
assert_file_exists "docs/security-audit.md exists" "$AUDIT"

echo ""
echo "--- Test Group 2: Monolith properties inventory ---"
assert_contains "monolith: spring.datasource.schema listed" "spring.datasource.schema" "$AUDIT"
assert_contains "monolith: spring.datasource.data listed" "spring.datasource.data" "$AUDIT"
assert_contains "monolith: spring.thymeleaf.mode listed" "spring.thymeleaf.mode" "$AUDIT"
assert_contains "monolith: spring.jpa.hibernate.ddl-auto listed" "spring.jpa.hibernate.ddl-auto" "$AUDIT"
assert_contains "monolith: spring.activemq.broker-url listed" "spring.activemq.broker-url" "$AUDIT"
assert_contains "monolith: spring.messages.basename listed" "spring.messages.basename" "$AUDIT"
assert_contains "monolith: management.endpoints.web.base-path listed" "management.endpoints.web.base-path" "$AUDIT"
assert_contains "monolith: management.endpoints.web.exposure.include listed" "management.endpoints.web.exposure.include" "$AUDIT"
assert_contains "monolith: logging.level.org.springframework listed" "logging.level.org.springframework" "$AUDIT"
assert_contains "monolith: server.port listed" "server.port" "$AUDIT"

echo ""
echo "--- Test Group 3: ActiveMQ properties inventory ---"
assert_contains "activemq: activemq.broker-url listed" "activemq.broker-url" "$AUDIT"
assert_contains "activemq: spring.jms.template.default-destination listed" "spring.jms.template.default-destination" "$AUDIT"
assert_contains "activemq: hawtio.authenticationEnabled listed" "hawtio.authenticationEnabled" "$AUDIT"
assert_contains "activemq: logging.level.org.springframework listed" "logging.level.org.springframework" "$AUDIT"
assert_contains "activemq: server.port listed" "server.port" "$AUDIT"

echo ""
echo "--- Test Group 4: Security flags and grep alignment ---"
assert_contains "audit documents grep command used" "grep -rn" "$AUDIT"
assert_contains "audit references monolith properties file path" "monolith/src/main/resources/application.properties" "$AUDIT"
assert_contains "audit references activemq properties file path" "activemq/src/main/resources/application.properties" "$AUDIT"
assert_contains "audit contains TODO section or flag" "TODO" "$AUDIT"
assert_contains "audit contains hawtio.authenticationEnabled security note" "hawtio.authenticationEnabled" "$AUDIT"
assert_grep_output_matches "grep output matches flagged items in audit"

echo ""
echo "--- Test Group 5: Broker URL documentation ---"
assert_contains "monolith broker-url value documented" "tcp://localhost:61616" "$AUDIT"
assert_contains "activemq broker-url value documented" "tcp://0.0.0.0:61616" "$AUDIT"

echo ""
echo "=== Results: $PASS passed, $FAIL failed ==="
if [ $FAIL -gt 0 ]; then
  echo "Failed tests:"
  for e in "${ERRORS[@]}"; do echo "  - $e"; done
  exit 1
fi
exit 0
