#!/bin/bash

# Comprehensive verification script for javax.persistence -> jakarta.persistence migration
REPO_DIR="/app/output/d2787eaf-10a3-40da-8017-24f8cb61fda7/worker-2/repo"
MAIN_JAVA="${REPO_DIR}/monolith/src/main/java"
MODEL_DIR="${MAIN_JAVA}/org/springframework/samples/petclinic/model"

PASS=0
FAIL=0
CRITERIA_MET=()
CRITERIA_FAILED=()

echo "=== Acceptance Criteria Verification ==="
echo ""

# ============================================================
# Criterion 1: No javax.persistence imports in main java
# ============================================================
echo "--- Criterion 1: grep -r 'javax.persistence' returns zero results ---"
JAVAX_RESULTS=$(grep -r 'javax.persistence' "${MAIN_JAVA}/" 2>/dev/null)
JAVAX_COUNT=$(echo "$JAVAX_RESULTS" | grep -c 'javax.persistence' 2>/dev/null || echo 0)
if [ -z "$JAVAX_RESULTS" ]; then
    echo "PASS: grep -r 'javax.persistence' monolith/src/main/java/ returns zero results"
    PASS=$((PASS+1))
    CRITERIA_MET+=("grep -r 'javax.persistence' monolith/src/main/java/ returns zero results")
else
    echo "FAIL: grep -r 'javax.persistence' found results:"
    echo "$JAVAX_RESULTS"
    FAIL=$((FAIL+1))
    CRITERIA_FAILED+=("grep -r 'javax.persistence' monolith/src/main/java/ returns zero results")
fi
echo ""

# ============================================================
# Criterion 2: At least one jakarta.persistence import per entity file
# ============================================================
echo "--- Criterion 2: jakarta.persistence imports in each entity file ---"
ENTITY_FILES=("BaseEntity.java" "Owner.java" "Pet.java" "PetType.java" "Visit.java" "Vet.java" "Specialty.java" "NamedEntity.java" "Person.java")
ALL_ENTITY_PASS=true
for entity in "${ENTITY_FILES[@]}"; do
    FILE="${MODEL_DIR}/${entity}"
    if [ ! -f "$FILE" ]; then
        echo "FAIL: ${entity} not found at $FILE"
        ALL_ENTITY_PASS=false
        continue
    fi
    COUNT=$(grep -c 'jakarta.persistence' "$FILE" 2>/dev/null || echo 0)
    if [ "$COUNT" -ge 1 ]; then
        echo "PASS: ${entity} has $COUNT jakarta.persistence import(s)"
    else
        echo "FAIL: ${entity} has NO jakarta.persistence imports"
        ALL_ENTITY_PASS=false
    fi
done
if [ "$ALL_ENTITY_PASS" = true ]; then
    PASS=$((PASS+1))
    CRITERIA_MET+=("grep -r 'jakarta.persistence' returns at least one result per entity file")
else
    FAIL=$((FAIL+1))
    CRITERIA_FAILED+=("grep -r 'jakarta.persistence' returns at least one result per entity file")
fi
echo ""

# ============================================================
# Criterion 3: Compilation check
# ============================================================
echo "--- Criterion 3: Entity files compile without errors ---"
cd "${REPO_DIR}"
COMPILE_OUTPUT=$(./mvnw compile -pl monolith -am --no-transfer-progress 2>&1)
COMPILE_EXIT=$?
if [ $COMPILE_EXIT -eq 0 ]; then
    echo "PASS: Project compiles without errors"
    PASS=$((PASS+1))
    CRITERIA_MET+=("Entity files compile without errors: BaseEntity.java, Owner.java, Pet.java, PetType.java, Visit.java, Vet.java, Specialty.java, NamedEntity.java, Person.java")
else
    echo "FAIL: Compilation failed with exit code $COMPILE_EXIT"
    echo "Key compilation errors:"
    echo "$COMPILE_OUTPUT" | grep -E "ERROR.*\.java" | head -20
    FAIL=$((FAIL+1))
    CRITERIA_FAILED+=("Entity files compile without errors: BaseEntity.java, Owner.java, Pet.java, PetType.java, Visit.java, Vet.java, Specialty.java, NamedEntity.java, Person.java")
fi
echo ""

# ============================================================
# Summary
# ============================================================
echo "=== Summary ==="
echo "PASSED: $PASS"
echo "FAILED: $FAIL"
echo ""
echo "Criteria Met:"
for c in "${CRITERIA_MET[@]}"; do
    echo "  ✓ $c"
done
echo ""
echo "Criteria Failed:"
for c in "${CRITERIA_FAILED[@]}"; do
    echo "  ✗ $c"
done
echo ""
if [ $FAIL -eq 0 ]; then
    echo "ALL CRITERIA MET"
    exit 0
else
    echo "SOME CRITERIA FAILED"
    exit 1
fi
