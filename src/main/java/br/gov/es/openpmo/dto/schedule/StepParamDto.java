package br.gov.es.openpmo.dto.schedule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import javax.validation.constraints.NotNull;

public class StepParamDto {

    private Long id;
    private BigDecimal actualWork;
    private BigDecimal plannedWork;
    private LocalDate periodFromStart;
    private Set<ConsumesParamDto> consumes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
