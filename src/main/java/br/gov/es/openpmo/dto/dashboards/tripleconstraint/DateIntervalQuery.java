package br.gov.es.openpmo.dto.dashboards.tripleconstraint;

import br.gov.es.openpmo.model.dashboards.DashboardMonth;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

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

  public static DateIntervalQuery empty() {
    return new DateIntervalQuery(null, null);
  }

  public static DateIntervalQuery of(Collection<DashboardMonth> months) {
    if (months == null || months.isEmpty()) {
      return empty();
    }
    final Set<ZonedDateTime> zonedDateTimes = months.stream()
      .map(month -> ZonedDateTime.from(month.getDate().atStartOfDay(ZoneId.systemDefault())))
      .collect(Collectors.toSet());
    return new DateIntervalQuery(
      zonedDateTimes.stream().min(Comparator.naturalOrder()).orElse(null),
      zonedDateTimes.stream().max(Comparator.naturalOrder()).orElse(null)
    );
  }

  public List<DashboardMonth> toDashboardMonths() {
    if (!this.isValid()) {
      return null;
    }
    final List<DashboardMonth> dashboardMonths = new ArrayList<>();
    final LocalDate localDate = this.getInitialDate();
    final long numberOfMonths = this.numberOfMonths();
    for (long m = 0; m < numberOfMonths; m++) {
      DashboardMonth month = new DashboardMonth(localDate.plusMonths(m));
      dashboardMonths.add(month);
    }
    return dashboardMonths;
  }

  public List<YearMonth> toYearMonths() {
    if (!this.isValid()) {
      return new ArrayList<>();
    }
    final List<YearMonth> yearMonths = new ArrayList<>();
    final LocalDate localDate = this.getInitialDate();
    final long numberOfMonths = this.numberOfMonths();
    for (long m = 0; m < numberOfMonths; m++) {
      final YearMonth yearMonth = YearMonth.from(localDate.plusMonths(m));
      yearMonths.add(yearMonth);
    }
    return yearMonths;
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
