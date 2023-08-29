package br.gov.es.openpmo.service.schedule;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
public class StepValueSigmoidalAllocator implements StepValueAllocator {

  @Override
  public Map<Long, BigDecimal> execute(final long months, final BigDecimal decimal, final int scale) {
    final Map<Long, BigDecimal> distributedValuesByMonth = new HashMap<>();
    if (Objects.isNull(decimal) || decimal.equals(BigDecimal.ZERO)) {
      for (long month = months; month > 0; month--) {
        distributedValuesByMonth.put(month, BigDecimal.ZERO);
      }
      return distributedValuesByMonth;
    }
    final Function<BigDecimal, BigDecimal> valueNormalizerFunction = value -> value.setScale(
      scale,
      RoundingMode.HALF_UP
    );
    final LinkedList<Long> indexes = new LinkedList<>();
    final long indexStep = (months - (months % 2)) / 2;
    for (long index = indexStep, counter = 0; counter < months; index--, counter++) {
      indexes.addFirst(index);
    }
    final Map<Long, BigDecimal> accumulatedSigmoidalValuesByMonth = new HashMap<>();
    for (int index = 0; index < months; index++) {
      final double value = isLastValue(months, index) ? 1 : sigmoidalFunction(indexes, index);
      accumulatedSigmoidalValuesByMonth.put(
        (long) index,
        valueNormalizerFunction.apply(BigDecimal.valueOf(value).multiply(decimal))
      );
    }
    for (long index = 0; index < months; index++) {
      if (index == 0) {
        distributedValuesByMonth.put(1L, accumulatedSigmoidalValuesByMonth.get(0L));
        continue;
      }
      final BigDecimal previousValue = accumulatedSigmoidalValuesByMonth.getOrDefault(index - 1, BigDecimal.ZERO);
      final BigDecimal currentValue = accumulatedSigmoidalValuesByMonth.getOrDefault(index, BigDecimal.ZERO);
      distributedValuesByMonth.put(index+1, valueNormalizerFunction.apply(currentValue.subtract(previousValue)));
    }
    return distributedValuesByMonth;
  }

  private static boolean isLastValue(final long months, final int index) {
    return index == months - 1;
  }

  private static double sigmoidalFunction(final LinkedList<Long> indexes, final int index) {
    return 1 / (1 + Math.exp(-indexes.get(index)));
  }
}
