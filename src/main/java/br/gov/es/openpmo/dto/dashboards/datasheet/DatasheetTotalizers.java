package br.gov.es.openpmo.dto.dashboards.datasheet;

public class DatasheetTotalizers {

  private final Long projectsQuantity;

  private final Long deliverablesQuantity;

  private final Long milestoneQuantity;

  public DatasheetTotalizers(final Long projectsQuantity, final Long deliverablesQuantity, final Long milestoneQuantity) {
    this.projectsQuantity = projectsQuantity;
    this.deliverablesQuantity = deliverablesQuantity;
    this.milestoneQuantity = milestoneQuantity;
  }

  public Long getProjectsQuantity() {
    return this.projectsQuantity;
  }

  public Long getDeliverablesQuantity() {
    return this.deliverablesQuantity;
  }

  public Long getMilestoneQuantity() {
    return this.milestoneQuantity;
  }

}
