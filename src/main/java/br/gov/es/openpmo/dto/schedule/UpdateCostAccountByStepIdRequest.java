package br.gov.es.openpmo.dto.schedule;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class UpdateCostAccountByStepIdRequest {

  @NotNull
  @Min(value = 0)
  BigDecimal actualCost;

  @NotNull
  @Min(value = 0)
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
