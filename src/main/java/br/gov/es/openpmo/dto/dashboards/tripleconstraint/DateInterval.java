package br.gov.es.openpmo.dto.dashboards.tripleconstraint;

import java.time.LocalDate;
import java.time.ZoneId;

public class DateInterval {

  private final LocalDate initialDate;

  private final LocalDate endDate;

  public DateInterval(
    final LocalDate initialDate,
    final LocalDate endDate
  ) {
    this.initialDate = initialDate;
    this.endDate = endDate;
  }

  public DateIntervalQuery toQuery() {
    if (!this.isValid()) {
      return DateIntervalQuery.empty();
    }
    return new DateIntervalQuery(
      this.initialDate.atStartOfDay(ZoneId.systemDefault()),
      this.endDate.atStartOfDay(ZoneId.systemDefault())
    );
  }

  public LocalDate getInitialDate() {
    return this.initialDate;
  }

  public LocalDate getEndDate() {
    return this.endDate;
  }

  public boolean isValid() {
    return this.initialDate != null && this.endDate != null;
  }

}
