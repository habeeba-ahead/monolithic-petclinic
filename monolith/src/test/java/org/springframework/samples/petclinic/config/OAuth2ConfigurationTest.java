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
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret"
})
class OAuth2ConfigurationTest {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    void shouldConfigureGoogleOAuth2Client() {
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertThat(googleRegistration).isNotNull();
        assertThat(googleRegistration.getClientId()).isEqualTo("test-client-id");
        assertThat(googleRegistration.getClientSecret()).isEqualTo("test-client-secret");
        assertThat(googleRegistration.getScopes()).contains("openid", "profile", "email");
    }

    @Test
    void shouldConfigureGoogleCloudIdentityProvider() {
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertThat(googleRegistration.getProviderDetails().getAuthorizationUri())
            .isEqualTo("https://accounts.google.com/o/oauth2/v2/auth");
        assertThat(googleRegistration.getProviderDetails().getTokenUri())
            .isEqualTo("https://oauth2.googleapis.com/token");
        assertThat(googleRegistration.getProviderDetails().getUserInfoEndpoint().getUri())
            .isEqualTo("https://www.googleapis.com/oauth2/v3/userinfo");
        assertThat(googleRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName())
            .isEqualTo("sub");
    }
}