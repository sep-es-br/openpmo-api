package br.gov.es.openpmo.dto.schedule;

import java.math.BigDecimal;
import java.util.Set;

public class StepDeliveryUpdateDto {

    private Integer stepDate;
    private BigDecimal actualWork;
    private Set<ConsumesParamDto> consumes;

    public StepDeliveryUpdateDto(Integer stepDate, BigDecimal actualWork, Set<ConsumesParamDto> consumes) {
        this.stepDate = stepDate;
        this.actualWork = actualWork;
        this.consumes = consumes;
    }

    public Integer getStepDate() {
        return stepDate;
    }
    public void setStepDate(Integer stepDate) {
        this.stepDate = stepDate;
    }
    public BigDecimal getActualWork() {
        return actualWork;
    }
    public void setActualWork(BigDecimal actualWork) {
        this.actualWork = actualWork;
    }
    public Set<ConsumesParamDto> getConsumes() {
        return consumes;
    }
    public void setConsumes(Set<ConsumesParamDto> consumes) {
        this.consumes = consumes;
    }

    
}
