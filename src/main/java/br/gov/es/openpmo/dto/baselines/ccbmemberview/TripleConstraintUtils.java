package br.gov.es.openpmo.dto.baselines.ccbmemberview;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

import static java.math.BigDecimal.valueOf;

public final class TripleConstraintUtils {

  static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
  static final BigDecimal ONE_MONTH = new BigDecimal(30);

  private TripleConstraintUtils() {
  }

  static BigDecimal daysBetween(final Temporal currentStartDate, final Temporal currentEndDate) {
    if(currentStartDate == null || currentEndDate == null) return null;
    return valueOf(ChronoUnit.DAYS.between(
      currentStartDate,
      currentEndDate
    ));
  }

  static BigDecimal roundOneDecimal(final BigDecimal number) {
    if(number == null) return null;
    return number.round(new MathContext(3, RoundingMode.HALF_EVEN));
  }

}
