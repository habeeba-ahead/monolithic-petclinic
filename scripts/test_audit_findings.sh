#!/usr/bin/env bash
# TDD test script for the application.properties audit findings note.
# Run this script; it exits non-zero (FAIL) until the findings note exists
# and satisfies every acceptance criterion below.

set -euo pipefail

FINDINGS="docs/security/application-properties-audit.md"
REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
FINDINGS_PATH="$REPO_ROOT/$FINDINGS"

pass() { echo "  PASS: $1"; }
fail() { echo "  FAIL: $1"; exit 1; }

echo "=== Audit findings note tests ==="

# AC-1: findings note exists
[ -f "$FINDINGS_PATH" ] || fail "Findings note not found at $FINDINGS"
pass "Findings note exists"

# AC-2: both files are documented
grep -q "monolith/src/main/resources/application.properties" "$FINDINGS_PATH" \
  || fail "Monolith application.properties not referenced in findings"
grep -q "activemq/src/main/resources/application.properties" "$FINDINGS_PATH" \
  || fail "ActiveMQ application.properties not referenced in findings"
pass "Both application.properties files are documented"

# AC-3: git log output is documented
grep -qi "git log" "$FINDINGS_PATH" \
  || fail "Git history review not documented in findings"
pass "Git history review is documented"

# AC-4: hardcoded credential assessment is present
grep -qi "credential\|password\|secret\|hardcoded" "$FINDINGS_PATH" \
  || fail "Credential/secret assessment not present in findings"
pass "Credential/secret assessment is present"

# AC-5: environment-specific values section exists (for Step 14)
grep -qi "environment\|step 14\|env-specific\|environment-specific" "$FINDINGS_PATH" \
  || fail "Environment-specific values section not present in findings"
pass "Environment-specific values section is present"

# AC-6: broker URL is listed (confirmed by JmsConfig.java @Value usage)
grep -q "activemq.broker-url\|broker-url\|broker_url" "$FINDINGS_PATH" \
  || fail "activemq.broker-url not listed in findings"
pass "activemq.broker-url is listed"

# AC-7: server ports are listed
grep -q "8080\|8081" "$FINDINGS_PATH" \
  || fail "Server ports (8080/8081) not listed in findings"
pass "Server ports are listed"

# AC-8: rotation / remediation guidance is present
grep -qi "rotat\|remediat\|purge\|git-filter-repo\|no.*credential\|no.*password\|no.*secret" "$FINDINGS_PATH" \
  || fail "Rotation/remediation guidance not present in findings"
pass "Rotation/remediation guidance is present"

echo ""
echo "All tests passed."
