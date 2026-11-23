#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# verify_config_audit.sh
# Verifies all acceptance criteria for the "Audit application.properties and
# document config gaps" task.
# ---------------------------------------------------------------------------
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
AUDIT_FILE="$REPO_ROOT/docs/config-audit.md"
MONOLITH_PROPS="$REPO_ROOT/monolith/src/main/resources/application.properties"
ACTIVEMQ_PROPS="$REPO_ROOT/activemq/src/main/resources/application.properties"
MONOLITH_SRC="$REPO_ROOT/monolith/src/"

PASS=0
FAIL=0

pass() { echo "  [PASS] $1"; PASS=$((PASS+1)); }
fail() { echo "  [FAIL] $1"; FAIL=$((FAIL+1)); }

# ============================================================
# CRITERION 1 – docs/config-audit.md exists and lists every
#               property key found in both application.properties
# ============================================================
echo ""
echo "=== CRITERION 1: docs/config-audit.md exists and lists all property keys ==="

# 1a. File exists
if [[ -f "$AUDIT_FILE" ]]; then
  pass "docs/config-audit.md exists"
else
  fail "docs/config-audit.md does NOT exist"
fi

# Helper: extract property keys from a .properties file (non-comment, non-blank lines)
extract_keys() {
  grep -E '^[^#[:space:]][^=]*=' "$1" | sed 's/=.*//' | sed 's/[[:space:]]*$//'
}

# 1b. Every key from monolith/application.properties appears in the audit doc
echo ""
echo "  -- Checking monolith property keys --"
while IFS= read -r key; do
  [[ -z "$key" ]] && continue
  if grep -qF "$key" "$AUDIT_FILE"; then
    pass "Monolith key '$key' is documented"
  else
    fail "Monolith key '$key' is MISSING from audit doc"
  fi
done < <(extract_keys "$MONOLITH_PROPS")

# 1c. Every key from activemq/application.properties appears in the audit doc
echo ""
echo "  -- Checking activemq property keys --"
while IFS= read -r key; do
  [[ -z "$key" ]] && continue
  if grep -qF "$key" "$AUDIT_FILE"; then
    pass "ActiveMQ key '$key' is documented"
  else
    fail "ActiveMQ key '$key' is MISSING from audit doc"
  fi
done < <(extract_keys "$ACTIVEMQ_PROPS")

# ============================================================
# CRITERION 2 – Hardcoded credentials are flagged with a TODO
#               comment and a placeholder for Secret Manager
# ============================================================
echo ""
echo "=== CRITERION 2: Hardcoded credentials flagged with TODO + Secret Manager placeholder ==="

# 2a. monolith props: spring.activemq.broker-url has a TODO comment
if grep -qE '^#.*TODO.*spring\.activemq\.broker-url' "$MONOLITH_PROPS"; then
  pass "monolith/application.properties: spring.activemq.broker-url has a TODO comment"
else
  fail "monolith/application.properties: spring.activemq.broker-url is MISSING a TODO comment"
fi

# 2b. monolith props: datasource credentials have TODO + Secret Manager placeholder
if grep -qE 'TODO.*spring\.datasource\.(url|username|password)' "$MONOLITH_PROPS"; then
  pass "monolith/application.properties: datasource credential TODOs present"
else
  fail "monolith/application.properties: datasource credential TODOs are MISSING"
fi

if grep -qi 'Secret Manager' "$MONOLITH_PROPS"; then
  pass "monolith/application.properties: Secret Manager placeholder present"
else
  fail "monolith/application.properties: Secret Manager placeholder is MISSING"
fi

# 2c. activemq props: broker-url has a TODO comment
if grep -qE '^#.*TODO.*activemq\.broker-url' "$ACTIVEMQ_PROPS"; then
  pass "activemq/application.properties: activemq.broker-url has a TODO comment"
else
  fail "activemq/application.properties: activemq.broker-url is MISSING a TODO comment"
fi

