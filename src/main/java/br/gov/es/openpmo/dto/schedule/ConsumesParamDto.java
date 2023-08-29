package br.gov.es.openpmo.dto.schedule;

import br.gov.es.openpmo.service.schedule.CostAccountValueAllocatorParameter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ConsumesParamDto implements CostAccountValueAllocatorParameter {

  private Long id;
  @Min(value = 0)
  private BigDecimal actualCost;
  @Min(value = 0)
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
