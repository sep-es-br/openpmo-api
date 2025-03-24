package br.gov.es.openpmo.dto.baselines;

import java.math.BigDecimal;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class BaselineConsumesStepSubmitDto {

    private Long idStep;
    private BigDecimal actualCost;
    private BigDecimal plannedCost;
    private Long idCostAccount;

    public Long getIdStep() {
        return idStep;
    }

    public void setIdStep(Long idStep) {
        this.idStep = idStep;
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
