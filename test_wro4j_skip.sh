#!/bin/bash
# TDD test: Verify wro4j plugin has skip=true configuration in monolith/pom.xml
# This test should FAIL before the fix and PASS after the fix.

REPO_ROOT="$(cd "$(dirname "$0")" && pwd)"
POM_FILE="$REPO_ROOT/monolith/pom.xml"
CSS_FILE="$REPO_ROOT/monolith/src/main/resources/static/resources/css/petclinic.css"

FAILURES=0

echo "=== Test 1: wro4j plugin has <skip>true</skip> configuration ==="
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
    echo "PASS: wro4j plugin has <skip>true</skip> in its configuration"
else
    echo "FAIL: wro4j plugin does NOT have <skip>true</skip> in its configuration"
    echo "      Result: $SKIP_RESULT"
    FAILURES=$((FAILURES + 1))
fi

echo ""
echo "=== Test 2: Pre-compiled CSS file exists for static serving ==="
if [ -f "$CSS_FILE" ]; then
    echo "PASS: Pre-compiled CSS file exists at resources/static/resources/css/petclinic.css"
    CSS_LINES=$(wc -l < "$CSS_FILE")
    echo "      File has $CSS_LINES lines"
else
    echo "FAIL: Pre-compiled CSS file does NOT exist at $CSS_FILE"
    echo "      When wro4j is skipped, a pre-compiled CSS must be present for static serving"
    FAILURES=$((FAILURES + 1))
fi

echo ""
echo "=== Test 3: Maven compile succeeds ==="
export JAVA_HOME=/workspace/tools/jdk-11.0.21+9
export PATH=$JAVA_HOME/bin:$PATH

cd "$REPO_ROOT"
COMPILE_OUTPUT=$(./mvnw compile -pl monolith 2>&1)
COMPILE_EXIT=$?
if [ $COMPILE_EXIT -eq 0 ]; then
    echo "PASS: ./mvnw compile -pl monolith exits with code 0"
else
    echo "FAIL: ./mvnw compile -pl monolith failed with exit code $COMPILE_EXIT"
    echo "$COMPILE_OUTPUT" | tail -20
    FAILURES=$((FAILURES + 1))
fi

echo ""
if [ $FAILURES -eq 0 ]; then
    echo "=== All tests PASSED ==="
    exit 0
else
    echo "=== $FAILURES test(s) FAILED ==="
    exit 1
fi
