#!/usr/bin/env bash
# Comprehensive verification tests for the application.properties audit task.
# Verifies all four acceptance criteria independently.

set -uo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
FINDINGS="$REPO_ROOT/docs/security/application-properties-audit.md"
MONOLITH_PROPS="$REPO_ROOT/monolith/src/main/resources/application.properties"
ACTIVEMQ_PROPS="$REPO_ROOT/activemq/src/main/resources/application.properties"

PASS_COUNT=0
FAIL_COUNT=0

pass() { echo "  PASS: $1"; PASS_COUNT=$((PASS_COUNT+1)); }
fail() { echo "  FAIL: $1"; FAIL_COUNT=$((FAIL_COUNT+1)); }

# Safe grep count: returns 0 if no matches (grep exits 1 when no match)
grep_count() {
  grep -icE "$1" "$2" 2>/dev/null || true
}

echo "============================================================"
echo " Acceptance Criteria Verification: application.properties Audit"
echo "============================================================"
echo ""

# ---------------------------------------------------------------
# AC-1: Both application.properties files have been read and their
#        contents documented in a local findings note (not committed)
# ---------------------------------------------------------------
echo "--- AC-1: Both files read and documented in a LOCAL (not committed) findings note ---"

# 1a. Findings note exists on disk
if [ -f "$FINDINGS" ]; then
  pass "Findings note exists at docs/security/application-properties-audit.md"
else
  fail "Findings note NOT found at docs/security/application-properties-audit.md"
fi

# 1b. Findings note is NOT committed to git (must be local only)
cd "$REPO_ROOT"
COMMITTED=$(git log --oneline -- docs/security/application-properties-audit.md 2>/dev/null | wc -l | tr -d ' \n')
if [ "$COMMITTED" -eq 0 ]; then
  pass "Findings note is NOT committed to git (local only)"
else
  fail "Findings note IS committed to git (should remain local)"
fi

# 1c. Monolith application.properties path is referenced in findings
if grep -q "monolith/src/main/resources/application.properties" "$FINDINGS" 2>/dev/null; then
  pass "Monolith application.properties path documented in findings"
else
  fail "Monolith application.properties path NOT documented in findings"
fi

# 1d. ActiveMQ application.properties path is referenced in findings
if grep -q "activemq/src/main/resources/application.properties" "$FINDINGS" 2>/dev/null; then
  pass "ActiveMQ application.properties path documented in findings"
else
  fail "ActiveMQ application.properties path NOT documented in findings"
fi

# 1e. Actual content from monolith props appears in findings
if grep -q "spring.datasource.schema" "$FINDINGS" 2>/dev/null; then
  pass "Monolith property content (spring.datasource.schema) documented"
else
  fail "Monolith property content NOT documented in findings"
fi

# 1f. Actual content from activemq props appears in findings
if grep -q "activemq.broker-url" "$FINDINGS" 2>/dev/null; then
  pass "ActiveMQ property content (activemq.broker-url) documented"
else
  fail "ActiveMQ property content NOT documented in findings"
fi

# 1g. Both source files actually exist (sanity check)
if [ -f "$MONOLITH_PROPS" ] && [ -f "$ACTIVEMQ_PROPS" ]; then
  pass "Both source application.properties files exist on disk"
else
  fail "One or both source application.properties files are missing"
fi

echo ""

# ---------------------------------------------------------------
# AC-2: Running git log --all --full-history -- '**/application.properties'
#        produces output that has been reviewed
# ---------------------------------------------------------------
echo "--- AC-2: git log output reviewed and documented ---"

# 2a. The git log command actually produces output
GIT_LOG_OUTPUT=$(git log --all --full-history -- '**/application.properties' 2>&1 || true)
if [ -n "$GIT_LOG_OUTPUT" ]; then
  pass "git log --all --full-history -- '**/application.properties' produces output"
else
  fail "git log produced no output (unexpected)"
fi

# 2b. The findings note documents that git log was run
if grep -qi "git log" "$FINDINGS" 2>/dev/null; then
  pass "Findings note documents that git log was executed"
else
  fail "Findings note does NOT document git log execution"
fi

# 2c. The findings note contains a commit hash from git log
HASH_FOUND=false
if grep -qE "[0-9a-f]{7,40}" "$FINDINGS" 2>/dev/null; then
  HASH_FOUND=true
fi
if $HASH_FOUND; then
  pass "Findings note contains a commit hash from git log output"
else
  fail "Findings note does NOT contain a commit hash from git log output"
fi

# 2d. The findings note contains a finding/conclusion about the git history
if grep -qi "history\|commit\|revision" "$FINDINGS" 2>/dev/null; then
  pass "Findings note contains a conclusion about git history"
else
  fail "Findings note does NOT contain a conclusion about git history"
fi

echo ""

# ---------------------------------------------------------------
# AC-3: Any hardcoded credentials found are flagged for rotation
#        before proceeding to later steps
# ---------------------------------------------------------------
echo "--- AC-3: Hardcoded credentials flagged for rotation ---"

# 3a. Credential assessment section exists
if grep -qi "credential\|password\|secret\|hardcoded" "$FINDINGS" 2>/dev/null; then
  pass "Findings note contains credential/secret assessment"
