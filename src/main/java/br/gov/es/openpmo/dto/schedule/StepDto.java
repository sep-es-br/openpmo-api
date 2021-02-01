package br.gov.es.openpmo.dto.schedule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public class StepDto {

    private Long id;
    private Long idSchedule;
    private BigDecimal actualWork;
    private BigDecimal plannedWork;
    private LocalDate periodFromStart;
    private Set<ConsumesDto> consumes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getPeriodFromStart() {
        return periodFromStart;
    }

    public void setPeriodFromStart(LocalDate periodFromStart) {
        this.periodFromStart = periodFromStart;
    }

    public Set<ConsumesDto> getConsumes() {
        return consumes;
    }

    public void setConsumes(Set<ConsumesDto> consumes) {
        this.consumes = consumes;
    }
}
