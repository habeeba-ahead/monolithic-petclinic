#!/usr/bin/env python3
"""
TDD tests for Java 17 Maven compiler configuration.
Acceptance criteria:
  1. java.version property set to 17 in root pom.xml
  2. Maven compiler plugin configured for Java 17 source and target (if explicitly set)
"""

import sys
import xml.etree.ElementTree as ET

MAVEN_NS = "http://maven.apache.org/POM/4.0.0"

def parse_pom(path):
    tree = ET.parse(path)
    return tree.getroot()

def find_text(root, xpath):
    """Find element text using Maven namespace."""
    ns = {"m": MAVEN_NS}
    el = root.find(xpath, ns)
    return el.text.strip() if el is not None and el.text else None

def test_java_version_property_is_17():
    """Test that java.version property is set to 17 in root pom.xml."""
    root = parse_pom("pom.xml")
    java_version = find_text(root, ".//m:properties/m:java.version")
    assert java_version == "17", (
        f"Expected java.version=17 in root pom.xml, got: {java_version!r}"
    )
    print("PASS: java.version property is 17")

def test_compiler_plugin_source_is_17_if_configured():
    """
    Test that if maven-compiler-plugin is explicitly configured with source/target,
    those values are 17. If not explicitly configured, Spring Boot parent handles it
    via java.version property, so this test passes trivially.
    """
    root = parse_pom("pom.xml")
    ns = {"m": MAVEN_NS}

    # Look for maven-compiler-plugin configuration in root pom
    plugins = root.findall(".//m:plugin", ns)
    compiler_plugin = None
    for plugin in plugins:
        artifact_id = find_text(plugin, "m:artifactId")
        if artifact_id == "maven-compiler-plugin":
            compiler_plugin = plugin
            break

    if compiler_plugin is None:
        print("PASS: No explicit maven-compiler-plugin config (Spring Boot parent handles via java.version)")
        return

    # If explicitly configured, check source and target
    source = find_text(compiler_plugin, ".//m:source")
    target = find_text(compiler_plugin, ".//m:target")
    release = find_text(compiler_plugin, ".//m:release")

    if release is not None:
        assert release == "17", f"Expected compiler release=17, got: {release!r}"
        print(f"PASS: maven-compiler-plugin release is {release}")
    else:
        if source is not None:
            assert source == "17", f"Expected compiler source=17, got: {source!r}"
        if target is not None:
            assert target == "17", f"Expected compiler target=17, got: {target!r}"
        print(f"PASS: maven-compiler-plugin source={source}, target={target}")

def test_java_version_not_18():
    """Test that java.version is not the old 1.8 value."""
    root = parse_pom("pom.xml")
    java_version = find_text(root, ".//m:properties/m:java.version")
    assert java_version != "1.8", (
        f"java.version is still 1.8 (Java 8) - must be updated to 17"
    )
    print("PASS: java.version is not 1.8 (old Java 8 value)")

def test_java_version_not_none():
    """Test that java.version property exists."""
    root = parse_pom("pom.xml")
    java_version = find_text(root, ".//m:properties/m:java.version")
    assert java_version is not None, "java.version property not found in pom.xml"
    print(f"PASS: java.version property exists with value: {java_version}")

if __name__ == "__main__":
    tests = [
        test_java_version_not_none,
        test_java_version_not_18,
        test_java_version_property_is_17,
        test_compiler_plugin_source_is_17_if_configured,
    ]

    failures = []
    for test in tests:
        try:
            test()
        except AssertionError as e:
            print(f"FAIL: {test.__name__}: {e}")
            failures.append(test.__name__)
        except Exception as e:
            print(f"ERROR: {test.__name__}: {e}")
            failures.append(test.__name__)

    print()
    if failures:
        print(f"FAILED: {len(failures)}/{len(tests)} tests failed: {failures}")
        sys.exit(1)
    else:
        print(f"ALL {len(tests)} TESTS PASSED")
        sys.exit(0)
