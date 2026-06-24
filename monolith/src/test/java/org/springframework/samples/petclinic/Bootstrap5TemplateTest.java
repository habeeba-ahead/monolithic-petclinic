package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that Thymeleaf templates have been migrated from Bootstrap 3
 * to Bootstrap 5 class names.
 */
class Bootstrap5TemplateTest {

    private static final Path TEMPLATES_DIR = Paths.get(
            System.getProperty("user.dir"),
            "src/main/resources/templates");

    @ParameterizedTest
    @ValueSource(strings = {
            "fragments/layout.html",
            "fragments/inputField.html",
            "owners/ownersList.html",
            "owners/ownerDetails.html",
            "owners/createOrUpdateOwnerForm.html",
            "pets/createOrUpdatePetForm.html",
            "pets/createOrUpdateVisitForm.html",
            "vets/vetList.html",
            "welcome.html"
    })
    void templateShouldNotContainBtnDefault(String templatePath) throws IOException {
        String content = readTemplate(templatePath);
        assertThat(content)
                .as("Template %s should not contain Bootstrap 3 'btn-default' class", templatePath)
                .doesNotContain("btn-default");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "fragments/layout.html",
            "fragments/inputField.html",
            "owners/ownersList.html",
            "owners/ownerDetails.html",
            "owners/createOrUpdateOwnerForm.html",
            "pets/createOrUpdatePetForm.html",
            "pets/createOrUpdateVisitForm.html",
            "vets/vetList.html",
            "welcome.html"
    })
    void templateShouldNotContainNavbarDefault(String templatePath) throws IOException {
        String content = readTemplate(templatePath);
        assertThat(content)
                .as("Template %s should not contain Bootstrap 3 'navbar-default' class", templatePath)
                .doesNotContain("navbar-default");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "fragments/layout.html",
            "fragments/inputField.html",
            "owners/ownersList.html",
            "owners/ownerDetails.html",
            "owners/createOrUpdateOwnerForm.html",
            "pets/createOrUpdatePetForm.html",
            "pets/createOrUpdateVisitForm.html",
            "vets/vetList.html",
            "welcome.html"
    })
    void templateShouldNotContainGlyphicon(String templatePath) throws IOException {
        String content = readTemplate(templatePath);
        assertThat(content)
                .as("Template %s should not contain Bootstrap 3 'glyphicon' class (removed in Bootstrap 5)", templatePath)
                .doesNotContain("glyphicon");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "fragments/layout.html",
            "fragments/inputField.html",
            "owners/ownersList.html",
            "owners/ownerDetails.html",
            "owners/createOrUpdateOwnerForm.html",
            "pets/createOrUpdatePetForm.html",
            "pets/createOrUpdateVisitForm.html",
            "vets/vetList.html",
            "welcome.html"
    })
    void templateShouldNotContainDataToggle(String templatePath) throws IOException {
        String content = readTemplate(templatePath);
        assertThat(content)
                .as("Template %s should not contain Bootstrap 3 'data-toggle' attribute (Bootstrap 5 uses data-bs-toggle)", templatePath)
                .doesNotContain("data-toggle=");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "fragments/layout.html",
            "fragments/inputField.html",
            "owners/ownersList.html",
            "owners/ownerDetails.html",
            "owners/createOrUpdateOwnerForm.html",
            "pets/createOrUpdatePetForm.html",
            "pets/createOrUpdateVisitForm.html",
            "vets/vetList.html",
            "welcome.html"
    })
    void templateShouldNotContainDataTarget(String templatePath) throws IOException {
        String content = readTemplate(templatePath);
        assertThat(content)
                .as("Template %s should not contain Bootstrap 3 'data-target' attribute (Bootstrap 5 uses data-bs-target)", templatePath)
                .doesNotContain("data-target=");
    }

    @Test
    void layoutShouldContainBootstrap5NavbarClasses() throws IOException {
        String content = readTemplate("fragments/layout.html");
        assertThat(content)
                .as("layout.html should use Bootstrap 5 navbar classes")
                .contains("navbar-expand");
    }

    @Test
    void layoutShouldServeBootstrapJsFromWebjar() throws IOException {
        String content = readTemplate("fragments/layout.html");
        assertThat(content)
                .as("layout.html should reference bootstrap JS webjar")
                .contains("webjars/bootstrap");
    }

    @Test
    void layoutShouldServeJqueryFromWebjar() throws IOException {
        String content = readTemplate("fragments/layout.html");
        assertThat(content)
                .as("layout.html should reference jquery webjar")
                .contains("webjars/jquery");
    }

    @Test
    void ownerListShouldUseBtnSecondaryOrOtherBootstrap5ButtonClass() throws IOException {
        String content = readTemplate("owners/ownersList.html");
        assertThat(containsAnyBootstrap5ButtonClass(content))
                .as("ownersList.html should use a Bootstrap 5 button class (btn-secondary, btn-primary, etc.)")
                .isTrue();
    }

    @Test
    void ownerDetailsShouldUseBtnSecondaryOrOtherBootstrap5ButtonClass() throws IOException {
        String content = readTemplate("owners/ownerDetails.html");
        assertThat(containsAnyBootstrap5ButtonClass(content))
                .as("ownerDetails.html should use a Bootstrap 5 button class")
                .isTrue();
    }

    @Test
    void createOrUpdateOwnerFormShouldUseBtnSecondaryOrOtherBootstrap5ButtonClass() throws IOException {
        String content = readTemplate("owners/createOrUpdateOwnerForm.html");
        assertThat(containsAnyBootstrap5ButtonClass(content))
                .as("createOrUpdateOwnerForm.html should use a Bootstrap 5 button class")
                .isTrue();
    }

    @Test
    void createOrUpdatePetFormShouldUseBtnSecondaryOrOtherBootstrap5ButtonClass() throws IOException {
        String content = readTemplate("pets/createOrUpdatePetForm.html");
        assertThat(containsAnyBootstrap5ButtonClass(content))
                .as("createOrUpdatePetForm.html should use a Bootstrap 5 button class")
                .isTrue();
    }

    @Test
    void createOrUpdateVisitFormShouldUseBtnSecondaryOrOtherBootstrap5ButtonClass() throws IOException {
        String content = readTemplate("pets/createOrUpdateVisitForm.html");
        assertThat(containsAnyBootstrap5ButtonClass(content))
                .as("createOrUpdateVisitForm.html should use a Bootstrap 5 button class")
                .isTrue();
    }

    @Test
    void inputFieldShouldNotContainHasError() throws IOException {
        String content = readTemplate("fragments/inputField.html");
        // Bootstrap 5 uses 'is-invalid' instead of 'has-error'
        assertThat(content)
                .as("inputField.html should not use Bootstrap 3 'has-error' class")
                .doesNotContain("has-error");
    }

    @Test
    void inputFieldShouldUseIsInvalidForBootstrap5() throws IOException {
        String content = readTemplate("fragments/inputField.html");
        // Bootstrap 5 uses 'is-invalid' on the input element
        assertThat(content)
                .as("inputField.html should use Bootstrap 5 'is-invalid' class")
                .contains("is-invalid");
    }

    @Test
    void welcomeShouldUseImgFluidInsteadOfImgResponsive() throws IOException {
        String content = readTemplate("welcome.html");
        // Bootstrap 5 uses 'img-fluid' instead of 'img-responsive'
        assertThat(content)
                .as("welcome.html should not use Bootstrap 3 'img-responsive' class")
                .doesNotContain("img-responsive");
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private static final List<String> BOOTSTRAP5_BUTTON_CLASSES = Arrays.asList(
            "btn-secondary", "btn-primary", "btn-success",
            "btn-info", "btn-warning", "btn-danger", "btn-light", "btn-dark"
    );

    private boolean containsAnyBootstrap5ButtonClass(String content) {
        for (String cls : BOOTSTRAP5_BUTTON_CLASSES) {
            if (content.contains(cls)) {
                return true;
            }
        }
        return false;
    }

    private String readTemplate(String relativePath) throws IOException {
        Path path = TEMPLATES_DIR.resolve(relativePath);
        return Files.readString(path);
    }
}
