package br.gov.es.openpmo.dto.baselines.ccbmemberview;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Optional;

public final class TripleConstraintUtils {

    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    public static final BigDecimal ONE_MONTH = new BigDecimal(30);

    private TripleConstraintUtils() {
    }

    public static BigDecimal daysBetween(final Temporal first, final Temporal second) {
        if (first == null || second == null) {
            return null;
        }
        return BigDecimal.valueOf(ChronoUnit.DAYS.between(first, second));
    }

    public static BigDecimal roundOneDecimal(final BigDecimal number) {
        return Optional.ofNullable(number)
                .map(bigDecimal -> bigDecimal.round(new MathContext(3, RoundingMode.HALF_EVEN)))
                .orElse(null);
    }

}
