package br.gov.es.openpmo.dto.schedule;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.costaccount.CostAccountEntityDto;
import br.gov.es.openpmo.model.relations.Consumes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class ConsumesCostDto {

  private Long id;
  private BigDecimal actualCost;
  private BigDecimal plannedCost;
  private CostAccountEntityDto costAccount;

  @JsonIgnore
  private Long stepId;

  @JsonIgnore
  private Long costAccountId;

  public ConsumesCostDto() {
  }

  public ConsumesCostDto(Consumes consumes) {
    this.id = consumes.getId();
    this.actualCost = consumes.getActualCost();
    this.plannedCost = consumes.getPlannedCost();
    if (consumes.getCostAccount() != null) {
      this.costAccount = new CostAccountEntityDto(consumes.getCostAccount().getId());
    }
  }

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

  public CostAccountEntityDto getCostAccount() {
    return this.costAccount;
  }

  public void setCostAccount(final CostAccountEntityDto costAccount) {
    this.costAccount = costAccount;
  }

  public Long getStepId() {
    return stepId;
}

  public void setStepId(Long stepId) {
    this.stepId = stepId;
  }

  public Long getCostAccountId() {
    return costAccountId;
  }

  public void setCostAccountId(Long costAccountId) {
    this.costAccountId = costAccountId;
  }

  @JsonIgnore
  public Long getIdCostAccount() {
    return Optional.ofNullable(this.costAccount)
      .map(CostAccountEntityDto::getId)
      .orElse(null);
  }

}
