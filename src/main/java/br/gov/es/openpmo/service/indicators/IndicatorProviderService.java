package br.gov.es.openpmo.service.indicators;

import br.gov.es.pmo.indicator_interface.model.ChallengeDto;
import br.gov.es.pmo.indicator_interface.model.IIndicatorProvider;
import br.gov.es.pmo.indicator_interface.model.IndicatorDto;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class IndicatorProviderService {

    private final ObjectProvider<IIndicatorProvider> provider;

    public IndicatorProviderService(final ObjectProvider<IIndicatorProvider> provider) {
        this.provider = provider;
    }

    public List<IndicatorDto> getIndicators() {
        final IIndicatorProvider indicatorProvider = this.provider.getIfAvailable();
        return indicatorProvider == null
            ? Collections.emptyList()
            : indicatorProvider.getIndicators();
    }

    public List<ChallengeDto> getChallenges() {
        final IIndicatorProvider indicatorProvider = this.provider.getIfAvailable();
        return indicatorProvider == null
            ? Collections.emptyList()
            : indicatorProvider.getChallenges();
    }

    public boolean isAvailable() {
        return this.provider.getIfAvailable() != null;
    }
}
