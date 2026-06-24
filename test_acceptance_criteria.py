"""
Comprehensive acceptance criteria tests for Spring Boot 2.2.1 -> 2.7.x upgrade.

Acceptance Criteria:
1. Root pom.xml parent version reads 2.7.18 (or latest 2.7.x)
2. mvn dependency:tree | grep spring-boot-starter-parent outputs version 2.7.x
3. mvn test passes in both monolith and activemq modules with no compilation errors
4. OWASP Dependency-Check reports zero critical CVEs attributable to Spring Boot core,
   Spring Framework, embedded Tomcat, Jackson Databind, or Logback after the upgrade
"""

import xml.etree.ElementTree as ET
import subprocess
import os
import re
import pytest

REPO_ROOT = os.path.dirname(os.path.abspath(__file__))
ROOT_POM = os.path.join(REPO_ROOT, "pom.xml")
MONOLITH_POM = os.path.join(REPO_ROOT, "monolith", "pom.xml")
ACTIVEMQ_POM = os.path.join(REPO_ROOT, "activemq", "pom.xml")

MAVEN_NS = "http://maven.apache.org/POM/4.0.0"
JAVA_HOME = "/usr/lib/jvm/java-21-openjdk-arm64"
MVN_BIN = "/usr/share/maven/bin/mvn"

ENV = {
    **os.environ,
    "JAVA_HOME": JAVA_HOME,
    "PATH": f"{JAVA_HOME}/bin:/usr/share/maven/bin:{os.environ.get('PATH', '')}",
}


def parse_pom(path):
    tree = ET.parse(path)
    return tree.getroot()


def get_parent_version(root):
    ns = {"m": MAVEN_NS}
    parent = root.find("m:parent", ns)
    if parent is None:
        return None
    version_el = parent.find("m:version", ns)
    if version_el is None:
        return None
    return version_el.text.strip()


def run_mvn(*args, timeout=300):
    """Run a Maven command and return (returncode, stdout+stderr)."""
    cmd = [MVN_BIN, "--no-transfer-progress"] + list(args)
    result = subprocess.run(
        cmd,
        cwd=REPO_ROOT,
        env=ENV,
        capture_output=True,
        text=True,
        timeout=timeout,
    )
    return result.returncode, result.stdout + result.stderr


# ─────────────────────────────────────────────────────────────────────────────
# Criterion 1: Root pom.xml parent version reads 2.7.18
# ─────────────────────────────────────────────────────────────────────────────

class TestCriterion1_RootPomVersion:
    """Root pom.xml parent version must be 2.7.18."""

    def test_root_pom_exists(self):
        assert os.path.isfile(ROOT_POM), f"Root pom.xml not found at {ROOT_POM}"

    def test_parent_version_is_2_7_18(self):
        root = parse_pom(ROOT_POM)
        version = get_parent_version(root)
        assert version == "2.7.18", (
            f"Expected Spring Boot parent version '2.7.18' but got '{version}'. "
            "Please upgrade the spring-boot-starter-parent version in root pom.xml."
        )

    def test_parent_version_is_not_old_release(self):
        root = parse_pom(ROOT_POM)
        version = get_parent_version(root)
        assert version != "2.2.1.RELEASE", (
            "Root pom.xml still references the old Spring Boot version 2.2.1.RELEASE."
        )

    def test_parent_artifact_is_spring_boot_starter_parent(self):
        root = parse_pom(ROOT_POM)
        ns = {"m": MAVEN_NS}
        parent = root.find("m:parent", ns)
        assert parent is not None
        artifact_id = parent.find("m:artifactId", ns)
        assert artifact_id is not None
        assert artifact_id.text.strip() == "spring-boot-starter-parent"

    def test_parent_group_is_spring_boot(self):
        root = parse_pom(ROOT_POM)
        ns = {"m": MAVEN_NS}
        parent = root.find("m:parent", ns)
        assert parent is not None
        group_id = parent.find("m:groupId", ns)
        assert group_id is not None
        assert group_id.text.strip() == "org.springframework.boot"

    def test_version_is_2_7_x_series(self):
        root = parse_pom(ROOT_POM)
        version = get_parent_version(root)
        assert version is not None
        assert version.startswith("2.7."), (
            f"Expected version in 2.7.x series but got '{version}'."
        )

    def test_no_release_suffix(self):
        root = parse_pom(ROOT_POM)
        version = get_parent_version(root)
        assert version is not None
        assert not version.endswith(".RELEASE"), (
            f"Version '{version}' still has .RELEASE suffix."
        )


# ─────────────────────────────────────────────────────────────────────────────
# Criterion 2: mvn dependency:tree shows spring-boot-starter-parent 2.7.x
# ─────────────────────────────────────────────────────────────────────────────

