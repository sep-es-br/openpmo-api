package br.gov.es.openpmo.dto.baselines;

import java.math.BigDecimal;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class BaselineConsumesStep {
    private Long idWorkpack;
    private Long idStep;
    private Long idConsumes;
    private BigDecimal plannedCost;
    private Long idMaster;

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public Long getIdStep() {
        return idStep;
    }

    public void setIdStep(Long idStep) {
        this.idStep = idStep;
    }

    public Long getIdConsumes() {
        return idConsumes;
    }

    public void setIdConsumes(Long idConsumes) {
        this.idConsumes = idConsumes;
    }

    public BigDecimal getPlannedCost() {
        return plannedCost;
    }

    public void setPlannedCost(BigDecimal plannedCost) {
        this.plannedCost = plannedCost;
    }

    public Long getIdMaster() {
        return idMaster;
    }

    public void setIdMaster(Long idMaster) {
        this.idMaster = idMaster;
    }
}
