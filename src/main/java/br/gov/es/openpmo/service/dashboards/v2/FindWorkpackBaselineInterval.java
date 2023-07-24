package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.DateInterval;
import br.gov.es.openpmo.dto.dashboards.tripleconstraint.DateIntervalQuery;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.properties.Date;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.relations.IsBaselinedBy;
import br.gov.es.openpmo.model.relations.IsPropertySnapshotOf;
import br.gov.es.openpmo.model.relations.IsScheduleSnapshotOf;
import br.gov.es.openpmo.model.relations.IsSnapshotOf;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.IntervalRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FindWorkpackBaselineInterval {

  private final IntervalRepository intervalRepository;

  public FindWorkpackBaselineInterval(IntervalRepository intervalRepository) {
    this.intervalRepository = intervalRepository;
  }

  public Optional<DateIntervalQuery> execute(Long workpackId, List<Long> baselineIds) {
    final List<Baseline> baselines = this.intervalRepository.findBaselineByIds(baselineIds);
    if (baselines.isEmpty()) {
      return Optional.of(DateIntervalQuery.empty());
    }
    final DateInterval interval = getInterval(workpackId, baselines);
    return Optional.of(interval.toQuery());
  }

  private DateInterval getInterval(Long workpackId, Iterable<Baseline> baselines) {
    LocalDate initialDate = null;
    LocalDate endDate = null;
    for (Baseline baseline : baselines) {
      final IsBaselinedBy baselinedBy = baseline.getBaselinedBy();
      final Workpack project = baselinedBy.getWorkpack();
      final Workpack workpack = this.findWorkpackFromParent(project, workpackId);
      DateInterval interval;
      if (workpack == null) {
        interval = getInterval(project, baseline);
      } else {
        interval = getInterval(workpack, baseline);
      }
      if (!interval.isValid()) {
        continue;
      }
      if (initialDate == null || initialDate.isAfter(interval.getInitialDate())) {
        initialDate = interval.getInitialDate();
      }
      if (endDate == null || endDate.isBefore(interval.getEndDate())) {
        endDate = interval.getEndDate();
      }
    }
    return new DateInterval(initialDate, endDate);
  }

  private DateInterval getInterval(Workpack workpack, Baseline baseline) {
    LocalDate initialDate = null;
    LocalDate endDate = null;
    if (workpack instanceof Deliverable) {
      final Schedule schedule = workpack.getSchedule();
      if (schedule != null) {
        final Set<IsScheduleSnapshotOf> scheduleSnapshots = schedule.getSnapshots();
        if (scheduleSnapshots != null) {
          final Optional<Schedule> scheduleSnapshot = scheduleSnapshots.stream()
            .map(IsSnapshotOf::getSnapshot)
            .filter(snapshot -> snapshot.getBaseline() == baseline)
            .findFirst();
          if (scheduleSnapshot.isPresent()) {
            final Schedule snapshot = scheduleSnapshot.get();
            initialDate = snapshot.getStart();
            endDate = snapshot.getEnd();
          }
        }
      }
    }
    if (workpack instanceof Milestone) {
      final Set<Property> properties = workpack.getProperties();
      if (properties != null) {
        final List<Date> dates = properties.stream()
          .filter(Date.class::isInstance)
          .map(Date.class::cast)
          .collect(Collectors.toList());
        for (Date date : dates) {
          final Set<IsPropertySnapshotOf> dateSnapshots = date.getSnapshots();
          if (dateSnapshots == null) {
            continue;
          }
          final Optional<Property> dateSnapshot = dateSnapshots.stream()
            .map(IsSnapshotOf::getSnapshot)
            .filter(snapshot -> snapshot.getBaseline() == baseline)
            .findFirst();
          if (dateSnapshot.isPresent()) {
            final Date snapshot = (Date) dateSnapshot.get();
            final LocalDateTime value = snapshot.getValue();
            if (value == null) {
              continue;
            }
            final LocalDate localDate = value.toLocalDate();
            if (initialDate == null || initialDate.isAfter(localDate)) {
              initialDate = localDate;
            }
            if (endDate == null || endDate.isBefore(localDate)) {
              endDate = localDate;
            }
          }
        }
      }
    }
    final Set<Workpack> children = workpack.getChildren();
    if (children != null) {
      for (Workpack child : children) {
        final DateInterval interval = this.getInterval(child, baseline);
        if (!interval.isValid()) {
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

  private Workpack findWorkpackFromParent(Workpack workpack, Long toBeFound) {
    if (workpack == null) {
      return null;
    }
    final Set<Workpack> parents = workpack.getParent();
    if (parents == null) {
      return null;
    }
    for (Workpack parent : parents) {
      if (Objects.equals(parent.getId(), toBeFound)) {
        return parent;
      }
    }
    for (Workpack parent : parents) {
      final Workpack found = findWorkpackFromParent(parent, toBeFound);
      if (found != null && Objects.equals(found.getId(), toBeFound)) {
        return found;
      }
    }
    return null;
  }

}