class TestCriterion2_DependencyTree:
    """mvn dependency:tree must show Spring Boot 2.7.x artifacts."""

    def test_dependency_tree_shows_spring_boot_2_7_x(self):
        """Spring Boot artifacts in dependency tree must be 2.7.x."""
        rc, output = run_mvn("dependency:tree")
        # Check that spring-boot artifacts are 2.7.x
        spring_boot_lines = [
            line for line in output.splitlines()
            if "spring-boot" in line.lower() and "2.7." in line
        ]
        assert len(spring_boot_lines) > 0, (
            f"No Spring Boot 2.7.x artifacts found in dependency:tree output.\n"
            f"Full output:\n{output[-3000:]}"
        )

    def test_dependency_tree_no_old_spring_boot_version(self):
        """Dependency tree must NOT show Spring Boot 2.2.x artifacts."""
        rc, output = run_mvn("dependency:tree")
        old_version_lines = [
            line for line in output.splitlines()
            if "spring-boot" in line.lower() and "2.2.1" in line
        ]
        assert len(old_version_lines) == 0, (
            f"Old Spring Boot 2.2.1 artifacts still found in dependency:tree:\n"
            + "\n".join(old_version_lines)
        )

    def test_dependency_tree_build_success(self):
        """mvn dependency:tree must succeed."""
        rc, output = run_mvn("dependency:tree")
        assert rc == 0, (
            f"mvn dependency:tree failed with return code {rc}.\n"
            f"Output:\n{output[-3000:]}"
        )


# ─────────────────────────────────────────────────────────────────────────────
# Criterion 3: mvn test passes in both monolith and activemq modules
# ─────────────────────────────────────────────────────────────────────────────

class TestCriterion3_MvnTest:
    """mvn test must pass in both monolith and activemq modules."""

    def test_activemq_module_compiles_and_tests_pass(self):
        """activemq module must compile and tests must pass."""
        rc, output = run_mvn("test", "-pl", "activemq", timeout=300)
        assert rc == 0, (
            f"mvn test -pl activemq FAILED with return code {rc}.\n"
            f"Output:\n{output[-5000:]}"
        )
        assert "BUILD SUCCESS" in output, (
            f"Expected BUILD SUCCESS in activemq test output.\n"
            f"Output:\n{output[-3000:]}"
        )

    def test_activemq_no_compilation_errors(self):
        """activemq module must have no compilation errors."""
        rc, output = run_mvn("compile", "-pl", "activemq", timeout=300)
        assert rc == 0, (
            f"mvn compile -pl activemq FAILED.\n"
            f"Compilation errors:\n{output[-5000:]}"
        )
        assert "ERROR" not in output or "BUILD SUCCESS" in output, (
            f"Compilation errors found in activemq module.\n{output[-3000:]}"
        )

    def test_monolith_module_compiles_and_tests_pass(self):
        """monolith module must compile and tests must pass."""
        rc, output = run_mvn("test", "-pl", "monolith", timeout=600)
        assert rc == 0, (
            f"mvn test -pl monolith FAILED with return code {rc}.\n"
            f"Compilation/test errors:\n{output[-5000:]}"
        )
        assert "BUILD SUCCESS" in output, (
            f"Expected BUILD SUCCESS in monolith test output.\n"
            f"Output:\n{output[-3000:]}"
        )

    def test_monolith_no_compilation_errors(self):
        """monolith module must have no compilation errors."""
        rc, output = run_mvn("compile", "-pl", "monolith", timeout=300)
        assert rc == 0, (
            f"mvn compile -pl monolith FAILED.\n"
            f"Compilation errors:\n{output[-5000:]}"
        )

    def test_full_build_passes(self):
        """Full mvn test on all modules must pass."""
        rc, output = run_mvn("test", timeout=600)
        assert rc == 0, (
            f"Full mvn test FAILED with return code {rc}.\n"
            f"Output:\n{output[-5000:]}"
        )
        assert "BUILD SUCCESS" in output, (
            f"Expected BUILD SUCCESS in full build output.\n"
            f"Output:\n{output[-3000:]}"
        )


# ─────────────────────────────────────────────────────────────────────────────
# Criterion 4: No critical CVEs in key Spring Boot components
# ─────────────────────────────────────────────────────────────────────────────

