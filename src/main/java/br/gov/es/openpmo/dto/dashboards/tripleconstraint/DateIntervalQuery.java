package br.gov.es.openpmo.dto.dashboards.tripleconstraint;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@QueryResult
public class DateIntervalQuery {

  private final ZonedDateTime initialDate;

  private final ZonedDateTime endDate;

  public DateIntervalQuery(final ZonedDateTime initialDate, final ZonedDateTime endDate) {
    this.initialDate = initialDate;
    this.endDate = endDate;
  }

  public List<YearMonth> toYearMonths() {
    final List<YearMonth> result = new ArrayList<>();

    for (int i = 0; i < this.numberOfMonths(); i++) {
      final LocalDate date = this.getInitialDate().plusMonths(i);
      final YearMonth yearMonth = YearMonth.from(date);
      result.add(yearMonth);
    }

    return result;
  }

  private long numberOfMonths() {
    final Period period = Period.between(this.getInitialDate(), this.getEndDate());
    return period.toTotalMonths() + 1;
  }

  public LocalDate getInitialDate() {
    final LocalDate date = this.initialDate.toLocalDate();
    return date.withDayOfMonth(1);
  }

  public LocalDate getEndDate() {
    final LocalDate date = this.endDate.toLocalDate();
    return date.withDayOfMonth(1);
  }

}
