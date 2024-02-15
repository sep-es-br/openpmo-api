package br.gov.es.openpmo.dto.dashboards;

public class DashboardMonthDto {

  private TripleConstraintDto tripleConstraint;
  private PerformanceIndexDto performanceIndex;

  public static DashboardMonthDto of(DashboardDto dashboardDto) {
    final DashboardMonthDto dashboardMonthDto = new DashboardMonthDto();
    dashboardMonthDto.setTripleConstraint(TripleConstraintDto.of(dashboardDto));
    dashboardMonthDto.setPerformanceIndex(PerformanceIndexDto.of(dashboardDto));
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
