package br.gov.es.openpmo.dto.workpackmodel.params;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Set;

public class DashboardConfiguration {

  private final Boolean dashboardShowRisks;
  private final Boolean dashboardShowEva;
  private final Boolean dashboardShowMilestones;
  private final Set<String> dashboardShowStakeholders;

  @JsonCreator
  public DashboardConfiguration(
    final Boolean dashboardShowRisks,
    final Boolean dashboardShowEva,
    final Boolean dashboardShowMilestones,
    final Set<String> dashboardShowStakeholders
  ) {
    this.dashboardShowRisks = dashboardShowRisks;
    this.dashboardShowEva = dashboardShowEva;
    this.dashboardShowMilestones = dashboardShowMilestones;
    this.dashboardShowStakeholders = dashboardShowStakeholders;
  }

  public Boolean getDashboardShowRisks() {
    return this.dashboardShowRisks;
  }

  public Boolean getDashboardShowEva() {
    return this.dashboardShowEva;
  }

  public Boolean getDashboardShowMilestones() {
    return this.dashboardShowMilestones;
  }

  public Set<String> getDashboardShowStakeholders() {
    return this.dashboardShowStakeholders;
  }
}