# 2d. activemq props: hawtio.authenticationEnabled has a TODO comment
if grep -qE '^#.*TODO.*hawtio\.authenticationEnabled' "$ACTIVEMQ_PROPS"; then
  pass "activemq/application.properties: hawtio.authenticationEnabled has a TODO comment"
else
  fail "activemq/application.properties: hawtio.authenticationEnabled is MISSING a TODO comment"
fi

# 2e. activemq props: Secret Manager mentioned
if grep -qi 'Secret Manager' "$ACTIVEMQ_PROPS"; then
  pass "activemq/application.properties: Secret Manager placeholder present"
else
  fail "activemq/application.properties: Secret Manager placeholder is MISSING"
fi

# 2f. Audit doc flags credentials with TODO + Secret Manager
if grep -qi 'TODO' "$AUDIT_FILE" && grep -qi 'Secret Manager' "$AUDIT_FILE"; then
  pass "docs/config-audit.md: contains TODO flags and Secret Manager references"
else
  fail "docs/config-audit.md: MISSING TODO flags or Secret Manager references"
fi

# ============================================================
# CRITERION 3 – Audit doc confirms whether @JmsListener or
#               JmsTemplate usages exist in monolith/src/
#               (run: grep -r 'JmsListener\|JmsTemplate' monolith/src/)
# ============================================================
echo ""
echo "=== CRITERION 3: Audit doc confirms JmsListener/JmsTemplate scan result ==="

# 3a. Run the actual grep and capture the result
JMS_GREP_OUTPUT=$(grep -r 'JmsListener\|JmsTemplate' "$MONOLITH_SRC" 2>&1 || true)
JMS_FOUND=false
if [[ -n "$JMS_GREP_OUTPUT" ]]; then
  JMS_FOUND=true
fi

# 3b. Audit doc must mention the grep command or its equivalent
if grep -qE 'JmsListener|JmsTemplate' "$AUDIT_FILE"; then
  pass "docs/config-audit.md: mentions JmsListener/JmsTemplate scan"
else
  fail "docs/config-audit.md: does NOT mention JmsListener/JmsTemplate scan"
fi

# 3c. Audit doc must state the correct outcome of the scan
if [[ "$JMS_FOUND" == "true" ]]; then
  # Matches were found – audit doc should say so
  if grep -qiE 'JmsListener|JmsTemplate' "$AUDIT_FILE"; then
    pass "docs/config-audit.md: correctly documents JMS usages found in monolith/src/"
  else
    fail "docs/config-audit.md: does NOT document JMS usages that were found"
  fi
else
  # No matches – audit doc should confirm absence
  if grep -qiE 'no.*JmsListener|no.*JmsTemplate|JmsListener.*not found|JmsTemplate.*not found|NO_MATCHES|no .*(JmsListener|JmsTemplate).*found|not.*found.*JmsListener|not.*found.*JmsTemplate|zero.*@JmsListener|absence.*JMS|no.*@JmsListener.*consumers|no.*JmsTemplate.*producers' "$AUDIT_FILE"; then
    pass "docs/config-audit.md: correctly confirms NO JmsListener/JmsTemplate in monolith/src/"
  else
    fail "docs/config-audit.md: does NOT confirm absence of JmsListener/JmsTemplate in monolith/src/"
  fi
fi

# 3d. Audit doc must contain the grep command itself (or equivalent)
if grep -qE "grep.*JmsListener.*JmsTemplate|grep.*JmsTemplate.*JmsListener" "$AUDIT_FILE"; then
  pass "docs/config-audit.md: includes the grep command used for the JMS scan"
else
  fail "docs/config-audit.md: does NOT include the grep command for the JMS scan"
fi

# ============================================================
# SUMMARY
# ============================================================
echo ""
echo "============================================================"
echo "  Results: $PASS passed, $FAIL failed"
echo "============================================================"

if [[ $FAIL -gt 0 ]]; then
  exit 1
fi
exit 0
