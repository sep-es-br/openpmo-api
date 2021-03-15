package br.gov.es.openpmo.dto.schedule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import javax.validation.constraints.NotNull;

public class StepStoreParamDto {

    @NotNull
    private Long idSchedule;
    private BigDecimal actualWork;
    private BigDecimal plannedWork;
    @NotNull
    private Boolean endStep;
    private Set<ConsumesParamDto> consumes;

    @NotNull
    private LocalDate periodFromStart;

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

    public Boolean getEndStep() {
        return endStep;
    }

    public void setEndStep(Boolean endStep) {
        this.endStep = endStep;
    }

    public Set<ConsumesParamDto> getConsumes() {
        return consumes;
    }

    public void setConsumes(Set<ConsumesParamDto> consumes) {
        this.consumes = consumes;
    }

    public LocalDate getPeriodFromStart() {
        return periodFromStart;
    }

    public void setPeriodFromStart(LocalDate periodFromStart) {
        this.periodFromStart = periodFromStart;
    }
}
