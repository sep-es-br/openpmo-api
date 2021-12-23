package br.gov.es.openpmo.dto.schedule;

import br.gov.es.openpmo.dto.EntityDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.Optional;

public class ConsumesDto {

  private Long id;
  private BigDecimal actualCost;
  private BigDecimal baselinePlannedCost;
  private BigDecimal plannedCost;
  private EntityDto costAccount;

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

  public EntityDto getCostAccount() {
    return this.costAccount;
  }

  public void setCostAccount(final EntityDto costAccount) {
    this.costAccount = costAccount;
  }

  public BigDecimal getBaselinePlannedCost() {
    return this.baselinePlannedCost;
  }

  public void setBaselinePlannedCost(final BigDecimal baselinePlannedCost) {
    this.baselinePlannedCost = baselinePlannedCost;
  }

  @JsonIgnore
  public Long getIdCostAccount() {
    return Optional.ofNullable(this.costAccount)
      .map(EntityDto::getId)
      .orElse(null);
  }
}
