"""
TDD test to verify Spring Boot parent version upgrade from 2.2.1.RELEASE to 2.7.18.

This test verifies the acceptance criteria:
- Root pom.xml parent version reads 2.7.18
- The version is no longer 2.2.1.RELEASE
"""

import xml.etree.ElementTree as ET
import pytest
import os

REPO_ROOT = os.path.dirname(os.path.abspath(__file__))
ROOT_POM = os.path.join(REPO_ROOT, "pom.xml")

MAVEN_NS = "http://maven.apache.org/POM/4.0.0"


def parse_pom(path):
    tree = ET.parse(path)
    return tree.getroot()


def get_parent_version(root):
    """Extract the parent version from a pom.xml root element."""
    ns = {"m": MAVEN_NS}
    parent = root.find("m:parent", ns)
    if parent is None:
        return None
    version_el = parent.find("m:version", ns)
    if version_el is None:
        return None
    return version_el.text.strip()


class TestSpringBootVersionUpgrade:
    """Tests verifying the Spring Boot parent version upgrade in root pom.xml."""

    def test_root_pom_exists(self):
        """Root pom.xml must exist."""
        assert os.path.isfile(ROOT_POM), f"Root pom.xml not found at {ROOT_POM}"

    def test_parent_version_is_2_7_18(self):
        """Root pom.xml parent version must be 2.7.18 (Spring Boot 2.7.x latest)."""
        root = parse_pom(ROOT_POM)
        version = get_parent_version(root)
        assert version == "2.7.18", (
            f"Expected Spring Boot parent version '2.7.18' but got '{version}'. "
            "Please upgrade the spring-boot-starter-parent version in root pom.xml."
        )

    def test_parent_version_is_not_old_release(self):
        """Root pom.xml must NOT still reference the old 2.2.1.RELEASE version."""
        root = parse_pom(ROOT_POM)
        version = get_parent_version(root)
        assert version != "2.2.1.RELEASE", (
            "Root pom.xml still references the old Spring Boot version 2.2.1.RELEASE. "
            "Please upgrade to 2.7.18."
        )

    def test_parent_artifact_is_spring_boot_starter_parent(self):
        """The parent artifact must remain spring-boot-starter-parent."""
        root = parse_pom(ROOT_POM)
        ns = {"m": MAVEN_NS}
        parent = root.find("m:parent", ns)
        assert parent is not None, "No <parent> element found in root pom.xml"
        artifact_id = parent.find("m:artifactId", ns)
        assert artifact_id is not None, "No <artifactId> in parent element"
        assert artifact_id.text.strip() == "spring-boot-starter-parent", (
            f"Expected parent artifactId 'spring-boot-starter-parent' but got '{artifact_id.text.strip()}'"
        )

    def test_parent_group_is_spring_boot(self):
        """The parent groupId must remain org.springframework.boot."""
        root = parse_pom(ROOT_POM)
        ns = {"m": MAVEN_NS}
        parent = root.find("m:parent", ns)
        assert parent is not None, "No <parent> element found in root pom.xml"
        group_id = parent.find("m:groupId", ns)
        assert group_id is not None, "No <groupId> in parent element"
        assert group_id.text.strip() == "org.springframework.boot", (
            f"Expected parent groupId 'org.springframework.boot' but got '{group_id.text.strip()}'"
        )

    def test_version_is_2_7_x_series(self):
        """The version must be in the 2.7.x series."""
        root = parse_pom(ROOT_POM)
        version = get_parent_version(root)
        assert version is not None, "No version found in parent element"
        assert version.startswith("2.7."), (
            f"Expected version in 2.7.x series but got '{version}'. "
            "Spring Boot 2.7.x is the last supported 2.x line."
        )

    def test_no_release_suffix(self):
        """Spring Boot 2.7.x versions do not use .RELEASE suffix."""
        root = parse_pom(ROOT_POM)
        version = get_parent_version(root)
        assert version is not None, "No version found in parent element"
        assert not version.endswith(".RELEASE"), (
            f"Version '{version}' still has .RELEASE suffix. "
            "Spring Boot 2.7.x does not use .RELEASE suffix."
        )


if __name__ == "__main__":
    pytest.main([__file__, "-v"])
