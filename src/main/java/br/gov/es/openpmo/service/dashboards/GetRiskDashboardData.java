package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.model.risk.Importance;
import br.gov.es.openpmo.repository.RiskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static br.gov.es.openpmo.model.risk.Importance.HIGH;
import static br.gov.es.openpmo.model.risk.Importance.LOW;
import static br.gov.es.openpmo.model.risk.Importance.MEDIUM;

@Component
public class GetRiskDashboardData implements IGetRiskDashboardData {

  private final RiskRepository repository;

  @Autowired
  public GetRiskDashboardData(final RiskRepository repository) {
    this.repository = repository;
  }


  @Override public RiskDataChart get(final Long idWorkpack) {
    return new RiskDataChart(
      this.countTotalAllRisks(idWorkpack),
      this.countRisksByImportance(idWorkpack, HIGH),
      this.countRisksByImportance(idWorkpack, LOW),
      this.countRisksByImportance(idWorkpack, MEDIUM),
      this.countTotalClosedRisks(idWorkpack)
    );
  }

  private Long countRisksByImportance(final Long idWorkpack, final Importance importance) {
    return this.repository.countOpenedRiskOfWorkpackByImportance(idWorkpack, importance.name());
  }

  private Long countTotalClosedRisks(final Long idWorkpack) {
    return this.repository.countClosedRisksOfWorkpack(idWorkpack);
  }

  private Long countTotalAllRisks(final Long idWorkpack) {
    return this.repository.countAllRisksOfWorkpack(idWorkpack);
  }

}
