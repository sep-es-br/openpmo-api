package br.gov.es.openpmo.dto.dashboards;

import br.gov.es.openpmo.model.dashboards.DashboardMonth;
import br.gov.es.openpmo.model.dashboards.PerformanceIndexes;
import br.gov.es.openpmo.model.dashboards.TripleConstraint;

public class DashboardMonthDto {

  private TripleConstraintDto tripleConstraint;
  private PerformanceIndexDto performanceIndex;

  public static DashboardMonthDto of(DashboardMonth dashboardMonth, Long baselineId) {
    final DashboardMonthDto dashboardMonthDto = new DashboardMonthDto();
    final TripleConstraint tripleConstraint = dashboardMonth.getTripleConstraint(baselineId);
    if (tripleConstraint != null) {
      dashboardMonthDto.setTripleConstraint(TripleConstraintDto.of(tripleConstraint));
    }
    final PerformanceIndexes performanceIndexes = dashboardMonth.getPerformanceIndexes(baselineId);
    if (performanceIndexes != null) {
      dashboardMonthDto.setPerformanceIndex(PerformanceIndexDto.of(performanceIndexes));
    }
    return dashboardMonthDto;
  }

  public TripleConstraintDto getTripleConstraint() {
    return tripleConstraint;
  }

  public void setTripleConstraint(TripleConstraintDto tripleConstraint) {
    this.tripleConstraint = tripleConstraint;
  }

  public PerformanceIndexDto getPerformanceIndex() {
    return performanceIndex;
  }

  public void setPerformanceIndex(PerformanceIndexDto performanceIndex) {
    this.performanceIndex = performanceIndex;
  }
}
