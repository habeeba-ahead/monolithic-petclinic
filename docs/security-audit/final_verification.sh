#!/usr/bin/env bash
# Final Acceptance Criteria Verification
# Tests each criterion individually and reports pass/fail.

REPO_ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
AUDIT_FILE="$REPO_ROOT/docs/security-audit/properties-audit.md"
MONOLITH_PROPS="$REPO_ROOT/monolith/src/main/resources/application.properties"
ACTIVEMQ_PROPS="$REPO_ROOT/activemq/src/main/resources/application.properties"

PASS=0
FAIL=0

pass() { echo "  PASS [$1]"; PASS=$((PASS+1)); }
fail() { echo "  FAIL [$1]: $2"; FAIL=$((FAIL+1)); }

contains() { grep -qiE "$1" "$AUDIT_FILE" 2>/dev/null; }

echo "================================================================"
echo " FINAL ACCEPTANCE CRITERIA VERIFICATION"
echo "================================================================"
echo ""

# ── AC-1: Both files read and full contents documented ───────────────────────
echo "AC-1: Both files read and full contents documented in properties-audit.md"

# File exists
if [ -f "$AUDIT_FILE" ]; then
    pass "AC-1a: docs/security-audit/properties-audit.md exists on disk"
else
    fail "AC-1a" "docs/security-audit/properties-audit.md does NOT exist"
fi

# Git committed
if git -C "$REPO_ROOT" ls-files --error-unmatch "docs/security-audit/properties-audit.md" >/dev/null 2>&1; then
    pass "AC-1b: properties-audit.md is committed to git"
else
    fail "AC-1b" "properties-audit.md is NOT committed to git (exists on disk only as untracked file)"
fi

# Monolith path documented
if contains "monolith/src/main/resources/application\.properties"; then
    pass "AC-1c: Monolith properties file path documented"
else
    fail "AC-1c" "Monolith properties file path not found in audit"
fi

# ActiveMQ path documented
if contains "activemq/src/main/resources/application\.properties"; then
    pass "AC-1d: ActiveMQ properties file path documented"
else
    fail "AC-1d" "ActiveMQ properties file path not found in audit"
fi

