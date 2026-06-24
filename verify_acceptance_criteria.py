#!/usr/bin/env python3
"""
Verification tests for OWASP Dependency-Check Maven integration.
Verifies all acceptance criteria without requiring a full network build.
"""

import os
import sys
import subprocess
import xml.etree.ElementTree as ET

REPO_ROOT = os.path.dirname(os.path.abspath(__file__))
ROOT_POM = os.path.join(REPO_ROOT, "pom.xml")
MONOLITH_POM = os.path.join(REPO_ROOT, "monolith", "pom.xml")
ACTIVEMQ_POM = os.path.join(REPO_ROOT, "activemq", "pom.xml")

PASS = 0
FAIL = 0
RESULTS = []

def check(description, condition, detail=""):
    global PASS, FAIL
    if condition:
        print(f"  PASS: {description}")
        PASS += 1
        RESULTS.append(("PASS", description))
    else:
        print(f"  FAIL: {description}")
        if detail:
            print(f"        Detail: {detail}")
        FAIL += 1
        RESULTS.append(("FAIL", description))

def read_pom(path):
    with open(path, "r") as f:
        return f.read()

def parse_pom(path):
    tree = ET.parse(path)
    return tree.getroot()

# ─────────────────────────────────────────────────────────────────────────────
# CRITERION 1: Plugin version is pinned explicitly in root pom.xml pluginManagement
# ─────────────────────────────────────────────────────────────────────────────
print("\n=== Criterion 1: Plugin version pinned in root pom.xml pluginManagement ===")

root_pom_text = read_pom(ROOT_POM)

check(
    "Root pom.xml contains <pluginManagement> section",
    "<pluginManagement>" in root_pom_text
)

check(
    "Root pom.xml contains org.owasp groupId",
    "org.owasp" in root_pom_text
)

check(
    "Root pom.xml contains dependency-check-maven artifactId",
    "dependency-check-maven" in root_pom_text
)

# Parse XML to verify version is inside pluginManagement
ns = {"m": "http://maven.apache.org/POM/4.0.0"}
root = parse_pom(ROOT_POM)

plugin_mgmt = root.find(".//m:pluginManagement", ns)
check(
    "pluginManagement element exists in root pom.xml",
    plugin_mgmt is not None
)

if plugin_mgmt is not None:
    owasp_in_mgmt = None
    for plugin in plugin_mgmt.findall(".//m:plugin", ns):
        artifact = plugin.find("m:artifactId", ns)
        if artifact is not None and artifact.text == "dependency-check-maven":
            owasp_in_mgmt = plugin
            break

    check(
        "dependency-check-maven plugin is declared inside pluginManagement",
        owasp_in_mgmt is not None
    )

    if owasp_in_mgmt is not None:
        version_el = owasp_in_mgmt.find("m:version", ns)
        check(
            "Plugin version is explicitly pinned (not empty/missing)",
            version_el is not None and version_el.text and len(version_el.text.strip()) > 0,
            f"version element: {version_el}"
        )
        if version_el is not None:
            check(
                f"Plugin version is a concrete version (found: {version_el.text})",
                version_el.text and not version_el.text.startswith("$"),
                f"version: {version_el.text}"
            )

# ─────────────────────────────────────────────────────────────────────────────
# CRITERION 2: Build fails when CVSS >= 7.0 (failBuildOnCVSS=7 configured)
# ─────────────────────────────────────────────────────────────────────────────
print("\n=== Criterion 2: Build configured to fail on CVSS >= 7.0 ===")

check(
    "failBuildOnCVSS is set to 7 in root pom.xml",
    "<failBuildOnCVSS>7</failBuildOnCVSS>" in root_pom_text
)

# Verify it's inside the pluginManagement configuration
if plugin_mgmt is not None and owasp_in_mgmt is not None:
    config = owasp_in_mgmt.find("m:configuration", ns)
    check(
        "configuration element exists for dependency-check-maven in pluginManagement",
        config is not None
    )
    if config is not None:
        cvss_el = config.find("m:failBuildOnCVSS", ns)
        check(
            "failBuildOnCVSS element is present in plugin configuration",
            cvss_el is not None
        )
        if cvss_el is not None:
            check(
                f"failBuildOnCVSS value is 7 (found: {cvss_el.text})",
                cvss_el.text == "7"
            )

# ─────────────────────────────────────────────────────────────────────────────
# CRITERION 3: Report format is HTML (produces dependency-check-report.html)
# ─────────────────────────────────────────────────────────────────────────────
print("\n=== Criterion 3: Report format configured as HTML ===")

check(
    "format is set to HTML in root pom.xml",
    "<format>HTML</format>" in root_pom_text
)

if plugin_mgmt is not None and owasp_in_mgmt is not None:
    config = owasp_in_mgmt.find("m:configuration", ns)
    if config is not None:
        fmt_el = config.find("m:format", ns)
        check(
            "format element is present in plugin configuration",
            fmt_el is not None
        )
        if fmt_el is not None:
            check(
                f"format value is HTML (found: {fmt_el.text})",
                fmt_el.text == "HTML"
            )

