#!/usr/bin/env bash
# Verification script for "Externalize all config to environment variables" acceptance criteria

REPO_ROOT="$(cd "$(dirname "$0")" && pwd)"
PASS=0
FAIL=0

pass() { echo "  [PASS] $1"; PASS=$((PASS + 1)); }
fail() { echo "  [FAIL] $1"; FAIL=$((FAIL + 1)); }

echo "=== Acceptance Criteria Verification ==="
echo ""

# -----------------------------------------------------------------------
# Criterion 1: grep -r 'localhost' monolith/src/main/resources/ returns
#              only placeholder defaults (e.g., ${DB_HOST:localhost}),
#              not bare hardcoded values
# -----------------------------------------------------------------------
echo "--- Criterion 1: monolith/src/main/resources/ localhost check ---"
MONOLITH_LOCALHOST=$(grep -r 'localhost' "$REPO_ROOT/monolith/src/main/resources/" 2>/dev/null || true)
if [ -z "$MONOLITH_LOCALHOST" ]; then
  pass "No 'localhost' occurrences found in monolith/src/main/resources/ (all externalized)"
else
  ALL_PLACEHOLDER=true
  while IFS= read -r line; do
    # Strip filename prefix from grep output
    content="${line#*:}"
    # Skip comment lines
    trimmed="${content#"${content%%[![:space:]]*}"}"
    if [[ "$trimmed" == \#* ]]; then
      continue
    fi
    # Must match ${VAR:...localhost...}
    if echo "$trimmed" | grep -qP '\$\{[^}]+:.*localhost.*\}'; then
      pass "Placeholder default found: $trimmed"
    else
      fail "Bare localhost found (not in placeholder): $trimmed"
      ALL_PLACEHOLDER=false
    fi
  done <<< "$MONOLITH_LOCALHOST"
  if [ "$ALL_PLACEHOLDER" = true ]; then
    pass "All 'localhost' occurrences in monolith/src/main/resources/ are placeholder defaults"
  fi
fi

echo ""

# -----------------------------------------------------------------------
# Criterion 2: grep -r 'localhost' activemq/src/main/resources/ returns
#              only placeholder defaults
# -----------------------------------------------------------------------
echo "--- Criterion 2: activemq/src/main/resources/ localhost check ---"
ACTIVEMQ_LOCALHOST=$(grep -r 'localhost' "$REPO_ROOT/activemq/src/main/resources/" 2>/dev/null || true)
if [ -z "$ACTIVEMQ_LOCALHOST" ]; then
  pass "No 'localhost' occurrences found in activemq/src/main/resources/ (all externalized)"
else
  ALL_PLACEHOLDER=true
  while IFS= read -r line; do
    content="${line#*:}"
    trimmed="${content#"${content%%[![:space:]]*}"}"
    if [[ "$trimmed" == \#* ]]; then
      continue
    fi
    if echo "$trimmed" | grep -qP '\$\{[^}]+:.*localhost.*\}'; then
      pass "Placeholder default found: $trimmed"
    else
      fail "Bare localhost found (not in placeholder): $trimmed"
      ALL_PLACEHOLDER=false
    fi
  done <<< "$ACTIVEMQ_LOCALHOST"
  if [ "$ALL_PLACEHOLDER" = true ]; then
    pass "All 'localhost' occurrences in activemq/src/main/resources/ are placeholder defaults"
  fi
fi

echo ""

# -----------------------------------------------------------------------
# Criterion 3: docs/env-vars.md exists and documents every env variable
#              with its purpose and default value
# -----------------------------------------------------------------------
echo "--- Criterion 3: docs/env-vars.md exists and is complete ---"
ENV_VARS_DOC="$REPO_ROOT/docs/env-vars.md"
if [ -f "$ENV_VARS_DOC" ]; then
  pass "docs/env-vars.md exists"
  DOC_CONTENT=$(cat "$ENV_VARS_DOC")

  # Check all required env var names are documented
  for VAR in DB_URL DB_USERNAME DB_PASSWORD ACTIVEMQ_BROKER_URL SERVER_PORT; do
    if echo "$DOC_CONTENT" | grep -q "$VAR"; then
      pass "docs/env-vars.md documents $VAR"
    else
      fail "docs/env-vars.md is missing documentation for $VAR"
    fi
  done

  # Check default values are documented
  if echo "$DOC_CONTENT" | grep -qi 'hsqldb'; then
    pass "docs/env-vars.md documents HSQLDB default for DB_URL"
  else
    fail "docs/env-vars.md missing HSQLDB default value for DB_URL"
  fi

  if echo "$DOC_CONTENT" | grep -q '8080'; then
    pass "docs/env-vars.md documents default port 8080"
  else
    fail "docs/env-vars.md missing default port 8080"
  fi

  if echo "$DOC_CONTENT" | grep -q '61616'; then
    pass "docs/env-vars.md documents default ActiveMQ port 61616"
  else
    fail "docs/env-vars.md missing default ActiveMQ port 61616"
  fi

  if echo "$DOC_CONTENT" | grep -q '8081'; then
    pass "docs/env-vars.md documents default activemq server port 8081"
  else
    fail "docs/env-vars.md missing default activemq server port 8081"
  fi
else
  fail "docs/env-vars.md does NOT exist"
fi

echo ""

# -----------------------------------------------------------------------
# Criterion 4: Verify placeholder syntax in application.properties files
# -----------------------------------------------------------------------
echo "--- Criterion 4: Placeholder syntax in application.properties ---"
MONOLITH_PROPS="$REPO_ROOT/monolith/src/main/resources/application.properties"
ACTIVEMQ_PROPS="$REPO_ROOT/activemq/src/main/resources/application.properties"

check_placeholder() {
  local file="$1"
  local key="$2"
  local var="$3"
  local module
  module=$(basename "$(dirname "$(dirname "$(dirname "$file")")")")
  if grep -q "${key}=\${${var}:" "$file"; then
    pass "$key uses \${${var}:...} placeholder in ${module}/application.properties"
  else
    fail "$key does NOT use \${${var}:...} placeholder in ${module}/application.properties"
  fi
}

check_placeholder "$MONOLITH_PROPS" "spring.datasource.url" "DB_URL"
check_placeholder "$MONOLITH_PROPS" "spring.datasource.username" "DB_USERNAME"
check_placeholder "$MONOLITH_PROPS" "spring.datasource.password" "DB_PASSWORD"
check_placeholder "$MONOLITH_PROPS" "spring.activemq.broker-url" "ACTIVEMQ_BROKER_URL"
check_placeholder "$MONOLITH_PROPS" "server.port" "SERVER_PORT"
check_placeholder "$ACTIVEMQ_PROPS" "activemq.broker-url" "ACTIVEMQ_BROKER_URL"
check_placeholder "$ACTIVEMQ_PROPS" "server.port" "SERVER_PORT"

echo ""
echo "=== Summary ==="
echo "  Passed: $PASS"
echo "  Failed: $FAIL"
echo ""

if [ "$FAIL" -eq 0 ]; then
  echo "ALL ACCEPTANCE CRITERIA MET"
  exit 0
else
  echo "SOME CRITERIA FAILED"
  exit 1
fi
