package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.DateInterval;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.DateIntervalQuery;
import br.gov.es.openpmo.model.properties.Date;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.IntervalRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FindWorkpackInterval {

  private final IntervalRepository intervalRepository;

  public FindWorkpackInterval(IntervalRepository intervalRepository) {
    this.intervalRepository = intervalRepository;
  }

  public Optional<DateIntervalQuery> execute(Long workpackId) {
    final Workpack workpack = this.intervalRepository.findWorkpackById(workpackId).orElse(null);
    final DateInterval interval = getInterval(workpack);
    return Optional.ofNullable(interval).map(DateInterval::toQuery);
  }

  private DateInterval getInterval(Workpack workpack) {
    if (workpack == null) {
      return null;
    }
    LocalDate initialDate = null;
    LocalDate endDate = null;
    if (workpack instanceof Deliverable) {
      final Schedule schedule = workpack.getSchedule();
      if (schedule != null) {
        final LocalDate start = schedule.getStart();
        final LocalDate end = schedule.getEnd();
        initialDate = start;
        endDate = end;
      }
    }
    if (workpack instanceof Milestone) {
      final LocalDateTime value = workpack.getDate();
      if (value != null) {
        final LocalDate localDate = value.toLocalDate();
        initialDate = localDate;
        endDate = localDate;
      }
    }
    final Set<Workpack> children = workpack.getChildren();
    if (children != null) {
      for (Workpack child : children) {
        final DateInterval interval = this.getInterval(child);
        if (interval == null || !interval.isValid()) {
          continue;
        }
        if (initialDate == null || initialDate.isAfter(interval.getInitialDate())) {
          initialDate = interval.getInitialDate();
        }
        if (endDate == null || endDate.isBefore(interval.getEndDate())) {
          endDate = interval.getEndDate();
        }
      }
    }
    return new DateInterval(initialDate, endDate);
  }

}
