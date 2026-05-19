package org.springframework.samples.petclinic.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.security.oauth2.client.registration.google.client-id=test-client-id",
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret",
    "spring.security.oauth2.client.registration.google.scope=openid,profile,email",
    "spring.security.oauth2.client.provider.google.issuer-uri=https://accounts.google.com"
})
public class OAuth2ConfigurationTest {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    public void shouldConfigureGoogleOAuth2Client() {
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertThat(googleRegistration).isNotNull();
        assertThat(googleRegistration.getClientId()).isEqualTo("test-client-id");
        assertThat(googleRegistration.getClientSecret()).isEqualTo("test-client-secret");
        assertThat(googleRegistration.getScopes()).contains("openid", "profile", "email");
        assertThat(googleRegistration.getProviderDetails().getConfigurationMetadata().get("issuer"))
            .isEqualTo("https://accounts.google.com");
    }
}