#!/bin/bash
# Comprehensive acceptance criteria tests for wro4j Java 17 compatibility

REPO_ROOT="$(cd "$(dirname "$0")" && pwd)"
POM_FILE="$REPO_ROOT/monolith/pom.xml"
CSS_FILE="$REPO_ROOT/monolith/src/main/resources/static/resources/css/petclinic.css"

export JAVA_HOME=/workspace/tools/jdk-11.0.21+9
export PATH=$JAVA_HOME/bin:$PATH

FAILURES=0
PASSES=0

echo "============================================================"
echo "  Acceptance Criteria Tests: wro4j Java 17 Compatibility"
echo "============================================================"
echo ""

# ---------------------------------------------------------------
# Criterion 1: ./mvnw compile -pl monolith exits with code 0
# ---------------------------------------------------------------
echo "=== Criterion 1: ./mvnw compile -pl monolith exits with code 0 ==="
cd "$REPO_ROOT"
COMPILE_OUTPUT=$(./mvnw compile -pl monolith 2>&1)
COMPILE_EXIT=$?
if [ $COMPILE_EXIT -eq 0 ]; then
    echo "PASS: ./mvnw compile -pl monolith exited with code 0"
    PASSES=$((PASSES + 1))
else
    echo "FAIL: ./mvnw compile -pl monolith failed with exit code $COMPILE_EXIT"
    echo "--- Last 20 lines of output ---"
    echo "$COMPILE_OUTPUT" | tail -20
    FAILURES=$((FAILURES + 1))
fi
echo ""

