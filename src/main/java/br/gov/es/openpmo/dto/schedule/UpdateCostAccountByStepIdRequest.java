package br.gov.es.openpmo.dto.schedule;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class UpdateCostAccountByStepIdRequest {

  @NotNull
  BigDecimal actualCost;

  @NotNull
  BigDecimal plannedCost;

  public BigDecimal getActualCost() {
    return actualCost;
  }

  public void setActualCost(BigDecimal actualCost) {
    this.actualCost = actualCost;
  }

  public BigDecimal getPlannedCost() {
    return plannedCost;
  }

  public void setPlannedCost(BigDecimal plannedCost) {
    this.plannedCost = plannedCost;
  }

}
