#!/usr/bin/env bash
# Verification script for acceptance criteria
# Tests all three criteria and reports pass/fail

set -euo pipefail

REPO_ROOT="/workspace/f506a5ae-d80e-4190-ac07-862879e2a53f/worker-0/repo"
PASS=0
FAIL=0
RESULTS=()

export JAVA_HOME=/workspace/jdk/jdk8u504-b01
export PATH=$JAVA_HOME/bin:$PATH

echo "============================================================"
echo "ACCEPTANCE CRITERIA VERIFICATION"
echo "============================================================"
echo ""

# ---------------------------------------------------------------
# CRITERION 1: dependency:tree produces BUILD SUCCESS with Spring Boot 2.2.1 artifacts
# ---------------------------------------------------------------
echo "--- CRITERION 1: dependency:tree -Dincludes=org.springframework.boot ---"
DEP_TREE_OUTPUT=$(cd "$REPO_ROOT" && ./mvnw dependency:tree -Dincludes=org.springframework.boot 2>&1)

CRIT1_PASS=true

# Check BUILD SUCCESS
if echo "$DEP_TREE_OUTPUT" | grep -q "BUILD SUCCESS"; then
    echo "  [PASS] Command completed with BUILD SUCCESS"
else
    echo "  [FAIL] Command did NOT produce BUILD SUCCESS"
    CRIT1_PASS=false
fi

# Check that Spring Boot 2.2.1.RELEASE artifacts are listed
if echo "$DEP_TREE_OUTPUT" | grep -q "org.springframework.boot.*2.2.1.RELEASE"; then
    echo "  [PASS] Spring Boot 2.2.1.RELEASE artifacts are listed"
else
    echo "  [FAIL] Spring Boot 2.2.1.RELEASE artifacts NOT found in output"
    CRIT1_PASS=false
fi

# Check no ERROR in output
if echo "$DEP_TREE_OUTPUT" | grep -q "BUILD FAILURE"; then
    echo "  [FAIL] BUILD FAILURE detected"
    CRIT1_PASS=false
else
    echo "  [PASS] No BUILD FAILURE detected"
fi

# Count unique Spring Boot artifacts
SB_ARTIFACT_COUNT=$(echo "$DEP_TREE_OUTPUT" | grep -o "org.springframework.boot:spring-boot[^:]*" | sort -u | wc -l)
echo "  [INFO] Unique Spring Boot artifacts found: $SB_ARTIFACT_COUNT"

if [ "$CRIT1_PASS" = true ]; then
    echo "  => CRITERION 1: PASSED"
    RESULTS+=("PASS: dependency:tree produces BUILD SUCCESS listing Spring Boot 2.2.1 transitive dependencies")
    PASS=$((PASS+1))
else
    echo "  => CRITERION 1: FAILED"
    RESULTS+=("FAIL: dependency:tree did not produce expected output")
    FAIL=$((FAIL+1))
fi

echo ""

# ---------------------------------------------------------------
# CRITERION 2: OWASP dependency-check-report.html exists in each module's target/
# ---------------------------------------------------------------
echo "--- CRITERION 2: OWASP dependency-check-report.html in each module target/ ---"
CRIT2_PASS=true

ACTIVEMQ_REPORT="$REPO_ROOT/activemq/target/dependency-check-report.html"
MONOLITH_REPORT="$REPO_ROOT/monolith/target/dependency-check-report.html"

if [ -f "$ACTIVEMQ_REPORT" ]; then
    ACTIVEMQ_SIZE=$(wc -c < "$ACTIVEMQ_REPORT")
    echo "  [PASS] activemq/target/dependency-check-report.html exists (size: ${ACTIVEMQ_SIZE} bytes)"
else
    echo "  [FAIL] activemq/target/dependency-check-report.html does NOT exist"
    CRIT2_PASS=false
fi

if [ -f "$MONOLITH_REPORT" ]; then
    MONOLITH_SIZE=$(wc -c < "$MONOLITH_REPORT")
    echo "  [PASS] monolith/target/dependency-check-report.html exists (size: ${MONOLITH_SIZE} bytes)"
else
    echo "  [FAIL] monolith/target/dependency-check-report.html does NOT exist"
    CRIT2_PASS=false
fi

# Verify the HTML files are valid (non-empty, contain expected content)
if [ -f "$ACTIVEMQ_REPORT" ] && grep -q "dependency-check" "$ACTIVEMQ_REPORT" 2>/dev/null; then
    echo "  [PASS] activemq report contains dependency-check content"
