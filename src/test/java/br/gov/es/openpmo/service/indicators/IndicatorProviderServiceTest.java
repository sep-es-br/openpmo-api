package br.gov.es.openpmo.service.indicators;

import br.gov.es.pmo.indicator_interface.model.ChallengeDto;
import br.gov.es.pmo.indicator_interface.model.IIndicatorProvider;
import br.gov.es.pmo.indicator_interface.model.IndicatorDto;
import org.junit.Test;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IndicatorProviderServiceTest {

    @Test
    public void returnsEmptyListsWhenPluginIsUnavailable() {
        final ObjectProvider<IIndicatorProvider> provider = mock(ObjectProvider.class);
        final IndicatorProviderService service = new IndicatorProviderService(provider);

        assertFalse(service.isAvailable());
        assertTrue(service.getIndicators().isEmpty());
        assertTrue(service.getChallenges().isEmpty());
    }

    @Test
    public void forwardsBothListsWithoutChangingTheirOrderOrRows() {
        final IIndicatorProvider plugin = mock(IIndicatorProvider.class);
        final ObjectProvider<IIndicatorProvider> provider = mock(ObjectProvider.class);
        final IndicatorDto ods = new IndicatorDto(1, "Descrição", "Nome");
        final ChallengeDto challenge = new ChallengeDto();
        challenge.setChallengeId(7);
        when(provider.getIfAvailable()).thenReturn(plugin);
        when(plugin.getIndicators()).thenReturn(Arrays.asList(ods));
        when(plugin.getChallenges()).thenReturn(Arrays.asList(challenge, challenge));

        final IndicatorProviderService service = new IndicatorProviderService(provider);

        assertTrue(service.isAvailable());
        assertEquals(Arrays.asList(ods), service.getIndicators());
        assertEquals(Arrays.asList(challenge, challenge), service.getChallenges());
    }
}
