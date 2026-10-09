#!/usr/bin/env bash
# TDD test: verifies that properties-audit.md exists and contains all required findings.
# Run BEFORE and AFTER writing the audit file to demonstrate red→green.

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
    if [ -f "$AUDIT_FILE" ]; then
        echo "  PASS: audit file exists at $AUDIT_FILE"
        PASS=$((PASS + 1))
    else
        echo "  FAIL: audit file does NOT exist at $AUDIT_FILE"
        FAIL=$((FAIL + 1))
    fi
}

echo "=== Security Audit: properties-audit.md test suite ==="
echo ""

# T1 – file existence
echo "--- T1: File existence ---"
assert_file_exists

# T2 – monolith properties fully documented
echo "--- T2: Monolith application.properties contents documented ---"
assert_contains "monolith datasource schema property present" "spring\.datasource\.schema"
assert_contains "monolith datasource data property present"   "spring\.datasource\.data"
assert_contains "monolith thymeleaf mode present"             "spring\.thymeleaf\.mode"
assert_contains "monolith ddl-auto value documented"          "spring\.jpa\.hibernate\.ddl-auto"
assert_contains "monolith activemq broker-url documented"     "spring\.activemq\.broker-url"
assert_contains "monolith messages basename documented"       "spring\.messages\.basename"
assert_contains "monolith actuator base-path documented"      "management\.endpoints\.web\.base-path"
assert_contains "monolith actuator exposure documented"       "management\.endpoints\.web\.exposure\.include"
assert_contains "monolith server port documented"             "server\.port"

# T3 – activemq properties fully documented
echo "--- T3: ActiveMQ application.properties contents documented ---"
assert_contains "activemq broker-url documented"              "activemq\.broker-url"
assert_contains "activemq jms destination documented"         "spring\.jms\.template\.default-destination"
assert_contains "activemq actuator base-path documented"      "management\.endpoints\.web\.base-path"
assert_contains "activemq actuator exposure documented"       "management\.endpoints\.web\.exposure\.include"
assert_contains "activemq hawtio auth documented"             "hawtio\.authenticationEnabled"
assert_contains "activemq server port documented"             "server\.port.*8081|8081.*server\.port"

# T4 – ddl-auto value confirmed
echo "--- T4: spring.jpa.hibernate.ddl-auto value confirmed ---"
assert_contains "ddl-auto value is 'none'"                    "ddl-auto.*none|none.*ddl-auto"

# T5 – spring.sql.init.mode confirmed (absent = documented)
echo "--- T5: spring.sql.init.mode status documented ---"
assert_contains "sql.init.mode absence documented"            "sql\.init\.mode|init\.mode"

# T6 – HikariCP pool configuration documented
echo "--- T6: HikariCP pool configuration documented ---"
assert_contains "HikariCP section present"                    "hikari|HikariCP|connection.?pool"

# T7 – hardcoded credentials check documented
echo "--- T7: Hardcoded credentials assessment documented ---"
assert_contains "password assessment present"                 "password|credential|secret"
assert_contains "no hardcoded DB password finding"            "no hardcoded|not.*hardcoded|hardcoded.*not|absent|not present|none found|not.*found"

# T8 – critical findings / security flags
echo "--- T8: Security findings flagged ---"
assert_contains "hawtio auth disabled flagged as finding"     "hawtio.*auth|auth.*hawtio|authentication.*disabled|disabled.*authentication"
assert_contains "actuator wildcard exposure flagged"          "actuator.*\*|wildcard|\*.*actuator|exposure.*\*|\*.*exposure"
assert_contains "schema.sql DROP statements flagged"          "DROP|drop.*table|schema.*drop"

# T9 – recommendations present
echo "--- T9: Recommendations section present ---"
assert_contains "recommendations section"                     "recommend|remediat|action"

echo ""
echo "=== Results: $PASS passed, $FAIL failed ==="
if [ "$FAIL" -gt 0 ]; then
    exit 1
fi
exit 0
