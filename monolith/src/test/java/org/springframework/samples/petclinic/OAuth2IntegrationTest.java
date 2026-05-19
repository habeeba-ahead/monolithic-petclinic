package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to verify OAuth2/OpenID Connect configuration meets all acceptance criteria
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.security.oauth2.client.registration.google.client-id=test-client-id",
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret",
    "spring.security.oauth2.client.registration.google.scope=openid,profile,email",
    "spring.security.oauth2.client.provider.google.issuer-uri=https://accounts.google.com"
})
public class OAuth2IntegrationTest {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Test
    public void verifyOAuth2ClientConfigurationMeetsAcceptanceCriteria() {
        // Verify OAuth 2.0 client configuration is properly set up
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        
        assertThat(googleRegistration).isNotNull();
        
        // Verify Google Cloud Identity Platform integration
        assertThat(googleRegistration.getProviderDetails().getConfigurationMetadata().get("issuer"))
            .isEqualTo("https://accounts.google.com");
        
        // Verify OAuth2 client configuration
        assertThat(googleRegistration.getClientId()).isEqualTo("test-client-id");
        assertThat(googleRegistration.getClientSecret()).isEqualTo("test-client-secret");
        
        // Verify OpenID Connect scopes
        assertThat(googleRegistration.getScopes())
            .contains("openid", "profile", "email");
        
        // Verify redirect URI is properly configured
        assertThat(googleRegistration.getRedirectUriTemplate())
            .isEqualTo("{baseUrl}/login/oauth2/code/{registrationId}");
        
        // Verify authorization grant type
        assertThat(googleRegistration.getAuthorizationGrantType().getValue())
            .isEqualTo("authorization_code");
    }
}