# ---------------------------------------------------------------
# Criterion 2a: wro4j plugin has <skip>true</skip> in configuration
# ---------------------------------------------------------------
echo "=== Criterion 2a: monolith/pom.xml contains wro4j plugin with <skip>true</skip> ==="
SKIP_RESULT=$(python3 -c "
import xml.etree.ElementTree as ET
tree = ET.parse('$POM_FILE')
root = tree.getroot()
ns = {'m': 'http://maven.apache.org/POM/4.0.0'}
plugins = root.findall('.//m:plugin', ns)
for plugin in plugins:
    artifact = plugin.find('m:artifactId', ns)
    if artifact is not None and 'wro4j' in artifact.text:
        config = plugin.find('m:configuration', ns)
        if config is not None:
            skip = config.find('m:skip', ns)
            if skip is not None and skip.text == 'true':
                print('FOUND')
                exit(0)
print('NOT_FOUND')
exit(1)
" 2>&1)

if [ "$SKIP_RESULT" = "FOUND" ]; then
    echo "PASS: wro4j plugin has <configuration><skip>true</skip></configuration>"
    PASSES=$((PASSES + 1))
else
    echo "FAIL: wro4j plugin does NOT have <skip>true</skip> in its configuration"
    FAILURES=$((FAILURES + 1))
fi
echo ""

# ---------------------------------------------------------------
# Criterion 2b: wro4j plugin block exists in monolith/pom.xml
# ---------------------------------------------------------------
echo "=== Criterion 2b: wro4j plugin block exists in monolith/pom.xml ==="
if grep -q "wro4j-maven-plugin" "$POM_FILE"; then
    echo "PASS: wro4j-maven-plugin block found in monolith/pom.xml"
    PASSES=$((PASSES + 1))
else
    echo "FAIL: wro4j-maven-plugin block NOT found in monolith/pom.xml"
    FAILURES=$((FAILURES + 1))
fi
echo ""

# ---------------------------------------------------------------
# Criterion 3a: Pre-compiled CSS file exists for static serving
# ---------------------------------------------------------------
echo "=== Criterion 3a: Pre-compiled CSS file exists at static/resources/css/petclinic.css ==="
if [ -f "$CSS_FILE" ]; then
    CSS_LINES=$(wc -l < "$CSS_FILE")
    CSS_SIZE=$(wc -c < "$CSS_FILE")
    echo "PASS: CSS file exists ($CSS_LINES lines, $CSS_SIZE bytes)"
    PASSES=$((PASSES + 1))
else
    echo "FAIL: CSS file NOT found at $CSS_FILE"
    echo "      When wro4j is skipped, a pre-compiled CSS must be present for static serving"
    FAILURES=$((FAILURES + 1))
fi
echo ""

# ---------------------------------------------------------------
# Criterion 3b: CSS file is non-empty and valid
# ---------------------------------------------------------------
echo "=== Criterion 3b: CSS file is non-empty and contains valid CSS ==="
if [ -f "$CSS_FILE" ] && [ -s "$CSS_FILE" ]; then
    # Check it has some CSS-like content
    if grep -q "{" "$CSS_FILE" && grep -q "}" "$CSS_FILE"; then
        echo "PASS: CSS file is non-empty and contains valid CSS content"
        PASSES=$((PASSES + 1))
    else
        echo "FAIL: CSS file exists but does not appear to contain valid CSS"
        FAILURES=$((FAILURES + 1))
    fi
else
    echo "FAIL: CSS file is empty or does not exist"
    FAILURES=$((FAILURES + 1))
fi
echo ""

# ---------------------------------------------------------------
# Criterion 3c: Layout template references the CSS file correctly
# ---------------------------------------------------------------
echo "=== Criterion 3c: Layout template references /resources/css/petclinic.css ==="
LAYOUT_FILE="$REPO_ROOT/monolith/src/main/resources/templates/fragments/layout.html"
if [ -f "$LAYOUT_FILE" ]; then
    if grep -q "resources/css/petclinic.css" "$LAYOUT_FILE"; then
        echo "PASS: layout.html references /resources/css/petclinic.css"
        PASSES=$((PASSES + 1))
    else
        echo "FAIL: layout.html does NOT reference /resources/css/petclinic.css"
        FAILURES=$((FAILURES + 1))
    fi
else
    echo "FAIL: layout.html not found at $LAYOUT_FILE"
    FAILURES=$((FAILURES + 1))
fi
echo ""

# ---------------------------------------------------------------
# Criterion 3d: Compiled classes include the CSS file
# ---------------------------------------------------------------
echo "=== Criterion 3d: After compile, CSS is in target/classes/static/resources/css/ ==="
TARGET_CSS="$REPO_ROOT/monolith/target/classes/static/resources/css/petclinic.css"
if [ -f "$TARGET_CSS" ]; then
    TARGET_SIZE=$(wc -c < "$TARGET_CSS")
    echo "PASS: CSS file found in target/classes ($TARGET_SIZE bytes)"
    PASSES=$((PASSES + 1))
else
    echo "FAIL: CSS file NOT found in target/classes/static/resources/css/petclinic.css"
    echo "      The CSS file should be copied to target during compile"
    FAILURES=$((FAILURES + 1))
fi
echo ""

# ---------------------------------------------------------------
# Criterion 3e: wro4j skip message appears in compile output
# ---------------------------------------------------------------
echo "=== Criterion 3e: wro4j 'Skipping execution' message appears during compile ==="
cd "$REPO_ROOT"
COMPILE_OUT=$(./mvnw compile -pl monolith 2>&1)
if echo "$COMPILE_OUT" | grep -q "Skipping execution"; then
    echo "PASS: wro4j plugin reports 'Skipping execution' during compile"
    PASSES=$((PASSES + 1))
else
    echo "FAIL: wro4j plugin did NOT report 'Skipping execution' during compile"
    echo "      (skip=true may not be working)"
    FAILURES=$((FAILURES + 1))
fi
echo ""

# ---------------------------------------------------------------
# Criterion 3f: BUILD SUCCESS in compile output
# ---------------------------------------------------------------
echo "=== Criterion 3f: Compile output contains BUILD SUCCESS ==="
if echo "$COMPILE_OUT" | grep -q "BUILD SUCCESS"; then
    echo "PASS: Compile output contains BUILD SUCCESS"
    PASSES=$((PASSES + 1))
else
    echo "FAIL: Compile output does NOT contain BUILD SUCCESS"
    FAILURES=$((FAILURES + 1))
fi
echo ""

# ---------------------------------------------------------------
# Summary
# ---------------------------------------------------------------
echo "============================================================"
echo "  Results: $PASSES passed, $FAILURES failed"
echo "============================================================"

if [ $FAILURES -eq 0 ]; then
    echo "  ALL TESTS PASSED"
    exit 0
else
    echo "  $FAILURES TEST(S) FAILED"
    exit 1
fi
