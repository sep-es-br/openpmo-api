package br.gov.es.openpmo.service.indicators;

import br.gov.es.pmo.indicator_interface.model.ChallengeDto;
import br.gov.es.pmo.indicator_interface.model.IIndicatorProvider;
import br.gov.es.pmo.indicator_interface.model.IndicatorDto;
import br.gov.es.pmo.indicator_interface.model.StrategicIndicatorDetailDto;
import br.gov.es.pmo.indicator_interface.model.StrategicIndicatorDto;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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

    public List<StrategicIndicatorDto> getStrategicIndicators() {
        final IIndicatorProvider indicatorProvider = this.provider.getIfAvailable();
        return indicatorProvider == null
            ? Collections.emptyList()
            : indicatorProvider.getStrategicIndicators();
    }

    public List<StrategicIndicatorDetailDto> getStrategicIndicatorDetails(final List<Integer> indicatorIds) {
        final IIndicatorProvider indicatorProvider = this.provider.getIfAvailable();
        if (indicatorProvider == null) {
            return Collections.emptyList();
        }
        return indicatorProvider.getStrategicIndicatorDetails(indicatorIds).stream()
            .map(detail -> {
                detail.setPolarity(normalizeStrategicPolarity(detail.getPolarity()));
                return detail;
            })
            .collect(Collectors.toList());
    }

    private static String normalizeStrategicPolarity(final String polarity) {
        if (polarity == null) {
            return null;
        }
        switch (polarity.trim().toLowerCase(Locale.ROOT)) {
            case "positiva":
            case "minimo":
                return "MINIMO";
            case "negativa":
            case "maximo":
                return "MAXIMO";
            default:
                return polarity;
        }
    }

    public boolean isAvailable() {
        return this.provider.getIfAvailable() != null;
    }
}
