package br.gov.es.openpmo.service.indicators;

import br.gov.es.pmo.strategic_planning_interface.model.IndicatorDto;
import br.gov.es.pmo.strategic_planning_interface.model.MissionDto;
import br.gov.es.pmo.strategic_planning_interface.model.StrategicObjectiveDto;
import br.gov.es.pmo.strategic_planning_interface.model.TargetDto;
import br.gov.es.pmo.strategic_planning_interface.model.IStrategicPlanningProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class StrategicPlanningProviderService {

    private final ObjectProvider<IStrategicPlanningProvider> provider;

    public StrategicPlanningProviderService(final ObjectProvider<IStrategicPlanningProvider> provider) {
        this.provider = provider;
    }

    public List<MissionDto> getMissions() {
        final IStrategicPlanningProvider implementation = provider.getIfAvailable();
        return implementation == null ? Collections.emptyList() : implementation.getMissions();
    }

    public List<StrategicObjectiveDto> getObjectives() {
        final IStrategicPlanningProvider implementation = provider.getIfAvailable();
        return implementation == null ? Collections.emptyList() : implementation.getObjectives();
    }

    public List<TargetDto> getTargets() {
        final IStrategicPlanningProvider implementation = provider.getIfAvailable();
        return implementation == null ? Collections.emptyList() : implementation.getTargets();
    }

    public List<IndicatorDto> getIndicators() {
        final IStrategicPlanningProvider implementation = provider.getIfAvailable();
        return implementation == null ? Collections.emptyList() : implementation.getIndicators();
    }

    public boolean isAvailable() {
        return provider.getIfAvailable() != null;
    }
}
