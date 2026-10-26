package br.gov.es.openpmo.service.strategicplanning;

import br.gov.es.pmo.strategic_planning_interface.model.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class StrategicPlanningProviderService {

    private final ObjectProvider<IStrategicPlanningProvider> provider;

    public StrategicPlanningProviderService(
            ObjectProvider<IStrategicPlanningProvider> provider) {
        this.provider = provider;
    }

    public List<MissionDto> getMissions() {
        IStrategicPlanningProvider plugin = provider.getIfAvailable();
        return plugin == null ? Collections.emptyList() : plugin.getMissions();
    }

    public List<StrategicObjectiveDto> getObjectives() {
        IStrategicPlanningProvider plugin = provider.getIfAvailable();
        return plugin == null ? Collections.emptyList() : plugin.getObjectives();
    }

    public List<TargetDto> getTargets() {
        IStrategicPlanningProvider plugin = provider.getIfAvailable();
        return plugin == null ? Collections.emptyList() : plugin.getTargets();
    }

    public List<IndicatorDto> getIndicators() {
        IStrategicPlanningProvider plugin = provider.getIfAvailable();
        return plugin == null ? Collections.emptyList() : plugin.getIndicators();
    }

    public boolean isAvailable() {
        return provider.getIfAvailable() != null;
    }
}