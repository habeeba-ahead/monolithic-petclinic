#!/bin/bash

# Verification script for javax.persistence -> jakarta.persistence migration
REPO_DIR="/app/output/d2787eaf-10a3-40da-8017-24f8cb61fda7/worker-2/repo"
MAIN_JAVA="${REPO_DIR}/monolith/src/main/java"
MODEL_DIR="${MAIN_JAVA}/org/springframework/samples/petclinic/model"

PASS=0
FAIL=0

echo "=== Acceptance Criteria Verification ==="
echo ""

# Criterion 1: No javax.persistence imports in main java
echo "--- Criterion 1: No javax.persistence imports ---"
JAVAX_COUNT=$(grep -r 'javax.persistence' "${MAIN_JAVA}/" 2>/dev/null | wc -l)
if [ "$JAVAX_COUNT" -eq 0 ]; then
    echo "PASS: grep -r 'javax.persistence' returns zero results (found: $JAVAX_COUNT)"
    PASS=$((PASS+1))
else
    echo "FAIL: grep -r 'javax.persistence' returns $JAVAX_COUNT results (expected: 0)"
    grep -r 'javax.persistence' "${MAIN_JAVA}/"
    FAIL=$((FAIL+1))
fi
echo ""

# Criterion 2: At least one jakarta.persistence import per entity file
echo "--- Criterion 2: jakarta.persistence imports in each entity file ---"
ENTITY_FILES=("BaseEntity" "Owner" "Pet" "PetType" "Visit" "Vet" "Specialty" "NamedEntity" "Person")
ALL_ENTITY_PASS=true
for entity in "${ENTITY_FILES[@]}"; do
    FILE="${MODEL_DIR}/${entity}.java"
    if [ ! -f "$FILE" ]; then
        echo "FAIL: ${entity}.java not found at $FILE"
        ALL_ENTITY_PASS=false
        FAIL=$((FAIL+1))
        continue
    fi
    COUNT=$(grep -c 'jakarta.persistence' "$FILE" 2>/dev/null || echo 0)
    if [ "$COUNT" -ge 1 ]; then
        echo "PASS: ${entity}.java has $COUNT jakarta.persistence import(s)"
    else
        echo "FAIL: ${entity}.java has NO jakarta.persistence imports"
        ALL_ENTITY_PASS=false
        FAIL=$((FAIL+1))
    fi
done
if [ "$ALL_ENTITY_PASS" = true ]; then
    PASS=$((PASS+1))
fi
echo ""

# Criterion 3: Compilation check
echo "--- Criterion 3: Compilation check ---"
cd "${REPO_DIR}"
COMPILE_OUTPUT=$(./mvnw compile -pl monolith -am -q 2>&1)
COMPILE_EXIT=$?
if [ $COMPILE_EXIT -eq 0 ]; then
    echo "PASS: Project compiles without errors"
    PASS=$((PASS+1))
else
    echo "FAIL: Compilation failed with exit code $COMPILE_EXIT"
    echo "Compilation errors:"
    echo "$COMPILE_OUTPUT" | grep -E "ERROR|error:" | head -30
    FAIL=$((FAIL+1))
fi
echo ""

echo "=== Summary ==="
echo "PASSED: $PASS"
echo "FAILED: $FAIL"
echo ""
if [ $FAIL -eq 0 ]; then
    echo "ALL CRITERIA MET"
    exit 0
else
    echo "SOME CRITERIA FAILED"
    exit 1
fi