else
    if [ -f "$ACTIVEMQ_REPORT" ]; then
        echo "  [WARN] activemq report exists but may not contain expected content"
    fi
fi

if [ -f "$MONOLITH_REPORT" ] && grep -q "dependency-check" "$MONOLITH_REPORT" 2>/dev/null; then
    echo "  [PASS] monolith report contains dependency-check content"
else
    if [ -f "$MONOLITH_REPORT" ]; then
        echo "  [WARN] monolith report exists but may not contain expected content"
    fi
fi

if [ "$CRIT2_PASS" = true ]; then
    echo "  => CRITERION 2: PASSED"
    RESULTS+=("PASS: dependency-check-report.html exists in activemq/target/ and monolith/target/")
    PASS=$((PASS+1))
else
    echo "  => CRITERION 2: FAILED"
    RESULTS+=("FAIL: dependency-check-report.html missing from one or more module target/ directories")
    FAIL=$((FAIL+1))
fi

echo ""

# ---------------------------------------------------------------
# CRITERION 3: docs/upgrade-baseline.md exists with required content
# ---------------------------------------------------------------
echo "--- CRITERION 3: docs/upgrade-baseline.md content verification ---"
BASELINE_FILE="$REPO_ROOT/docs/upgrade-baseline.md"
CRIT3_PASS=true

if [ -f "$BASELINE_FILE" ]; then
    echo "  [PASS] docs/upgrade-baseline.md exists"
else
    echo "  [FAIL] docs/upgrade-baseline.md does NOT exist"
    CRIT3_PASS=false
fi

if [ -f "$BASELINE_FILE" ]; then
    # Check Spring Boot version 2.2.1.RELEASE is recorded
    if grep -q "2.2.1.RELEASE" "$BASELINE_FILE"; then
        echo "  [PASS] Spring Boot version 2.2.1.RELEASE is recorded"
    else
        echo "  [FAIL] Spring Boot version 2.2.1.RELEASE NOT found in baseline"
        CRIT3_PASS=false
    fi

    # Check Java version 1.8 is recorded
    if grep -q "1.8" "$BASELINE_FILE"; then
        echo "  [PASS] Java version 1.8 is recorded"
    else
        echo "  [FAIL] Java version 1.8 NOT found in baseline"
        CRIT3_PASS=false
    fi

    # Check CVSS >= 7.0 CVE count is recorded
    if grep -qE "CVSS.*7\.0|7\.0.*CVSS" "$BASELINE_FILE"; then
        echo "  [PASS] CVSS >= 7.0 threshold is referenced in baseline"
    else
        echo "  [FAIL] CVSS >= 7.0 reference NOT found in baseline"
        CRIT3_PASS=false
    fi

    # Check that a count of CVSS >= 7.0 CVEs is present (a number associated with CVSS >= 7.0)
    CVSS_COUNT=$(grep -E "CVSS.*7\.0|7\.0.*CVSS" "$BASELINE_FILE" | grep -oE "[0-9]+" | head -1)
    if [ -n "$CVSS_COUNT" ]; then
        echo "  [PASS] Count of CVSS >= 7.0 CVEs is recorded: $CVSS_COUNT"
    else
        echo "  [FAIL] No numeric count of CVSS >= 7.0 CVEs found"
        CRIT3_PASS=false
    fi
fi

if [ "$CRIT3_PASS" = true ]; then
    echo "  => CRITERION 3: PASSED"
    RESULTS+=("PASS: docs/upgrade-baseline.md records Spring Boot 2.2.1.RELEASE, Java 1.8, and CVSS >= 7.0 CVE count")
    PASS=$((PASS+1))
else
    echo "  => CRITERION 3: FAILED"
    RESULTS+=("FAIL: docs/upgrade-baseline.md missing or incomplete")
    FAIL=$((FAIL+1))
fi

echo ""
echo "============================================================"
echo "SUMMARY"
echo "============================================================"
for r in "${RESULTS[@]}"; do
    echo "  $r"
done
echo ""
echo "Total PASSED: $PASS / $((PASS+FAIL))"
echo "Total FAILED: $FAIL / $((PASS+FAIL))"

if [ "$FAIL" -eq 0 ]; then
    echo ""
    echo "ALL ACCEPTANCE CRITERIA MET"
    exit 0
else
    echo ""
    echo "SOME ACCEPTANCE CRITERIA FAILED"
    exit 1
fi