# ─────────────────────────────────────────────────────────────────────────────
# CRITERION 4: Plugin is bound to verify phase (check goal)
# ─────────────────────────────────────────────────────────────────────────────
print("\n=== Criterion 4: Plugin bound to verify phase via 'check' goal ===")

check(
    "check goal is configured in root pom.xml",
    "<goal>check</goal>" in root_pom_text
)

if plugin_mgmt is not None and owasp_in_mgmt is not None:
    executions = owasp_in_mgmt.find("m:executions", ns)
    check(
        "executions element exists for dependency-check-maven",
        executions is not None
    )
    if executions is not None:
        goals = executions.findall(".//m:goal", ns)
        goal_texts = [g.text for g in goals]
        check(
            f"'check' goal is listed in executions (found: {goal_texts})",
            "check" in goal_texts
        )

# ─────────────────────────────────────────────────────────────────────────────
# CRITERION 5: Plugin is applied to both monolith and activemq modules
# ─────────────────────────────────────────────────────────────────────────────
print("\n=== Criterion 5: Plugin applied to both modules (monolith and activemq) ===")

# Check that root pom.xml has both modules declared
check(
    "Root pom.xml declares 'monolith' module",
    "<module>monolith</module>" in root_pom_text
)

check(
    "Root pom.xml declares 'activemq' module",
    "<module>activemq</module>" in root_pom_text
)

# Check that the plugin is in the root build/plugins section (applies to all modules)
build_plugins_section = root.find(".//m:build/m:plugins", ns)
owasp_in_build = None
if build_plugins_section is not None:
    for plugin in build_plugins_section.findall("m:plugin", ns):
        artifact = plugin.find("m:artifactId", ns)
        if artifact is not None and artifact.text == "dependency-check-maven":
            owasp_in_build = plugin
            break

check(
    "dependency-check-maven is declared in root build/plugins (applies to all child modules)",
    owasp_in_build is not None
)

# Verify monolith pom.xml inherits from root
monolith_text = read_pom(MONOLITH_POM)
monolith_root = parse_pom(MONOLITH_POM)
parent_el = monolith_root.find("m:parent", ns)
check(
    "monolith/pom.xml has parent pointing to petclinic-root",
    parent_el is not None and "petclinic-root" in monolith_text
)

# Verify activemq pom.xml inherits from root
activemq_text = read_pom(ACTIVEMQ_POM)
activemq_root = parse_pom(ACTIVEMQ_POM)
parent_el_amq = activemq_root.find("m:parent", ns)
check(
    "activemq/pom.xml has parent pointing to petclinic-root",
    parent_el_amq is not None and "petclinic-root" in activemq_text
)

# ─────────────────────────────────────────────────────────────────────────────
# CRITERION 6: Verify Maven can parse the POM files (syntax check)
# ─────────────────────────────────────────────────────────────────────────────
print("\n=== Criterion 6: Maven POM syntax validation ===")

result = subprocess.run(
    ["mvn", "help:effective-pom", "-N", "-q"],
    cwd=REPO_ROOT,
    capture_output=True,
    text=True,
    timeout=120
)
check(
    "Root pom.xml is valid Maven POM (mvn help:effective-pom -N succeeds)",
    result.returncode == 0,
    f"stdout: {result.stdout[:500]}\nstderr: {result.stderr[:500]}" if result.returncode != 0 else ""
)

# ─────────────────────────────────────────────────────────────────────────────
# CRITERION 7: Verify plugin is resolvable (mvn dependency:resolve-plugins)
# ─────────────────────────────────────────────────────────────────────────────
print("\n=== Criterion 7: OWASP plugin is resolvable from Maven Central ===")

result2 = subprocess.run(
    ["mvn", "dependency:resolve-plugins", "-pl", ".", "-N", "-q",
     "--batch-mode", "-Dmaven.test.skip=true"],
    cwd=REPO_ROOT,
    capture_output=True,
    text=True,
    timeout=300
)
check(
    "OWASP dependency-check-maven plugin can be resolved from Maven Central",
    result2.returncode == 0,
    f"stdout: {result2.stdout[:500]}\nstderr: {result2.stderr[:500]}" if result2.returncode != 0 else ""
)

# ─────────────────────────────────────────────────────────────────────────────
# SUMMARY
# ─────────────────────────────────────────────────────────────────────────────
print(f"\n{'='*60}")
print(f"TOTAL: {PASS} passed, {FAIL} failed")
print(f"{'='*60}")

if FAIL > 0:
    print("\nFAILED TESTS:")
    for status, desc in RESULTS:
        if status == "FAIL":
            print(f"  - {desc}")
    sys.exit(1)
else:
    print("\nALL TESTS PASSED")
    sys.exit(0)
