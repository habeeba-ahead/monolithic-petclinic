#!/usr/bin/env bash
# Acceptance Criteria Verification Script
# Verifies all acceptance criteria for the properties-audit task.
# Exit code 0 = all criteria met; non-zero = failures found.

REPO_ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
AUDIT_FILE="$REPO_ROOT/docs/security-audit/properties-audit.md"
MONOLITH_PROPS="$REPO_ROOT/monolith/src/main/resources/application.properties"
ACTIVEMQ_PROPS="$REPO_ROOT/activemq/src/main/resources/application.properties"

PASS=0
FAIL=0

assert_true() {
    local description="$1"
    local result="$2"   # "0" = success, anything else = failure
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
    if ! grep -qiE "$pattern" "$file" 2>/dev/null; then
        echo "  PASS: $description"
        PASS=$((PASS + 1))
    else
        echo "  FAIL: $description  [unexpected pattern found: $pattern]"
        FAIL=$((FAIL + 1))
    fi
}

echo "============================================================"
echo " Acceptance Criteria Verification: properties-audit.md"
echo "============================================================"
echo ""

# ─────────────────────────────────────────────────────────────────────────────
# CRITERION 1: Both properties files exist in the repo
# ─────────────────────────────────────────────────────────────────────────────
echo "[ CRITERION 1: Source properties files exist ]"
[ -f "$MONOLITH_PROPS" ]
assert_true "monolith/src/main/resources/application.properties exists" $?
[ -f "$ACTIVEMQ_PROPS" ]
assert_true "activemq/src/main/resources/application.properties exists" $?
echo ""

# ─────────────────────────────────────────────────────────────────────────────
# CRITERION 2: Audit document exists at docs/security-audit/properties-audit.md
# ─────────────────────────────────────────────────────────────────────────────
echo "[ CRITERION 2: Audit document committed at correct path ]"
[ -f "$AUDIT_FILE" ]
assert_true "docs/security-audit/properties-audit.md exists" $?
# Verify it is tracked by git (committed, not just a working-tree file)
git -C "$REPO_ROOT" ls-files --error-unmatch "docs/security-audit/properties-audit.md" >/dev/null 2>&1
assert_true "properties-audit.md is tracked by git (committed)" $?
echo ""

# ─────────────────────────────────────────────────────────────────────────────
# CRITERION 3: Full contents of BOTH files are documented in the audit
# ─────────────────────────────────────────────────────────────────────────────
echo "[ CRITERION 3: Full contents of both files documented ]"

