package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.model.risk.Importance;
import br.gov.es.openpmo.repository.RiskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.gov.es.openpmo.model.risk.Importance.*;

@Service
public class DashboardRiskService implements IDashboardRiskService {

    private final RiskRepository repository;

    @Autowired
    public DashboardRiskService(final RiskRepository repository) {
        this.repository = repository;
    }

    @Override
    public RiskDataChart build(DashboardParameters parameters) {
        final Long workpackId = parameters.getWorkpackId();
        return this.build(workpackId);
    }

    @Override
    public RiskDataChart build(Long workpackId) {
        return new RiskDataChart(
                this.countTotalAllRisks(workpackId),
                this.countRisksByImportance(workpackId, HIGH),
                this.countRisksByImportance(workpackId, LOW),
                this.countRisksByImportance(workpackId, MEDIUM),
                this.countTotalClosedRisks(workpackId)
        );
    }

    private Long countTotalAllRisks(final Long workpackId) {
        return this.repository.countAllRisksOfWorkpack(workpackId);
    }

    private Long countRisksByImportance(final Long workpackId, final Importance importance) {
        return this.repository.countOpenedRiskOfWorkpackByImportance(workpackId, importance.name());
    }

    private Long countTotalClosedRisks(final Long workpacKId) {
        return this.repository.countClosedRisksOfWorkpack(workpacKId);
    }

}
