package br.gov.es.openpmo.service.schedule;

import java.math.BigDecimal;

public interface CostAccountValueAllocatorParameter {

  BigDecimal getPlannedCost();

  BigDecimal getActualCost();

  Long getIdCostAccount();

}