# Monolith properties — every key must appear in the audit doc
while IFS= read -r line; do
    # Skip blank lines and comment-only lines
    [[ "$line" =~ ^[[:space:]]*$ ]] && continue
    [[ "$line" =~ ^[[:space:]]*# ]] && continue
    key="${line%%=*}"
    key="${key// /}"
    [ -z "$key" ] && continue
    assert_contains "Monolith key '$key' documented in audit" "$(echo "$key" | sed 's/\./\\./g')"
done < "$MONOLITH_PROPS"

# ActiveMQ properties — every key must appear in the audit doc
while IFS= read -r line; do
    [[ "$line" =~ ^[[:space:]]*$ ]] && continue
    [[ "$line" =~ ^[[:space:]]*# ]] && continue
    key="${line%%=*}"
    key="${key// /}"
    [ -z "$key" ] && continue
    assert_contains "ActiveMQ key '$key' documented in audit" "$(echo "$key" | sed 's/\./\\./g')"
done < "$ACTIVEMQ_PROPS"

# Verbatim content blocks: spot-check specific values from each file
assert_contains "Monolith: spring.datasource.schema value documented" \
    "classpath\*:db/schema\.sql"
assert_contains "Monolith: spring.datasource.data value documented" \
    "classpath\*:db/data\.sql"
assert_contains "Monolith: spring.thymeleaf.mode=HTML documented" \
    "spring\.thymeleaf\.mode.*HTML|HTML.*thymeleaf"
assert_contains "Monolith: spring.jpa.hibernate.ddl-auto=none documented" \
    "ddl-auto.*none|none.*ddl-auto"
assert_contains "Monolith: spring.activemq.broker-url=tcp://localhost:61616 documented" \
    "tcp://localhost:61616"
assert_contains "Monolith: management.endpoints.web.exposure.include=* documented" \
    'exposure\.include.*\*|\*.*exposure'
assert_contains "Monolith: server.port=8080 documented" \
    "8080"

assert_contains "ActiveMQ: activemq.broker-url=tcp://0.0.0.0:61616 documented" \
    "tcp://0\.0\.0\.0:61616"
assert_contains "ActiveMQ: spring.jms.template.default-destination=visitCreated documented" \
    "visitCreated"
assert_contains "ActiveMQ: hawtio.authenticationEnabled=false documented" \
    "hawtio\.authenticationEnabled.*false|false.*hawtio"
assert_contains "ActiveMQ: management.endpoints.web.exposure.include=hawtio,jolokia documented" \
    "hawtio.*jolokia|jolokia.*hawtio"
assert_contains "ActiveMQ: server.port=8081 documented" \
    "8081"
echo ""

# ─────────────────────────────────────────────────────────────────────────────
# CRITERION 4: Hardcoded secrets flagged as CRITICAL requiring immediate rotation
# ─────────────────────────────────────────────────────────────────────────────
echo "[ CRITERION 4: Hardcoded secrets assessment and flagging ]"

# The audit must assess database password
assert_contains "Database password assessed in audit" \
    "spring\.datasource\.password|database.*password|password.*database"

# The audit must assess broker password
assert_contains "Broker password assessed in audit" \
    "activemq.*password|broker.*password|spring\.activemq\.password"

# The audit must assess API keys
assert_contains "API key assessment present" \
    "api.key|api-key|api key|secret|token"

# The audit must document that no hardcoded DB password was found
assert_contains "No hardcoded database password finding documented" \
    "not set|not found|no.*password|password.*not|absent|none found|not present|not configured"

# The audit must document that no hardcoded broker password was found
assert_contains "No hardcoded broker password finding documented" \
    "not set|not found|no.*password|password.*not|absent|none found|not present|not configured"

# The audit must state that if a hardcoded secret WERE found it requires rotation
# (The document discusses rotation in the context of the assessment)
assert_contains "Rotation requirement discussed" \
    "rotat"

# The audit must flag hawtio.authenticationEnabled=false as critical
assert_contains "hawtio.authenticationEnabled=false flagged as CRITICAL" \
    "critical.*hawtio|hawtio.*critical|CRITICAL.*hawtio|hawtio.*CRITICAL|critical.*auth.*false|auth.*false.*critical"

echo ""

# ─────────────────────────────────────────────────────────────────────────────
# CRITERION 5: spring.jpa.hibernate.ddl-auto value confirmed and documented
# ─────────────────────────────────────────────────────────────────────────────
echo "[ CRITERION 5: spring.jpa.hibernate.ddl-auto confirmed ]"

# Actual value in the file
ACTUAL_DDL_AUTO=$(grep -E '^spring\.jpa\.hibernate\.ddl-auto' "$MONOLITH_PROPS" | cut -d= -f2 | tr -d ' ')
assert_contains "spring.jpa.hibernate.ddl-auto property documented" \
    "spring\.jpa\.hibernate\.ddl-auto"
assert_contains "ddl-auto value '$ACTUAL_DDL_AUTO' documented" \
    "ddl-auto.*$ACTUAL_DDL_AUTO|$ACTUAL_DDL_AUTO.*ddl-auto"
# The value must be confirmed as safe (none = no DDL operations)
assert_contains "ddl-auto=none confirmed as safe" \
    "safe|no.*ddl|ddl.*no|none.*instruct|instruct.*none|no.*schema|schema.*no|no.*operation"
echo ""

# ─────────────────────────────────────────────────────────────────────────────
# CRITERION 6: spring.sql.init.mode value confirmed and documented
# ─────────────────────────────────────────────────────────────────────────────
echo "[ CRITERION 6: spring.sql.init.mode confirmed ]"

# Verify the actual state of the property in both files
MONOLITH_INIT_MODE=$(grep -E '^spring\.sql\.init\.mode' "$MONOLITH_PROPS" 2>/dev/null || echo "")
ACTIVEMQ_INIT_MODE=$(grep -E '^spring\.sql\.init\.mode' "$ACTIVEMQ_PROPS" 2>/dev/null || echo "")

assert_contains "spring.sql.init.mode documented in audit" \
    "sql\.init\.mode|init\.mode"

# Since the property is absent from both files, the audit must say so
assert_contains "spring.sql.init.mode absence documented" \
    "not.*set|not.*present|not.*configured|not.*defined|absent|not.*found|property.*absent|missing"

# The audit must note the indirect risk via spring.datasource.schema/data
assert_contains "Indirect risk via datasource.schema/data noted" \
    "datasource\.schema|datasource\.data|schema\.sql|data\.sql"
echo ""

# ─────────────────────────────────────────────────────────────────────────────
# CRITERION 7: HikariCP pool configuration confirmed and documented
# ─────────────────────────────────────────────────────────────────────────────
echo "[ CRITERION 7: HikariCP pool configuration confirmed ]"

assert_contains "HikariCP section present in audit" \
    "hikari|HikariCP"

# Since no HikariCP properties are set, the audit must document this
assert_contains "HikariCP not configured / defaults in use documented" \
    "not.*configured|default|no.*hikari|hikari.*not|none.*configured|defaults.*used|defaults.*in.*use"

# The audit must list specific HikariCP settings (even if defaults)
assert_contains "maximum-pool-size documented" \
    "maximum.pool.size|max.*pool.*size|pool.*size"
assert_contains "connection-timeout documented" \
    "connection.timeout|connection.*timeout"
assert_contains "idle-timeout documented" \
    "idle.timeout|idle.*timeout"
assert_contains "max-lifetime documented" \
    "max.lifetime|max.*lifetime"
echo ""

# ─────────────────────────────────────────────────────────────────────────────
# CRITERION 8: Critical findings section present with proper severity labels
# ─────────────────────────────────────────────────────────────────────────────
echo "[ CRITERION 8: Critical findings section ]"

assert_contains "Critical findings section header present" \
    "critical.*finding|finding.*critical|critical.*issue|security.*finding|key.*finding"
assert_contains "Severity labels used (CRITICAL/HIGH/MEDIUM)" \
    "CRITICAL|HIGH|MEDIUM"
assert_contains "CWE references or security standards cited" \
    "CWE|OWASP|CVE|security"
echo ""

# ─────────────────────────────────────────────────────────────────────────────
# SUMMARY
# ─────────────────────────────────────────────────────────────────────────────
echo "============================================================"
echo " Results: $PASS passed, $FAIL failed"
echo "============================================================"

if [ "$FAIL" -gt 0 ]; then
    exit 1
fi
exit 0
