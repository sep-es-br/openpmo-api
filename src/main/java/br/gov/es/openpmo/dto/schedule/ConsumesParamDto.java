package br.gov.es.openpmo.dto.schedule;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ConsumesParamDto {

  private Long id;
  private BigDecimal actualCost;

  private BigDecimal plannedCost;
  @NotNull
  private Long idCostAccount;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public BigDecimal getActualCost() {
    return this.actualCost;
  }

  public void setActualCost(final BigDecimal actualCost) {
    this.actualCost = actualCost;
  }

  public BigDecimal getPlannedCost() {
    return this.plannedCost;
  }

  public void setPlannedCost(final BigDecimal plannedCost) {
    this.plannedCost = plannedCost;
  }

  public Long getIdCostAccount() {
    return this.idCostAccount;
  }

  public void setIdCostAccount(final Long idCostAccount) {
    this.idCostAccount = idCostAccount;
  }

}
