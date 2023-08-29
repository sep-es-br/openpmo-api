package br.gov.es.openpmo.service.schedule;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class StepValueLinearAllocator implements StepValueAllocator {
  @Override
  public Map<Long, BigDecimal> execute(final long months, final BigDecimal decimal, final int scale) {
    BigDecimal count = BigDecimal.ZERO;
    final Map<Long, BigDecimal> results = new HashMap<>();
    if (Objects.isNull(decimal) || decimal.equals(BigDecimal.ZERO)) {
      for (long month = months; month > 0; month--) {
        results.put(month, BigDecimal.ZERO);
      }
      return results;
    }
    for (long month = months; month > 0; month--) {
      final BigDecimal result = decimal
        .subtract(count)
        .divide(
          new BigDecimal(month),
          scale,
          RoundingMode.HALF_UP
        );
      count = count.add(result);
      results.put(months - month + 1, result);
    }
    return results;
  }
}
