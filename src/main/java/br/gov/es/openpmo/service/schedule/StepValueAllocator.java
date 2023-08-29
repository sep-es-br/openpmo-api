package br.gov.es.openpmo.service.schedule;

import java.math.BigDecimal;
import java.util.Map;

@FunctionalInterface
public interface StepValueAllocator {

  Map<Long, BigDecimal> execute(long months, BigDecimal decimal, int scale);

}
