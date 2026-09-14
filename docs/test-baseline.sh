#!/bin/bash
# TDD test for upgrade-baseline.md
# This test validates the acceptance criteria for the baseline document

set -e

BASELINE="$(dirname "$0")/upgrade-baseline.md"
PASS=0
FAIL=0

check() {
    local desc="$1"
    local result="$2"
    if [ "$result" = "0" ]; then
        echo "PASS: $desc"
        PASS=$((PASS + 1))
    else
        echo "FAIL: $desc"
        FAIL=$((FAIL + 1))
    fi
}

# Test 1: File exists
[ -f "$BASELINE" ]
check "upgrade-baseline.md exists" $?

# Test 2: Contains Spring Boot version 2.2.1.RELEASE
grep -q "2.2.1.RELEASE" "$BASELINE"
check "Contains Spring Boot version 2.2.1.RELEASE" $?

# Test 3: Contains Java version 1.8
grep -q "1.8" "$BASELINE"
check "Contains Java version 1.8" $?

# Test 4: Contains CVSS >= 7.0 CVE count
grep -qiE "cvss.*7\.0|cve.*count|high.*cve|cve.*high" "$BASELINE"
check "Contains CVSS >= 7.0 CVE count reference" $?

# Test 5: Contains OWASP report reference
grep -qi "owasp\|dependency.check" "$BASELINE"
check "Contains OWASP/dependency-check reference" $?

# Test 6: Contains dependency tree reference
grep -qi "dependency.*tree\|transitive" "$BASELINE"
check "Contains dependency tree reference" $?

echo ""
echo "Results: $PASS passed, $FAIL failed"
[ "$FAIL" -eq 0 ] && exit 0 || exit 1
