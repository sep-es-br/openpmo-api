package br.gov.es.openpmo.utils;

import java.math.BigDecimal;
import java.util.Map;

public final class MapPair<T, U> extends Pair<Map<T, U>, Map<T, U>> {

  private MapPair(
    final Map<T, U> first,
    final Map<T, U> second
  ) {
    super(first, second);
  }

  public static MapPair<Long, BigDecimal> of(
    final Map<Long, BigDecimal> actualCostParts,
    final Map<Long, BigDecimal> plannedCostParts
  ) {
    return new MapPair<>(actualCostParts, plannedCostParts);
  }

}
