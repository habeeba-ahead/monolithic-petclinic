"""
TDD tests for docs/config-inventory.md

Acceptance criteria:
1. docs/config-inventory.md exists
2. Every property key from both application.properties files is listed
3. Each entry documents: current value, whether env-specific, whether it contains a credential
4. All localhost assumptions are explicitly flagged
5. All hardcoded credentials (if any) are flagged as requiring Secret Manager migration
"""

import os
import re
import sys

REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
INVENTORY_PATH = os.path.join(REPO_ROOT, "docs", "config-inventory.md")

# All property keys from monolith/src/main/resources/application.properties
MONOLITH_KEYS = [
    "spring.datasource.schema",
    "spring.datasource.data",
    "spring.thymeleaf.mode",
    "spring.jpa.hibernate.ddl-auto",
    "spring.activemq.broker-url",
    "spring.messages.basename",
    "management.endpoints.web.base-path",
    "management.endpoints.web.exposure.include",
    "logging.level.org.springframework",
    "server.port",
]

# All property keys from activemq/src/main/resources/application.properties
ACTIVEMQ_KEYS = [
    "activemq.broker-url",
    "spring.jms.template.default-destination",
    "management.endpoints.web.base-path",
    "management.endpoints.web.exposure.include",
    "hawtio.authenticationEnabled",
    "logging.level.org.springframework",
    "server.port",
]

# Values that contain localhost — must be flagged
LOCALHOST_VALUES = [
    "tcp://localhost:61616",   # monolith spring.activemq.broker-url
]

# Values that bind to all interfaces (0.0.0.0) — also environment-specific
BIND_ALL_VALUES = [
    "tcp://0.0.0.0:61616",    # activemq activemq.broker-url
]

# Credentials: hawtio.authenticationEnabled=false disables auth — flag it
CREDENTIAL_FLAGS = [
    "hawtio.authenticationEnabled",
]

FAILURES = []


def check(condition, message):
    if not condition:
        FAILURES.append(f"FAIL: {message}")
    else:
        print(f"PASS: {message}")


def run_tests():
    # Test 1: file exists
    check(os.path.isfile(INVENTORY_PATH), "docs/config-inventory.md exists")

    if not os.path.isfile(INVENTORY_PATH):
        print("\nCannot continue — inventory file missing.")
        return

    with open(INVENTORY_PATH, "r") as f:
        content = f.read()

    content_lower = content.lower()

    # Test 2: every monolith key is present
    for key in MONOLITH_KEYS:
        check(key in content, f"Monolith key '{key}' is listed in inventory")

    # Test 3: every activemq key is present
    for key in ACTIVEMQ_KEYS:
        check(key in content, f"ActiveMQ key '{key}' is listed in inventory")

    # Test 4: current values are documented
    check("tcp://localhost:61616" in content,
          "Value 'tcp://localhost:61616' is documented")
    check("tcp://0.0.0.0:61616" in content,
          "Value 'tcp://0.0.0.0:61616' is documented")
    check("classpath*:db/schema.sql" in content,
          "Value 'classpath*:db/schema.sql' is documented")
    check("classpath*:db/data.sql" in content,
          "Value 'classpath*:db/data.sql' is documented")
    check("visitCreated" in content,
          "Value 'visitCreated' is documented")
    check("hawtio,jolokia" in content,
          "Value 'hawtio,jolokia' is documented")

    # Test 5: env-specific column/flag present
    check(
        "env" in content_lower or "environment" in content_lower,
        "Inventory contains 'env' or 'environment' column/flag"
    )

    # Test 6: credential column/flag present
    check(
        "credential" in content_lower or "secret" in content_lower,
        "Inventory contains 'credential' or 'secret' column/flag"
    )

    # Test 7: localhost is explicitly flagged
    # The word "localhost" must appear AND some flag/warning near it
    localhost_section = re.search(
        r"localhost.{0,200}(flag|warn|yes|⚠|🚨|environment|env-specific|hardcoded)",
        content, re.IGNORECASE | re.DOTALL
    )
    check(
        localhost_section is not None,
        "localhost assumptions are explicitly flagged in the inventory"
    )

    # Test 8: 0.0.0.0 bind-all is flagged as environment-specific
    bind_all_flagged = re.search(
        r"0\.0\.0\.0.{0,200}(yes|flag|warn|⚠|environment|env-specific|hardcoded)",
        content, re.IGNORECASE | re.DOTALL
    )
    check(
        bind_all_flagged is not None,
        "0.0.0.0 bind-all address is flagged as environment-specific"
    )

    # Test 9: hawtio.authenticationEnabled=false is flagged
    hawtio_flagged = re.search(
        r"hawtio\.authenticationEnabled.{0,300}(credential|secret|flag|warn|yes|⚠|security|migration)",
        content, re.IGNORECASE | re.DOTALL
    )
    check(
        hawtio_flagged is not None,
        "hawtio.authenticationEnabled is flagged (credential/security concern)"
    )

    # Test 10: Secret Manager migration mentioned for credentials
    check(
        "secret manager" in content_lower or "secretmanager" in content_lower,
        "Secret Manager migration is mentioned for credentials"
    )

    # Test 11: both source files are identified in the document
    check(
        "monolith" in content_lower,
        "Document identifies monolith application.properties as a source"
    )
    check(
        "activemq" in content_lower,
        "Document identifies activemq application.properties as a source"
    )

    # Test 12: server ports are documented
    check("8080" in content, "Monolith server port 8080 is documented")
    check("8081" in content, "ActiveMQ server port 8081 is documented")

    # Test 13: management base paths are documented
    check("/manage" in content, "Monolith management base-path '/manage' is documented")

    # Summary
    print()
    if FAILURES:
        for f in FAILURES:
            print(f)
        print(f"\n{len(FAILURES)} test(s) FAILED.")
        sys.exit(1)
    else:
        print("All tests PASSED.")
        sys.exit(0)


if __name__ == "__main__":
    run_tests()
