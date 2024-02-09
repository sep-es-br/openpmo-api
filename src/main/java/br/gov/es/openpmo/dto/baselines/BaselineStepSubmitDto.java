package br.gov.es.openpmo.dto.baselines;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class BaselineStepSubmitDto {

    private Long idStep;
    private Long idSchedule;
    private BigDecimal actualWork;
    private BigDecimal plannedWork;
    private Long periodFromStart;
    private List<BaselineConsumesStepSubmitDto> consumes = new ArrayList<>(0);

    public Long getIdStep() {
        return idStep;
    }

    public void setIdStep(Long idStep) {
        this.idStep = idStep;
    }

    public Long getIdSchedule() {
        return idSchedule;
    }

    public void setIdSchedule(Long idSchedule) {
        this.idSchedule = idSchedule;
    }

    public BigDecimal getActualWork() {
        return actualWork;
    }

    public void setActualWork(BigDecimal actualWork) {
        this.actualWork = actualWork;
    }

    public BigDecimal getPlannedWork() {
        return plannedWork;
    }

    public void setPlannedWork(BigDecimal plannedWork) {
        this.plannedWork = plannedWork;
    }

    public Long getPeriodFromStart() {
        return periodFromStart;
    }

    public void setPeriodFromStart(Long periodFromStart) {
        this.periodFromStart = periodFromStart;
    }

    public List<BaselineConsumesStepSubmitDto> getConsumes() {
        return consumes;
    }

    public void setConsumes(List<BaselineConsumesStepSubmitDto> consumes) {
        this.consumes = consumes;
    }
}
