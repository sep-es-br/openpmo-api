package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@RelationshipEntity(type = "CONSUMES")
public class Consumes extends Entity {

  private BigDecimal actualCost;

  private BigDecimal plannedCost;

  @EndNode
  private CostAccount costAccount;

  @StartNode
  private Step step;

  public Consumes() {
  }

  public Consumes(
    final Long id,
    final BigDecimal actualCost,
    final BigDecimal plannedCost,
    final CostAccount costAccount,
    final Step step
  ) {
    this.setId(id);
    this.actualCost = actualCost;
    this.plannedCost = plannedCost;
    this.costAccount = costAccount;
    this.step = step;
  }

  public Consumes(final Consumes consumes, final CostAccount costAccount, final Step step) {
    this.actualCost = consumes.actualCost;
    this.plannedCost = consumes.plannedCost;
    this.costAccount = costAccount;
    this.step = step;
  }

  public CostAccount getCostAccount() {
    return this.costAccount;
  }

  public void setCostAccount(final CostAccount costAccount) {
    this.costAccount = costAccount;
  }

  public Step getStep() {
    return this.step;
  }

  public void setStep(final Step step) {
    this.step = step;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.costAccount, this.step);
  }

  @Override
  public boolean equals(final Object o) {
    if(this == o) {
      return true;
    }
    if(o == null || this.getClass() != o.getClass()) {
      return false;
    }
    if(!super.equals(o)) {
      return false;
    }
    final Consumes consumes = (Consumes) o;
    return Objects.equals(this.costAccount, consumes.costAccount) && Objects.equals(this.step, consumes.step);
  }

  @Transient
  public boolean hasPlannedCostChanges(final Consumes consumes) {
    return (this.getPlannedCost() != null || consumes.getPlannedCost() != null)
           && (this.getPlannedCost() != null && consumes.getPlannedCost() == null
               || this.getPlannedCost() == null && consumes.getPlannedCost() != null
               || !this.getPlannedCost().equals(consumes.getPlannedCost()));
  }

  public BigDecimal getPlannedCost() {
    return this.plannedCost;
  }

  public void setPlannedCost(final BigDecimal plannedCost) {
    this.plannedCost = plannedCost;
  }

  @Transient
  public boolean hasActualCostChanges(final Consumes consumes) {
    return (this.getActualCost() != null || consumes.getActualCost() != null)
           && (this.getActualCost() != null && consumes.getActualCost() == null
               || this.getActualCost() == null && consumes.getActualCost() != null
               || !this.getActualCost().equals(consumes.getActualCost()));
  }

  public BigDecimal getActualCost() {
    return this.actualCost;
  }

  public void setActualCost(final BigDecimal actualCost) {
    this.actualCost = actualCost;
  }

  public Long getIdCostAccount() {
    return Optional.ofNullable(this.costAccount)
      .map(CostAccount::getId)
      .orElse(null);
  }

  public Long getIdCostAccountMaster() {
    return Optional.ofNullable(this.costAccount)
      .map(CostAccount::getMaster)
      .map(IsCostAccountSnapshotOf::getMaster)
      .map(CostAccount::getId)
      .orElse(null);
  }
}
