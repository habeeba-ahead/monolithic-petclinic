#!/usr/bin/env bash
# TDD test script for config-audit.md acceptance criteria
# Run from repo root. Exit code 0 = all tests pass, non-zero = failure.

PASS=0
FAIL=0
ERRORS=()

assert_contains() {
  local description="$1"
  local file="$2"
  local pattern="$3"
  if grep -qF "$pattern" "$file" 2>/dev/null; then
    echo "  PASS: $description"
    PASS=$((PASS + 1))
  else
    echo "  FAIL: $description"
    echo "        Expected to find: '$pattern' in $file"
    FAIL=$((FAIL + 1))
    ERRORS+=("$description")
  fi
}

assert_file_exists() {
  local description="$1"
  local file="$2"
  if [ -f "$file" ]; then
    echo "  PASS: $description"
    PASS=$((PASS + 1))
  else
    echo "  FAIL: $description"
    echo "        File not found: $file"
    FAIL=$((FAIL + 1))
    ERRORS+=("$description")
  fi
}

assert_not_contains() {
  local description="$1"
  local file="$2"
  local pattern="$3"
  if ! grep -qF "$pattern" "$file" 2>/dev/null; then
    echo "  PASS: $description"
    PASS=$((PASS + 1))
  else
    echo "  FAIL: $description"
    echo "        Did NOT expect to find: '$pattern' in $file"
    FAIL=$((FAIL + 1))
    ERRORS+=("$description")
  fi
}

echo "========================================"
echo " Config Audit TDD Tests"
echo "========================================"

# ── Test Group 1: docs/config-audit.md exists ──────────────────────────────
echo ""
echo "Group 1: File existence"
assert_file_exists "docs/config-audit.md exists" "docs/config-audit.md"

# ── Test Group 2: All monolith property keys documented ────────────────────
echo ""
echo "Group 2: Monolith property keys present in audit doc"
AUDIT="docs/config-audit.md"
assert_contains "monolith spring.datasource.schema documented"        "$AUDIT" "spring.datasource.schema"
assert_contains "monolith spring.datasource.data documented"          "$AUDIT" "spring.datasource.data"
assert_contains "monolith spring.thymeleaf.mode documented"           "$AUDIT" "spring.thymeleaf.mode"
assert_contains "monolith spring.jpa.hibernate.ddl-auto documented"   "$AUDIT" "spring.jpa.hibernate.ddl-auto"
assert_contains "monolith spring.activemq.broker-url documented"      "$AUDIT" "spring.activemq.broker-url"
assert_contains "monolith spring.messages.basename documented"        "$AUDIT" "spring.messages.basename"
assert_contains "monolith management.endpoints.web.base-path documented (monolith)" "$AUDIT" "management.endpoints.web.base-path"
assert_contains "monolith management.endpoints.web.exposure.include documented" "$AUDIT" "management.endpoints.web.exposure.include"
assert_contains "monolith logging.level.org.springframework documented" "$AUDIT" "logging.level.org.springframework"
assert_contains "monolith server.port documented"                     "$AUDIT" "server.port"

# ── Test Group 3: All activemq property keys documented ────────────────────
echo ""
echo "Group 3: ActiveMQ module property keys present in audit doc"
assert_contains "activemq activemq.broker-url documented"             "$AUDIT" "activemq.broker-url"
assert_contains "activemq spring.jms.template.default-destination documented" "$AUDIT" "spring.jms.template.default-destination"
assert_contains "activemq hawtio.authenticationEnabled documented"    "$AUDIT" "hawtio.authenticationEnabled"

# ── Test Group 4: Hardcoded credentials flagged with TODO ──────────────────
echo ""
echo "Group 4: Hardcoded credentials / security risks flagged with TODO"
assert_contains "hawtio.authenticationEnabled=false flagged with TODO" "$AUDIT" "TODO"
assert_contains "management wildcard exposure flagged"                 "$AUDIT" "exposure.include=*"
assert_contains "Secret Manager placeholder present"                   "$AUDIT" "Secret Manager"

# ── Test Group 5: JMS usage grep result documented ─────────────────────────
echo ""
echo "Group 5: JMS usage grep result documented"
assert_contains "JmsListener grep result documented"  "$AUDIT" "JmsListener"
assert_contains "JmsTemplate grep result documented"  "$AUDIT" "JmsTemplate"
assert_contains "@EnableJms annotation finding documented" "$AUDIT" "@EnableJms"

# ── Test Group 6: monolith application.properties has TODO comment ─────────
echo ""
echo "Group 6: monolith application.properties has TODO credential comment"
MONOLITH_PROPS="monolith/src/main/resources/application.properties"
assert_contains "monolith props has TODO for broker-url" "$MONOLITH_PROPS" "TODO"

# ── Test Group 7: activemq application.properties has TODO comment ─────────
echo ""
echo "Group 7: activemq application.properties has TODO credential comment"
ACTIVEMQ_PROPS="activemq/src/main/resources/application.properties"
assert_contains "activemq props has TODO for hawtio auth" "$ACTIVEMQ_PROPS" "TODO"

# ── Summary ────────────────────────────────────────────────────────────────
echo ""
echo "========================================"
echo " Results: $PASS passed, $FAIL failed"
echo "========================================"
if [ ${#ERRORS[@]} -gt 0 ]; then
  echo " Failed tests:"
  for err in "${ERRORS[@]}"; do
    echo "   - $err"
  done
fi

[ $FAIL -eq 0 ]
