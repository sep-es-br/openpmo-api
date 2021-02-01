package br.gov.es.openpmo.dto.schedule;

import java.math.BigDecimal;

import br.gov.es.openpmo.dto.EntityDto;

public class ConsumesDto {

	private Long id;
	private BigDecimal actualCost;
	private BigDecimal plannedCost;
	private EntityDto costAccount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public EntityDto getCostAccount() {
		return costAccount;
	}

	public void setCostAccount(EntityDto costAccount) {
		this.costAccount = costAccount;
	}
}
