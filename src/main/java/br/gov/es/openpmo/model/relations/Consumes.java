package br.gov.es.openpmo.model.relations;

import java.math.BigDecimal;
import java.util.Objects;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import br.gov.es.openpmo.model.CostAccount;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.Step;

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

	public Consumes(Long id, BigDecimal actualCost, BigDecimal plannedCost, CostAccount costAccount, Step step) {
		setId(id);
		this.actualCost = actualCost;
		this.plannedCost = plannedCost;
		this.costAccount = costAccount;
		this.step = step;
	}

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

	public CostAccount getCostAccount() {
		return costAccount;
	}

	public void setCostAccount(CostAccount costAccount) {
		this.costAccount = costAccount;
	}

	public Step getStep() {
		return step;
	}

	public void setStep(Step step) {
		this.step = step;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		Consumes consumes = (Consumes) o;
		return Objects.equals(costAccount, consumes.costAccount) && Objects.equals(step, consumes.step);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), costAccount, step);
	}
}
