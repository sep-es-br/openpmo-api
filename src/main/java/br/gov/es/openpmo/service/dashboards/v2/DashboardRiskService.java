package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.RiskDataChart;
import br.gov.es.openpmo.dto.dashboards.RiskDataChartDto;
import br.gov.es.openpmo.model.risk.Importance;
import br.gov.es.openpmo.model.risk.StatusOfRisk;
import br.gov.es.openpmo.repository.RiskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.gov.es.openpmo.model.risk.Importance.HIGH;
import static br.gov.es.openpmo.model.risk.Importance.LOW;
import static br.gov.es.openpmo.model.risk.Importance.MEDIUM;

@Service
public class DashboardRiskService implements IDashboardRiskService {

  private final RiskRepository repository;

  @Autowired
  public DashboardRiskService(final RiskRepository repository) {
    this.repository = repository;
  }

  @Override
  public RiskDataChart build(final DashboardParameters parameters) {
    final Long workpackId = parameters.getWorkpackId();
    return this.build(workpackId);
  }

  @Override
  public RiskDataChart build(final Long workpackId) {
    List<RiskDataChartDto> counts = this.repository.countRisksOfWorkpack(workpackId);
    Long totalOpenedRisks = this.countTotalOpenedRisks(counts);
    Long totalByImportanceHigh = this.countRisksByImportance(counts, HIGH);
    Long totalByImportanceLow = this.countRisksByImportance(counts, LOW);
    Long totalByImportanceMedium = this.countRisksByImportance(counts, MEDIUM);
    Long totalClosedRisks = this.countTotalClosedRisks(counts);

    return new RiskDataChart(
      totalOpenedRisks,
      totalByImportanceHigh,
      totalByImportanceLow,
      totalByImportanceMedium,
      totalClosedRisks
    );
  }

  private Long countTotalOpenedRisks(List<RiskDataChartDto> counts) {
    return counts.stream()
            .filter(item -> item.getStatus().toLowerCase().equals(StatusOfRisk.OPEN.getStatus()))
            .map(RiskDataChartDto::getCount)
            .reduce(0L, Long::sum);
  }

  private Long countRisksByImportance(
          List<RiskDataChartDto> counts,
          final Importance importance
  ) {
    return counts.stream()
            .filter(item -> item.getStatus().toLowerCase().equals(StatusOfRisk.OPEN.getStatus()) &&
                    item.getImportance().toLowerCase().equals(importance.getImportance()))
            .map(RiskDataChartDto::getCount)
            .reduce(0L, (subtotal, item) -> subtotal + item);
  }

  private Long countTotalClosedRisks(List<RiskDataChartDto> counts) {
    return counts.stream()
            .filter(item -> !item.getStatus().toLowerCase().equals(StatusOfRisk.OPEN.getStatus()))
            .map(RiskDataChartDto::getCount)
            .reduce(0L, (subtotal, item) -> subtotal + item);
  }
}