else
  fail "Findings note does NOT contain credential/secret assessment"
fi

# 3b. Verify actual files have no hardcoded passwords (ground truth check)
MONOLITH_SECRETS=0
ACTIVEMQ_SECRETS=0
if grep -qiE "password\s*=|secret\s*=|passwd\s*=|api.?key\s*=" "$MONOLITH_PROPS" 2>/dev/null; then
  MONOLITH_SECRETS=1
fi
if grep -qiE "password\s*=|secret\s*=|passwd\s*=|api.?key\s*=" "$ACTIVEMQ_PROPS" 2>/dev/null; then
  ACTIVEMQ_SECRETS=1
fi
if [ "$MONOLITH_SECRETS" -eq 0 ] && [ "$ACTIVEMQ_SECRETS" -eq 0 ]; then
  pass "Verified: no hardcoded passwords/secrets in either properties file"
else
  fail "Hardcoded secrets found in properties files (MONOLITH=$MONOLITH_SECRETS, ACTIVEMQ=$ACTIVEMQ_SECRETS)"
fi

# 3c. Findings note states the result of the credential check (none found OR flagged)
if grep -qi "no.*credential\|no.*password\|no.*secret\|none found\|zero match\|0 match\|rotation.*required\|rotat" "$FINDINGS" 2>/dev/null; then
  pass "Findings note states credential check result and rotation guidance"
else
  fail "Findings note does NOT state credential check result or rotation guidance"
fi

# 3d. Remediation/rotation section exists
if grep -qi "remediat\|rotat\|purge\|git-filter-repo\|checklist" "$FINDINGS" 2>/dev/null; then
  pass "Findings note contains remediation/rotation guidance section"
else
  fail "Findings note does NOT contain remediation/rotation guidance"
fi

echo ""

# ---------------------------------------------------------------
# AC-4: A list of all environment-specific values (URLs, ports,
#        passwords) is produced for use in Step 14
# ---------------------------------------------------------------
echo "--- AC-4: Environment-specific values list produced for Step 14 ---"

# 4a. Environment-specific section exists
if grep -qi "environment.specific\|env.specific\|step 14\|environment" "$FINDINGS" 2>/dev/null; then
  pass "Findings note contains environment-specific values section"
else
  fail "Findings note does NOT contain environment-specific values section"
fi

# 4b. Broker URL (URL) is listed
if grep -q "tcp://localhost:61616\|tcp://0.0.0.0:61616\|broker.url\|broker-url" "$FINDINGS" 2>/dev/null; then
  pass "Broker URL (environment-specific URL) is listed"
else
  fail "Broker URL NOT listed in environment-specific values"
fi

# 4c. Server port 8080 (monolith) is listed
if grep -q "8080" "$FINDINGS" 2>/dev/null; then
  pass "Server port 8080 (monolith) is listed"
else
  fail "Server port 8080 NOT listed in environment-specific values"
fi

# 4d. Server port 8081 (activemq) is listed
if grep -q "8081" "$FINDINGS" 2>/dev/null; then
  pass "Server port 8081 (activemq) is listed"
else
  fail "Server port 8081 NOT listed in environment-specific values"
fi

# 4e. spring.activemq.broker-url is listed (monolith URL)
if grep -q "spring.activemq.broker-url" "$FINDINGS" 2>/dev/null; then
  pass "spring.activemq.broker-url is listed"
else
  fail "spring.activemq.broker-url NOT listed"
fi

# 4f. server.port is listed
if grep -q "server.port" "$FINDINGS" 2>/dev/null; then
  pass "server.port is listed"
else
  fail "server.port NOT listed"
fi

# 4g. hawtio.authenticationEnabled is listed (security misconfiguration)
if grep -q "hawtio.authenticationEnabled\|hawtio" "$FINDINGS" 2>/dev/null; then
  pass "hawtio.authenticationEnabled is listed (security misconfiguration)"
else
  fail "hawtio.authenticationEnabled NOT listed"
fi

# 4h. management.endpoints exposure is listed
if grep -q "management.endpoints" "$FINDINGS" 2>/dev/null; then
  pass "management.endpoints exposure settings are listed"
else
  fail "management.endpoints exposure settings NOT listed"
fi

# 4i. The list is usable for Step 14 (has enough entries - at least 5 properties)
PROP_COUNT=0
for pattern in "server.port" "broker.url" "hawtio" "management.endpoints" "datasource" "jms" "thymeleaf" "jpa" "messages"; do
  if grep -q "$pattern" "$FINDINGS" 2>/dev/null; then
    PROP_COUNT=$((PROP_COUNT+1))
  fi
done
if [ "$PROP_COUNT" -ge 5 ]; then
  pass "Environment-specific values list has sufficient entries ($PROP_COUNT distinct property types) for Step 14"
else
  fail "Environment-specific values list has too few entries ($PROP_COUNT) for Step 14"
fi

echo ""
echo "============================================================"
echo " Results: $PASS_COUNT passed, $FAIL_COUNT failed"
echo "============================================================"

if [ "$FAIL_COUNT" -gt 0 ]; then
  exit 1
fi
echo "All acceptance criteria verified successfully."
