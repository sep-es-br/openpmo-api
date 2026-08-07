package br.gov.es.openpmo.dto.workpack.breakdown.structure;

import java.util.List;

public class PlanBreakdownStructure {

  private Long idPlan;

  private String planName;

  private List<WorkpackModelBreakdownStructure> workpackModels;

  public Long getIdPlan() {
    return this.idPlan;
  }

  public void setIdPlan(final Long idPlan) {
    this.idPlan = idPlan;
  }

  public String getPlanName() {
    return this.planName;
  }

  public void setPlanName(final String planName) {
    this.planName = planName;
  }

  public List<WorkpackModelBreakdownStructure> getWorkpackModels() {
    return this.workpackModels;
  }

  public void setWorkpackModels(final List<WorkpackModelBreakdownStructure> workpackModels) {
    this.workpackModels = workpackModels;
  }
}
