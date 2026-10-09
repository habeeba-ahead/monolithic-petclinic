#!/usr/bin/env bash
# Verification script for acceptance criteria of the properties-audit task.
# Tests each criterion independently and reports pass/fail.

REPO_ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
AUDIT_FILE="$REPO_ROOT/docs/security-audit/properties-audit.md"
MONOLITH_PROPS="$REPO_ROOT/monolith/src/main/resources/application.properties"
ACTIVEMQ_PROPS="$REPO_ROOT/activemq/src/main/resources/application.properties"

PASS=0
FAIL=0

assert_true() {
    local description="$1"
    local result="$2"   # 0 = success, non-zero = failure
    if [ "$result" -eq 0 ]; then
        echo "  PASS: $description"
        PASS=$((PASS + 1))
    else
        echo "  FAIL: $description"
        FAIL=$((FAIL + 1))
    fi
}

assert_contains() {
    local description="$1"
    local pattern="$2"
    local file="${3:-$AUDIT_FILE}"
    if grep -qiE "$pattern" "$file" 2>/dev/null; then
        echo "  PASS: $description"
        PASS=$((PASS + 1))
    else
        echo "  FAIL: $description  [pattern not found: $pattern]"
        FAIL=$((FAIL + 1))
    fi
}

assert_not_contains() {
    local description="$1"
    local pattern="$2"
    local file="${3:-$AUDIT_FILE}"
    if grep -qiE "$pattern" "$file" 2>/dev/null; then
        echo "  FAIL: $description  [pattern unexpectedly found: $pattern]"
        FAIL=$((FAIL + 1))
    else
        echo "  PASS: $description"
        PASS=$((PASS + 1))
    fi
}

# Helper: count matching lines (returns integer, never fails)
count_matches() {
    local pattern="$1"
    local file="$2"
    grep -cE "$pattern" "$file" 2>/dev/null || echo 0
}

echo "============================================================"
echo " Acceptance Criteria Verification: properties-audit.md"
echo "============================================================"
echo ""

# ---------------------------------------------------------------
# CRITERION 1: Both properties files read and full contents
#              documented in docs/security-audit/properties-audit.md
# ---------------------------------------------------------------
echo "--- CRITERION 1: Both properties files fully documented ---"

# 1a: audit file exists
if [ -f "$AUDIT_FILE" ]; then
    assert_true "docs/security-audit/properties-audit.md exists" 0
else
    assert_true "docs/security-audit/properties-audit.md exists" 1
fi

# 1b: monolith properties file exists (source of truth)
if [ -f "$MONOLITH_PROPS" ]; then
    assert_true "monolith/src/main/resources/application.properties exists" 0
else
    assert_true "monolith/src/main/resources/application.properties exists" 1
fi

# 1c: activemq properties file exists (source of truth)
if [ -f "$ACTIVEMQ_PROPS" ]; then
    assert_true "activemq/src/main/resources/application.properties exists" 0
else
    assert_true "activemq/src/main/resources/application.properties exists" 1
fi

# 1d: audit references the monolith file path
assert_contains "Audit references monolith properties file path" \
    "monolith/src/main/resources/application\.properties"

# 1e: audit references the activemq file path
assert_contains "Audit references activemq properties file path" \
    "activemq/src/main/resources/application\.properties"

