#!/usr/bin/env bash
# Acceptance Criteria Verification Tests
# Verifies all three acceptance criteria for the security audit task.

PASS=0
FAIL=0
ERRORS=()

AUDIT="docs/security-audit.md"
MONOLITH_PROPS="monolith/src/main/resources/application.properties"
ACTIVEMQ_PROPS="activemq/src/main/resources/application.properties"

pass() {
  echo "  PASS: $1"
  PASS=$((PASS+1))
}

fail() {
  echo "  FAIL: $1"
  echo "        $2"
  FAIL=$((FAIL+1))
  ERRORS+=("$1")
}

assert_file_exists() {
  local desc="$1" file="$2"
  if [ -f "$file" ]; then pass "$desc"; else fail "$desc" "File not found: $file"; fi
}

assert_contains() {
  local desc="$1" pattern="$2" file="$3"
  if grep -qF "$pattern" "$file" 2>/dev/null; then
    pass "$desc"
  else
    fail "$desc" "Pattern '$pattern' not found in $file"
  fi
}

assert_not_empty() {
  local desc="$1"; shift
  local val="$*"
  if [ -n "$val" ]; then pass "$desc"; else fail "$desc" "Value was empty"; fi
}

echo "========================================================"
echo " Acceptance Criteria Verification — Security Audit"
echo "========================================================"
echo ""

# ─────────────────────────────────────────────────────────────
# CRITERION 1: docs/security-audit.md exists and lists every
#              property key from both application.properties files
# ─────────────────────────────────────────────────────────────
echo "--- Criterion 1: Documented inventory in docs/security-audit.md ---"

assert_file_exists "docs/security-audit.md file exists" "$AUDIT"

# Extract all active (non-comment, non-blank) property keys from both files
extract_keys() {
  grep -v '^\s*#' "$1" | grep -v '^\s*$' | sed 's/=.*//' | sed 's/[[:space:]]//g'
}

MONOLITH_KEYS=$(extract_keys "$MONOLITH_PROPS")
ACTIVEMQ_KEYS=$(extract_keys "$ACTIVEMQ_PROPS")

echo ""
echo "  [Monolith property keys found in application.properties]"
for key in $MONOLITH_KEYS; do
  assert_contains "  monolith key '$key' listed in audit" "$key" "$AUDIT"
done

echo ""
echo "  [ActiveMQ property keys found in application.properties]"
for key in $ACTIVEMQ_KEYS; do
  assert_contains "  activemq key '$key' listed in audit" "$key" "$AUDIT"
done

# Verify both file paths are referenced in the audit
assert_contains "audit references monolith file path" "$MONOLITH_PROPS" "$AUDIT"
assert_contains "audit references activemq file path" "$ACTIVEMQ_PROPS" "$AUDIT"

echo ""

# ─────────────────────────────────────────────────────────────
# CRITERION 2: Properties containing password/secret/key/credential
#              are flagged with TODO in the .properties file AND
#              listed in docs/security-audit.md
# ─────────────────────────────────────────────────────────────
echo "--- Criterion 2: Sensitive properties flagged with TODO ---"

# Run the grep to find sensitive properties in both files
SENSITIVE_LINES=$(grep -n 'password\|secret\|credential\|api\.key\|api_key' \
  "$MONOLITH_PROPS" "$ACTIVEMQ_PROPS" 2>/dev/null)

if [ -z "$SENSITIVE_LINES" ]; then
  # No sensitive properties found — audit must document this fact
  echo "  [No lines matching password|secret|credential|api.key found in properties files]"
  if grep -qi "no hardcoded secrets\|no password\|no secret\|no credential\|no api.key\|none found\|no matches\|zero matches" "$AUDIT" 2>/dev/null; then
    pass "audit documents that no password/secret/credential/api.key properties exist"
  else
    fail "audit documents that no password/secret/credential/api.key properties exist" \
         "Audit does not state that grep returned zero matches"
  fi
  # Verify the audit still contains a TODO section (for other security issues)
  assert_contains "audit contains TODO items for security issues found" "TODO" "$AUDIT"
