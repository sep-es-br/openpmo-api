package br.gov.es.openpmo.dto.schedule;

import br.gov.es.openpmo.service.schedule.CostAccountValueAllocatorParameter;
import net.minidev.json.annotate.JsonIgnore;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

public class CostSchedule implements CostAccountValueAllocatorParameter {

  private Long id;
  @Min(value = 0)
  private BigDecimal plannedCost;
  @Min(value = 0)
  private BigDecimal actualCost;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public BigDecimal getPlannedCost() {
    return this.plannedCost;
  }

  public void setPlannedCost(final BigDecimal plannedCost) {
    this.plannedCost = plannedCost;
  }

  public BigDecimal getActualCost() {
    return this.actualCost;
  }

  public void setActualCost(final BigDecimal actualCost) {
    this.actualCost = actualCost;
  }

  @Override
  @JsonIgnore
  public Long getIdCostAccount() {
    return this.id;
  }

}
