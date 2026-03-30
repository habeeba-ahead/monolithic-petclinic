#!/bin/bash

# Test script to verify Spring Boot 3.2.x upgrade
echo "=== Verifying Spring Boot 3.2.x Upgrade ==="
echo

# Test 1: Verify Spring Boot parent version is 3.2.x
echo "1. Checking Spring Boot parent version..."
SPRING_BOOT_VERSION=$(grep -A 3 "<parent>" pom.xml | grep "<version>" | sed 's/.*<version>\(.*\)<\/version>.*/\1/' | tr -d ' ')
echo "Found Spring Boot version: $SPRING_BOOT_VERSION"

if [[ $SPRING_BOOT_VERSION =~ ^3\.2\. ]]; then
    echo "✓ Spring Boot parent version is 3.2.x"
    PARENT_VERSION_OK=true
else
    echo "✗ Spring Boot parent version is NOT 3.2.x (found: $SPRING_BOOT_VERSION)"
    PARENT_VERSION_OK=false
fi
echo

# Test 2: Verify Maven dependency resolution succeeds
echo "2. Testing Maven dependency resolution..."
mvn dependency:resolve -q
RESOLVE_EXIT_CODE=$?

if [ $RESOLVE_EXIT_CODE -eq 0 ]; then
    echo "✓ Maven dependency resolution succeeded"
    DEPENDENCY_RESOLUTION_OK=true
else
    echo "✗ Maven dependency resolution failed (exit code: $RESOLVE_EXIT_CODE)"
    DEPENDENCY_RESOLUTION_OK=false
fi
echo

# Test 3: Check for version conflicts in dependency tree
echo "3. Checking for version conflicts in dependency tree..."
mvn dependency:tree -Dverbose > dependency-tree.log 2>&1
TREE_EXIT_CODE=$?

if [ $TREE_EXIT_CODE -eq 0 ]; then
    # Check for conflict indicators in the dependency tree
    CONFLICTS=$(grep -i "conflict\|omitted for conflict\|version conflict" dependency-tree.log | wc -l)
    
    if [ $CONFLICTS -eq 0 ]; then
        echo "✓ No version conflicts detected in dependency tree"
        NO_CONFLICTS=true
    else
        echo "✗ Found $CONFLICTS potential version conflicts:"
        grep -i "conflict\|omitted for conflict\|version conflict" dependency-tree.log
        NO_CONFLICTS=false
    fi
else
    echo "✗ Failed to generate dependency tree (exit code: $TREE_EXIT_CODE)"
    NO_CONFLICTS=false
fi
echo

# Test 4: Verify project compiles successfully
echo "4. Testing project compilation..."
mvn compile -q
COMPILE_EXIT_CODE=$?

if [ $COMPILE_EXIT_CODE -eq 0 ]; then
    echo "✓ Project compiles successfully"
    COMPILATION_OK=true
else
    echo "✗ Project compilation failed (exit code: $COMPILE_EXIT_CODE)"
    COMPILATION_OK=false
fi
echo

# Summary
echo "=== SUMMARY ==="
if [ "$PARENT_VERSION_OK" = true ] && [ "$DEPENDENCY_RESOLUTION_OK" = true ] && [ "$NO_CONFLICTS" = true ] && [ "$COMPILATION_OK" = true ]; then
    echo "✓ All acceptance criteria met!"
    exit 0
else
    echo "✗ Some acceptance criteria failed:"
    [ "$PARENT_VERSION_OK" = false ] && echo "  - Spring Boot parent version not 3.2.x"
    [ "$DEPENDENCY_RESOLUTION_OK" = false ] && echo "  - Maven dependency resolution failed"
    [ "$NO_CONFLICTS" = false ] && echo "  - Version conflicts detected"
    [ "$COMPILATION_OK" = false ] && echo "  - Project compilation failed"
    exit 1
fi