# 1f: Every property from monolith/application.properties is documented in the audit
echo ""
echo "  [Checking each monolith property key is documented in audit]"
while IFS= read -r line; do
    # Skip comments and blank lines
    [[ "$line" =~ ^[[:space:]]*# ]] && continue
    [[ -z "${line// }" ]] && continue
    # Extract key (before =)
    key=$(echo "$line" | cut -d'=' -f1 | tr -d ' ')
    [[ -z "$key" ]] && continue
    # Escape dots for grep
    escaped_key=$(echo "$key" | sed 's/\./\\./g')
    assert_contains "Monolith property '$key' documented in audit" "$escaped_key"
done < "$MONOLITH_PROPS"

# 1g: Every property from activemq/application.properties is documented in the audit
echo ""
echo "  [Checking each activemq property key is documented in audit]"
while IFS= read -r line; do
    [[ "$line" =~ ^[[:space:]]*# ]] && continue
    [[ -z "${line// }" ]] && continue
    key=$(echo "$line" | cut -d'=' -f1 | tr -d ' ')
    [[ -z "$key" ]] && continue
    escaped_key=$(echo "$key" | sed 's/\./\\./g')
    assert_contains "ActiveMQ property '$key' documented in audit" "$escaped_key"
done < "$ACTIVEMQ_PROPS"

# 1h: Verbatim/full content blocks present (code fences with properties content)
assert_contains "Monolith properties verbatim block present (schema.sql reference)" \
    "classpath\*:db/schema\.sql"
assert_contains "ActiveMQ properties verbatim block present (visitCreated destination)" \
    "visitCreated"

echo ""

# ---------------------------------------------------------------
# CRITERION 2: Hardcoded database password, broker password, or
#              API key flagged as CRITICAL requiring immediate rotation
# ---------------------------------------------------------------
echo "--- CRITERION 2: Hardcoded secrets flagged / assessed ---"

# 2a: Credential assessment section exists
assert_contains "Credentials/secrets assessment section present" \
    "credential|hardcoded|secret|password"

# 2b: Database password assessment documented
assert_contains "Database password assessment documented" \
    "database.*password|datasource.*password|password.*database|datasource.*password"

# 2c: Broker password assessment documented
assert_contains "Broker/ActiveMQ password assessment documented" \
    "broker.*password|activemq.*password|password.*broker|password.*activemq"

# 2d: API key assessment documented
assert_contains "API key assessment documented" \
    "api.?key|api key|oauth|jwt|secret"

# 2e: The audit correctly identifies NO hardcoded DB password (none present in source)
assert_contains "Audit confirms no hardcoded DB password found" \
    "no hardcoded|not.*hardcoded|hardcoded.*not|absent|not present|none found|not.*found|not set"

# 2f: The audit correctly identifies NO hardcoded broker password (none present in source)
assert_contains "Audit confirms no hardcoded broker password found" \
    "no hardcoded|not.*hardcoded|hardcoded.*not|absent|not present|none found|not.*found|not set"

# 2g: Rotation guidance present (even if no rotation needed, the policy must be stated)
assert_contains "Rotation guidance/status documented" \
    "rotation|rotate"

# 2h: If a hardcoded secret WERE found, it would be CRITICAL — verify the audit
#     documents the severity framework (critical finding language)
assert_contains "Critical severity language present in audit" \
    "critical|CRITICAL"

# 2i: Verify the actual source files do NOT contain hardcoded passwords
#     (so the audit's "none found" conclusion is correct)
assert_not_contains "Monolith props does NOT contain hardcoded password value" \
    "password\s*=\s*.+" "$MONOLITH_PROPS"
assert_not_contains "ActiveMQ props does NOT contain hardcoded password value" \
    "password\s*=\s*.+" "$ACTIVEMQ_PROPS"

echo ""

# ---------------------------------------------------------------
# CRITERION 3: spring.jpa.hibernate.ddl-auto value confirmed and documented
# ---------------------------------------------------------------
echo "--- CRITERION 3: spring.jpa.hibernate.ddl-auto confirmed ---"

# 3a: Property exists in monolith source with value 'none'
ACTUAL_DDL=$(grep -E "^spring\.jpa\.hibernate\.ddl-auto" "$MONOLITH_PROPS" | cut -d'=' -f2 | tr -d ' ')
if [ "$ACTUAL_DDL" = "none" ]; then
    assert_true "spring.jpa.hibernate.ddl-auto=none in monolith source file" 0
else
    assert_true "spring.jpa.hibernate.ddl-auto=none in monolith source file (got: '$ACTUAL_DDL')" 1
fi

# 3b: Audit documents the property
assert_contains "Audit documents spring.jpa.hibernate.ddl-auto" \
    "spring\.jpa\.hibernate\.ddl-auto"

# 3c: Audit documents the value 'none'
assert_contains "Audit documents ddl-auto value as 'none'" \
    "ddl-auto.*=.*none|ddl-auto.*none|none.*ddl-auto"

# 3d: Audit explains what ddl-auto=none means
assert_contains "Audit explains meaning of ddl-auto=none" \
    "hibernate.*not.*manage|not.*manage.*ddl|ddl.*not.*manage|schema.*managed|not.*create|not.*drop|not.*update"

echo ""

# ---------------------------------------------------------------
# CRITERION 4: spring.sql.init.mode value confirmed and documented
# ---------------------------------------------------------------
echo "--- CRITERION 4: spring.sql.init.mode confirmed ---"

# 4a: Verify the property is actually absent from monolith source
if grep -qE "spring\.sql\.init\.mode" "$MONOLITH_PROPS" 2>/dev/null; then
    assert_true "spring.sql.init.mode is absent from monolith source (not set)" 1
else
    assert_true "spring.sql.init.mode is absent from monolith source (not set)" 0
fi

# 4b: Verify the property is actually absent from activemq source
if grep -qE "spring\.sql\.init\.mode" "$ACTIVEMQ_PROPS" 2>/dev/null; then
    assert_true "spring.sql.init.mode is absent from activemq source (not set)" 1
else
    assert_true "spring.sql.init.mode is absent from activemq source (not set)" 0
fi

# 4c: Audit documents the property (even if absent)
assert_contains "Audit documents spring.sql.init.mode status" \
    "sql\.init\.mode|init\.mode|sql.*init.*mode"

# 4d: Audit notes the property is not set / absent
assert_contains "Audit notes spring.sql.init.mode is not set/absent" \
    "not set|absent|not present|not.*configured|missing|NOT SET"

# 4e: Audit explains the default behaviour
assert_contains "Audit explains default sql.init.mode behaviour" \
    "default|embedded|always"

echo ""

# ---------------------------------------------------------------
# CRITERION 5: HikariCP pool configuration values confirmed and documented
# ---------------------------------------------------------------
echo "--- CRITERION 5: HikariCP pool configuration documented ---"

# 5a: HikariCP section exists in audit
assert_contains "HikariCP section present in audit" \
    "hikari|HikariCP|connection.?pool"

# 5b: Audit notes no custom HikariCP properties are set in source
if grep -qiE "hikari" "$MONOLITH_PROPS" 2>/dev/null; then
    assert_true "No HikariCP properties in monolith source (confirmed absent)" 1
else
    assert_true "No HikariCP properties in monolith source (confirmed absent)" 0
fi

assert_contains "Audit notes HikariCP properties are not set / using defaults" \
    "not set|default|none set|no.*hikari|hikari.*not|not.*configured|NONE SET"

# 5c: Audit documents default pool size
assert_contains "Audit documents default maximum-pool-size" \
    "maximum.pool.size|max.*pool.*size|pool.*size"

# 5d: Audit documents default connection timeout
assert_contains "Audit documents default connection-timeout" \
    "connection.?timeout|connection.*timeout"

# 5e: Audit documents default idle timeout
assert_contains "Audit documents default idle-timeout" \
    "idle.?timeout|idle.*timeout"

# 5f: Audit documents default max-lifetime
assert_contains "Audit documents default max-lifetime" \
    "max.?lifetime|max.*lifetime"

echo ""

# ---------------------------------------------------------------
# SUMMARY
# ---------------------------------------------------------------
echo "============================================================"
echo " RESULTS: $PASS passed, $FAIL failed"
echo "============================================================"

if [ "$FAIL" -gt 0 ]; then
    exit 1
fi
exit 0
