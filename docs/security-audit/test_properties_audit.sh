#!/usr/bin/env bash
# TDD test script for properties-audit.md
# Run this script to verify the audit document meets all acceptance criteria.
# Exit code 0 = all tests pass; non-zero = failures found.

AUDIT_FILE="$(dirname "$0")/properties-audit.md"
PASS=0
FAIL=0

assert_contains() {
    local description="$1"
    local pattern="$2"
    if grep -qiE "$pattern" "$AUDIT_FILE" 2>/dev/null; then
        echo "  PASS: $description"
        PASS=$((PASS + 1))
    else
        echo "  FAIL: $description  [pattern: $pattern]"
        FAIL=$((FAIL + 1))
    fi
}

assert_file_exists() {
    local description="$1"
    local file="$2"
    if [ -f "$file" ]; then
        echo "  PASS: $description"
        PASS=$((PASS + 1))
    else
        echo "  FAIL: $description  [file not found: $file]"
        FAIL=$((FAIL + 1))
    fi
}

echo "=== Security Audit: properties-audit.md Tests ==="
echo ""

# ── Test 1: Audit file exists ──────────────────────────────────────────────
echo "[ File existence ]"
assert_file_exists "properties-audit.md exists" "$AUDIT_FILE"
echo ""

# ── Test 2: Both files documented ─────────────────────────────────────────
echo "[ Both properties files documented ]"
assert_contains "Monolith application.properties path documented" \
    "monolith/src/main/resources/application\.properties"
assert_contains "ActiveMQ application.properties path documented" \
    "activemq/src/main/resources/application\.properties"
echo ""

# ── Test 3: Monolith properties fully documented ──────────────────────────
echo "[ Monolith properties documented ]"
assert_contains "spring.datasource.schema documented" \
    "spring\.datasource\.schema"
assert_contains "spring.datasource.data documented" \
    "spring\.datasource\.data"
assert_contains "spring.thymeleaf.mode documented" \
    "spring\.thymeleaf\.mode"
assert_contains "spring.jpa.hibernate.ddl-auto documented" \
    "spring\.jpa\.hibernate\.ddl-auto"
assert_contains "spring.activemq.broker-url documented" \
    "spring\.activemq\.broker-url"
assert_contains "spring.messages.basename documented" \
    "spring\.messages\.basename"
assert_contains "management.endpoints.web.base-path (monolith) documented" \
    "management\.endpoints\.web\.base-path"
assert_contains "management.endpoints.web.exposure.include documented" \
    "management\.endpoints\.web\.exposure\.include"
assert_contains "server.port documented" \
    "server\.port"
echo ""

# ── Test 4: ActiveMQ properties fully documented ──────────────────────────
echo "[ ActiveMQ properties documented ]"
assert_contains "activemq.broker-url documented" \
    "activemq\.broker-url"
assert_contains "spring.jms.template.default-destination documented" \
    "spring\.jms\.template\.default-destination"
assert_contains "hawtio.authenticationEnabled documented" \
    "hawtio\.authenticationEnabled"
assert_contains "hawtio jolokia exposure documented" \
    "hawtio.*jolokia|jolokia.*hawtio"
echo ""

# ── Test 5: spring.jpa.hibernate.ddl-auto value confirmed ─────────────────
echo "[ spring.jpa.hibernate.ddl-auto value confirmed ]"
assert_contains "ddl-auto value is 'none'" \
    "ddl-auto.*none|none.*ddl-auto"
echo ""

# ── Test 6: spring.sql.init.mode confirmed ────────────────────────────────
echo "[ spring.sql.init.mode confirmed ]"
assert_contains "spring.sql.init.mode absence or value documented" \
    "sql\.init\.mode|init\.mode|not.*set|absent|not present|not configured|not defined"
echo ""

# ── Test 7: HikariCP pool configuration confirmed ─────────────────────────
echo "[ HikariCP pool configuration confirmed ]"
assert_contains "HikariCP documented" \
    "hikari|HikariCP|connection.pool|connection pool"
echo ""

# ── Test 8: Hardcoded credentials check ───────────────────────────────────
echo "[ Hardcoded credentials assessment ]"
assert_contains "Database password finding documented" \
    "password|credential|secret"
assert_contains "Broker password finding documented" \
    "broker.*password|password.*broker|activemq.*password|password.*activemq|no.*broker.*password|broker.*password.*not"
echo ""

# ── Test 9: Critical findings section ─────────────────────────────────────
echo "[ Critical findings section ]"
assert_contains "Critical findings section present" \
    "critical|finding|security"
assert_contains "hawtio authentication disabled flagged" \
    "hawtio.*auth.*false|auth.*false.*hawtio|authenticationEnabled.*false|false.*authenticationEnabled"
assert_contains "Actuator wildcard exposure flagged" \
    "exposure\.include.*\*|\*.*exposure|wildcard|all.*endpoint|endpoint.*all"
echo ""

# ── Test 10: Recommendations section ──────────────────────────────────────
echo "[ Recommendations section ]"
assert_contains "Recommendations or remediation section present" \
    "recommend|remediat|action|mitigat"
echo ""

# ── Summary ────────────────────────────────────────────────────────────────
echo "=== Results: $PASS passed, $FAIL failed ==="
if [ "$FAIL" -gt 0 ]; then
    exit 1
fi
exit 0
