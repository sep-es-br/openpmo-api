package br.gov.es.openpmo.controller.indicators;

import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.indicators.IndicatorProviderService;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IndicatorControllerAuthorizationTest {

    @Test
    public void invalidAuthorizationDoesNotQueryEitherProviderEndpoint() {
        final TokenService tokenService = mock(TokenService.class);
        final IndicatorProviderService providerService = mock(IndicatorProviderService.class);
        when(tokenService.getUserId(anyString()))
            .thenThrow(new IllegalArgumentException("Invalid token"));

        final IndicatorController controller = new IndicatorController(
            null,
            providerService,
            tokenService,
            null,
            null
        );

        assertRejected(() -> controller.getOds("Bearer invalid"));
        assertRejected(() -> controller.getChallenges("Bearer invalid"));
        verify(providerService, never()).getIndicators();
        verify(providerService, never()).getChallenges();
    }

    private static void assertRejected(final Runnable request) {
        try {
            request.run();
            fail("Expected invalid authorization to be rejected");
        } catch (final IllegalArgumentException expected) {
            // The provider must not be reached after token validation fails.
        }
    }
}
