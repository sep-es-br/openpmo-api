package br.gov.es.openpmo.dto.dashboards.tripleconstraint;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@QueryResult
public class DateIntervalQuery {

  private final ZonedDateTime initialDate;

  private final ZonedDateTime endDate;

  public DateIntervalQuery(
    final ZonedDateTime initialDate,
    final ZonedDateTime endDate
  ) {
    this.initialDate = initialDate;
    this.endDate = endDate;
  }

  public List<YearMonth> toYearMonths() {
    if(!this.isValid()) {
      return new ArrayList<>();
    }

    return LongStream.range(0, this.numberOfMonths())
      .boxed()
      .map(this.getInitialDate()::plusMonths)
      .map(YearMonth::from)
      .collect(Collectors.toList());
  }

  private long numberOfMonths() {
    final Period period = Period.between(this.getInitialDate(), this.getEndDate());
    return period.toTotalMonths() + 1;
  }

  public LocalDate getInitialDate() {
    return Optional.ofNullable(this.initialDate).map(ZonedDateTime::toLocalDate).orElse(null);
  }

  public LocalDate getEndDate() {
    return Optional.ofNullable(this.endDate).map(ZonedDateTime::toLocalDate).orElse(null);
  }

  public boolean isValid() {
    return this.initialDate != null && this.endDate != null;
  }

}
