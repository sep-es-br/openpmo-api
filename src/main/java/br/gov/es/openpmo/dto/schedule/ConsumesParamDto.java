package br.gov.es.openpmo.dto.schedule;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;

public class ConsumesParamDto {

	private Long id;
	private BigDecimal actualCost;
	private BigDecimal plannedCost;
	@NotNull
	private Long idCostAccount;

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

	public Long getIdCostAccount() {
		return idCostAccount;
	}

	public void setIdCostAccount(Long idCostAccount) {
		this.idCostAccount = idCostAccount;
	}
}