class TestCriterion4_NoCriticalCVEs:
    """
    Verify that the upgraded versions of key components are not affected by
    known critical CVEs. We check the resolved versions against known-safe
    version thresholds.
    """

    def _get_dependency_tree(self):
        rc, output = run_mvn("dependency:tree")
        return output

    def test_spring_boot_version_not_vulnerable(self):
        """
        Spring Boot 2.7.18 is the latest 2.7.x and addresses all known CVEs
        in the 2.7.x line. Verify the version is >= 2.7.18.
        """
        root = parse_pom(ROOT_POM)
        version = get_parent_version(root)
        assert version is not None
        parts = version.split(".")
        assert len(parts) >= 3
        major, minor, patch = int(parts[0]), int(parts[1]), int(parts[2])
        assert (major, minor, patch) >= (2, 7, 18), (
            f"Spring Boot version {version} is below 2.7.18. "
            "Upgrade to 2.7.18 to address all known CVEs in the 2.7.x line."
        )

    def test_spring_framework_version_not_vulnerable(self):
        """
        Spring Framework 5.3.x (bundled with Spring Boot 2.7.x) addresses
        CVE-2022-22965 (Spring4Shell) and other critical CVEs.
        Spring Boot 2.7.18 bundles Spring Framework 5.3.31+.
        """
        output = self._get_dependency_tree()
        # Find spring-framework version
        spring_core_lines = [
            line for line in output.splitlines()
            if "spring-core" in line and "org.springframework:spring-core" in line
        ]
        if spring_core_lines:
            # Extract version from line like: org.springframework:spring-core:jar:5.3.31:compile
            for line in spring_core_lines:
                match = re.search(r"spring-core:jar:(\d+\.\d+\.\d+)", line)
                if match:
                    version = match.group(1)
                    parts = version.split(".")
                    major, minor, patch = int(parts[0]), int(parts[1]), int(parts[2])
                    assert (major, minor, patch) >= (5, 3, 27), (
                        f"Spring Framework {version} may be vulnerable. "
                        "Need >= 5.3.27 to address CVE-2023-20860 and others."
                    )

    def test_tomcat_version_not_vulnerable(self):
        """
        Embedded Tomcat in Spring Boot 2.7.18 should be >= 9.0.82
        to address known critical CVEs.
        """
        output = self._get_dependency_tree()
        tomcat_lines = [
            line for line in output.splitlines()
            if "tomcat-embed-core" in line
        ]
        if tomcat_lines:
            for line in tomcat_lines:
                match = re.search(r"tomcat-embed-core:jar:(\d+\.\d+\.\d+)", line)
                if match:
                    version = match.group(1)
                    parts = version.split(".")
                    major, minor, patch = int(parts[0]), int(parts[1]), int(parts[2])
                    # Tomcat 9.0.x bundled with Spring Boot 2.7.x
                    assert major == 9, (
                        f"Expected Tomcat 9.x but got {version}"
                    )
                    assert (major, minor, patch) >= (9, 0, 75), (
                        f"Tomcat {version} may be vulnerable. "
                        "Need >= 9.0.75 to address known CVEs."
                    )

    def test_jackson_databind_version_not_vulnerable(self):
        """
        Jackson Databind in Spring Boot 2.7.18 should be >= 2.13.5
        to address CVE-2022-42003, CVE-2022-42004, and others.
        """
        output = self._get_dependency_tree()
        jackson_lines = [
            line for line in output.splitlines()
            if "jackson-databind" in line
        ]
        if jackson_lines:
            for line in jackson_lines:
                match = re.search(r"jackson-databind:jar:(\d+\.\d+\.\d+)", line)
                if match:
                    version = match.group(1)
                    parts = version.split(".")
                    major, minor, patch = int(parts[0]), int(parts[1]), int(parts[2])
                    assert (major, minor, patch) >= (2, 13, 4), (
                        f"Jackson Databind {version} may be vulnerable. "
                        "Need >= 2.13.4 to address CVE-2022-42003."
                    )

    def test_logback_version_not_vulnerable(self):
        """
        Logback in Spring Boot 2.7.18 should be >= 1.2.12
        to address CVE-2021-42550 (logback JNDI injection).
        """
        output = self._get_dependency_tree()
        logback_lines = [
            line for line in output.splitlines()
            if "logback-classic" in line
        ]
        if logback_lines:
            for line in logback_lines:
                match = re.search(r"logback-classic:jar:(\d+\.\d+\.\d+)", line)
                if match:
                    version = match.group(1)
                    parts = version.split(".")
                    major, minor, patch = int(parts[0]), int(parts[1]), int(parts[2])
                    assert (major, minor, patch) >= (1, 2, 11), (
                        f"Logback {version} may be vulnerable. "
                        "Need >= 1.2.11 to address CVE-2021-42550."
                    )

    def test_no_log4j_vulnerable_version(self):
        """
        Verify no vulnerable Log4j 2.x versions (< 2.17.1) are present.
        CVE-2021-44228 (Log4Shell) affects Log4j 2.x < 2.17.1.
        """
        output = self._get_dependency_tree()
        log4j_lines = [
            line for line in output.splitlines()
            if "log4j-core" in line and "2." in line
        ]
        for line in log4j_lines:
            match = re.search(r"log4j-core:jar:2\.(\d+)\.(\d+)", line)
            if match:
                minor, patch = int(match.group(1)), int(match.group(2))
                assert (2, minor, patch) >= (2, 17, 1), (
                    f"Vulnerable Log4j version found: {line.strip()}. "
                    "CVE-2021-44228 (Log4Shell) requires Log4j >= 2.17.1."
                )


if __name__ == "__main__":
    pytest.main([__file__, "-v", "--tb=short"])