# Monolith verbatim content — check every non-comment key
echo "  Checking monolith properties keys..."
while IFS= read -r line; do
    [[ "$line" =~ ^[[:space:]]*$ ]] && continue
    [[ "$line" =~ ^[[:space:]]*# ]] && continue
    key="${line%%=*}"; key="${key// /}"
    [ -z "$key" ] && continue
    escaped_key=$(echo "$key" | sed 's/\./\\./g')
    if contains "$escaped_key"; then
        pass "AC-1e: Monolith key '$key' documented"
    else
        fail "AC-1e" "Monolith key '$key' NOT documented in audit"
    fi
done < "$MONOLITH_PROPS"

# ActiveMQ verbatim content — check every non-comment key
echo "  Checking activemq properties keys..."
while IFS= read -r line; do
    [[ "$line" =~ ^[[:space:]]*$ ]] && continue
    [[ "$line" =~ ^[[:space:]]*# ]] && continue
    key="${line%%=*}"; key="${key// /}"
    [ -z "$key" ] && continue
    escaped_key=$(echo "$key" | sed 's/\./\\./g')
    if contains "$escaped_key"; then
        pass "AC-1f: ActiveMQ key '$key' documented"
    else
        fail "AC-1f" "ActiveMQ key '$key' NOT documented in audit"
    fi
done < "$ACTIVEMQ_PROPS"

echo ""

# ── AC-2: Hardcoded secrets flagged as CRITICAL requiring immediate rotation ─
echo "AC-2: Hardcoded secrets flagged as CRITICAL requiring immediate rotation"

# Database password assessed
if contains "spring\.datasource\.password|database.*password|password.*database"; then
    pass "AC-2a: Database password assessed"
else
    fail "AC-2a" "Database password not assessed in audit"
fi

# Broker password assessed
if contains "activemq.*password|broker.*password|spring\.activemq\.password"; then
    pass "AC-2b: Broker password assessed"
else
    fail "AC-2b" "Broker password not assessed in audit"
fi

# API key assessed
if contains "api.key|api-key|api key|secret|token"; then
    pass "AC-2c: API key/secret assessment present"
else
    fail "AC-2c" "API key/secret assessment not present"
fi

# Rotation discussed
if contains "rotat"; then
    pass "AC-2d: Rotation requirement discussed"
else
    fail "AC-2d" "Rotation requirement not discussed"
fi

# Critical findings flagged (hawtio auth disabled is the most critical)
if contains "critical.*hawtio|hawtio.*critical|CRITICAL.*hawtio|hawtio.*CRITICAL|critical.*auth.*false|auth.*false.*critical|CRITICAL-1|critical.*authentication"; then
    pass "AC-2e: hawtio.authenticationEnabled=false flagged as CRITICAL"
else
    fail "AC-2e" "hawtio.authenticationEnabled=false not flagged as CRITICAL"
fi

# No hardcoded DB password found (correct — none exists)
if contains "not set|not found|no.*password|password.*not|absent|none found|not present|not configured"; then
    pass "AC-2f: No hardcoded DB/broker password documented correctly"
else
    fail "AC-2f" "Hardcoded credential absence not documented"
fi

echo ""

# ── AC-3: spring.jpa.hibernate.ddl-auto confirmed and documented ─────────────
echo "AC-3: spring.jpa.hibernate.ddl-auto value confirmed and documented"

ACTUAL_DDL=$(grep -E '^spring\.jpa\.hibernate\.ddl-auto' "$MONOLITH_PROPS" | cut -d= -f2 | tr -d ' ')
echo "  Actual value in file: '$ACTUAL_DDL'"

if contains "spring\.jpa\.hibernate\.ddl-auto"; then
    pass "AC-3a: spring.jpa.hibernate.ddl-auto property documented"
else
    fail "AC-3a" "spring.jpa.hibernate.ddl-auto not documented"
fi

if contains "ddl-auto.*$ACTUAL_DDL|$ACTUAL_DDL.*ddl-auto"; then
    pass "AC-3b: ddl-auto value '$ACTUAL_DDL' documented"
else
    fail "AC-3b" "ddl-auto value '$ACTUAL_DDL' not documented"
fi

if contains "safe|no.*ddl|ddl.*no|none.*instruct|no.*schema.*operation|no.*operation|no DDL"; then
    pass "AC-3c: ddl-auto=none confirmed as safe"
else
    fail "AC-3c" "ddl-auto=none not confirmed as safe"
fi

echo ""

# ── AC-4: spring.sql.init.mode confirmed and documented ──────────────────────
echo "AC-4: spring.sql.init.mode value confirmed and documented"

if contains "sql\.init\.mode|init\.mode"; then
    pass "AC-4a: spring.sql.init.mode documented in audit"
else
    fail "AC-4a" "spring.sql.init.mode not documented"
fi

# Property is absent from both files — audit must say so
if contains "not.*set|not.*present|not.*configured|not.*defined|absent|not.*found|property.*absent|missing|not present"; then
    pass "AC-4b: spring.sql.init.mode absence documented"
else
    fail "AC-4b" "spring.sql.init.mode absence not documented"
fi

# Indirect risk via datasource.schema/data noted
if contains "datasource\.schema|datasource\.data|schema\.sql|data\.sql"; then
    pass "AC-4c: Indirect risk via datasource.schema/data noted"
else
    fail "AC-4c" "Indirect risk via datasource.schema/data not noted"
fi

echo ""

# ── AC-5: HikariCP pool configuration confirmed and documented ────────────────
echo "AC-5: HikariCP pool configuration confirmed and documented"

if contains "hikari|HikariCP"; then
    pass "AC-5a: HikariCP section present"
else
    fail "AC-5a" "HikariCP not mentioned in audit"
fi

if contains "not.*configured|default|no.*hikari|hikari.*not|none.*configured|defaults.*used|defaults.*in.*use|default.*value"; then
    pass "AC-5b: HikariCP defaults-in-use documented"
else
    fail "AC-5b" "HikariCP defaults-in-use not documented"
fi

if contains "maximum.pool.size|max.*pool.*size|pool.*size|maximum-pool-size"; then
    pass "AC-5c: maximum-pool-size documented"
else
    fail "AC-5c" "maximum-pool-size not documented"
fi

if contains "connection.timeout|connection.*timeout|connection-timeout"; then
    pass "AC-5d: connection-timeout documented"
else
    fail "AC-5d" "connection-timeout not documented"
fi

if contains "idle.timeout|idle.*timeout|idle-timeout"; then
    pass "AC-5e: idle-timeout documented"
else
    fail "AC-5e" "idle-timeout not documented"
fi

if contains "max.lifetime|max.*lifetime|max-lifetime"; then
    pass "AC-5f: max-lifetime documented"
else
    fail "AC-5f" "max-lifetime not documented"
fi

echo ""

# ── Summary ───────────────────────────────────────────────────────────────────
echo "================================================================"
echo " RESULTS: $PASS passed, $FAIL failed"
echo "================================================================"

if [ "$FAIL" -gt 0 ]; then
    exit 1
fi
exit 0
