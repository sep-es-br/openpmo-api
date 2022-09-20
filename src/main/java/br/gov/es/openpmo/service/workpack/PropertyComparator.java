package br.gov.es.openpmo.service.workpack;

import java.math.BigDecimal;
import java.text.Collator;
import java.time.LocalDateTime;

public final class PropertyComparator {

  private PropertyComparator() {
  }

  public static int compare(
    final Object a,
    final Object b
  ) {
    if(a instanceof String && b instanceof String) {
      final Collator collator = Collator.getInstance();
      collator.setStrength(Collator.PRIMARY);
      return collator.compare((String) a, (String) b);
    }
    if(a instanceof LocalDateTime && b instanceof LocalDateTime) {
      return ((LocalDateTime) a).compareTo((LocalDateTime) b);
    }
    if(a instanceof BigDecimal && b instanceof BigDecimal) {
      return ((BigDecimal) a).compareTo((BigDecimal) b);
    }
    if(a instanceof java.lang.Integer && b instanceof java.lang.Integer) {
      return ((java.lang.Integer) a).compareTo((java.lang.Integer) b);
    }
    if(a instanceof Long && b instanceof Long) {
      return ((Long) a).compareTo((Long) b);
    }
    if(a instanceof Boolean && b instanceof Boolean) {
      return ((Boolean) a).compareTo((Boolean) b);
    }
    if(a instanceof Double && b instanceof Double) {
      return ((Double) a).compareTo((Double) b);
    }
    return -1;
  }

}