else
  # Sensitive properties exist — each must have a TODO comment in the .properties file
  # and be listed in the audit
  echo "  [Sensitive lines found — verifying TODO flags and audit listing]"
  while IFS= read -r line; do
    # Extract filename and line content
    file=$(echo "$line" | cut -d: -f1)
    lineno=$(echo "$line" | cut -d: -f2)
    content=$(echo "$line" | cut -d: -f3-)
    key=$(echo "$content" | sed 's/=.*//' | sed 's/[[:space:]]//g')

    # Check for TODO comment near the property in the .properties file
    # Look within 3 lines before/after for a TODO comment
    context=$(sed -n "$((lineno-3)),$((lineno+3))p" "$file" 2>/dev/null)
    if echo "$context" | grep -qi "TODO"; then
      pass "sensitive property '$key' in $file has nearby TODO comment"
    else
      fail "sensitive property '$key' in $file has nearby TODO comment" \
           "No TODO comment found within 3 lines of line $lineno in $file"
    fi

    # Check that the key is listed in the audit
    assert_contains "sensitive property '$key' listed in audit" "$key" "$AUDIT"
  done <<< "$SENSITIVE_LINES"
fi

echo ""

# ─────────────────────────────────────────────────────────────
# CRITERION 3: grep output matches flagged items in audit
# ─────────────────────────────────────────────────────────────
echo "--- Criterion 3: grep output matches flagged items in docs/security-audit.md ---"

# Run the exact grep command from the acceptance criteria
GREP_OUTPUT=$(grep -rn 'password\|secret\|credential\|api.key' \
  "$MONOLITH_PROPS" \
  "$ACTIVEMQ_PROPS" 2>/dev/null)

echo "  [Exact grep command: grep -rn 'password\\|secret\\|credential\\|api.key' $MONOLITH_PROPS $ACTIVEMQ_PROPS]"

if [ -z "$GREP_OUTPUT" ]; then
  echo "  [grep returned no output — verifying audit documents this]"
  # Audit must state that the grep returned zero/no matches
  if grep -qi "zero matches\|no matches\|no hardcoded secrets\|no password\|no secret\|no credential\|no api.key\|none found" "$AUDIT" 2>/dev/null; then
    pass "audit correctly documents that grep returned zero matches"
  else
    fail "audit correctly documents that grep returned zero matches" \
         "Audit does not document that grep returned zero/no matches"
  fi

  # Audit must document the grep command itself
  assert_contains "audit documents the grep command used" "grep -rn" "$AUDIT"
  assert_contains "audit includes 'password' in grep pattern documentation" "password" "$AUDIT"
  assert_contains "audit includes 'secret' in grep pattern documentation" "secret" "$AUDIT"
  assert_contains "audit includes 'credential' in grep pattern documentation" "credential" "$AUDIT"
  assert_contains "audit includes 'api.key' in grep pattern documentation" "api.key" "$AUDIT"

else
  echo "  [grep returned output — verifying each match is listed in audit]"
  # Every line returned by grep must appear in the audit
  while IFS= read -r line; do
    # Extract the property key from the grep output line
    key=$(echo "$line" | sed 's/.*:\([^=]*\)=.*/\1/' | sed 's/[[:space:]]//g')
    assert_contains "grep-matched property '$key' is listed in audit" "$key" "$AUDIT"
  done <<< "$GREP_OUTPUT"

  # Audit must also contain TODO flags for each matched item
  assert_contains "audit contains TODO items for flagged properties" "TODO" "$AUDIT"
fi

echo ""

# ─────────────────────────────────────────────────────────────
# Additional structural checks on the audit document
# ─────────────────────────────────────────────────────────────
echo "--- Additional structural checks on docs/security-audit.md ---"

assert_contains "audit has a title/heading" "Security Audit" "$AUDIT"
assert_contains "audit covers monolith file" "monolith" "$AUDIT"
assert_contains "audit covers activemq file" "activemq" "$AUDIT"
assert_contains "audit has a flagged items section or summary" "Flagged" "$AUDIT"

# Verify the audit lists the hawtio.authenticationEnabled=false as a security concern
assert_contains "audit flags hawtio.authenticationEnabled as security concern" "hawtio.authenticationEnabled" "$AUDIT"

# Verify the audit lists management.endpoints.web.exposure.include=* as a security concern
assert_contains "audit flags management.endpoints.web.exposure.include" "management.endpoints.web.exposure.include" "$AUDIT"

# Verify broker URLs are documented
assert_contains "audit documents monolith broker URL value" "tcp://localhost:61616" "$AUDIT"
assert_contains "audit documents activemq broker URL value" "tcp://0.0.0.0:61616" "$AUDIT"

echo ""
echo "========================================================"
echo " Results: $PASS passed, $FAIL failed"
echo "========================================================"

if [ $FAIL -gt 0 ]; then
  echo ""
  echo "Failed tests:"
  for e in "${ERRORS[@]}"; do echo "  - $e"; done
  exit 1
fi
exit 0
