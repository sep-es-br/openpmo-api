package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.repository.dashboards.DashboardMilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DashboardMilestoneService implements IDashboardMilestoneService {

  private final DashboardMilestoneRepository repository;

  @Autowired
  public DashboardMilestoneService(final DashboardMilestoneRepository repository) {
    this.repository = repository;
  }

  private static LocalDate getMinOfNowAnd(final YearMonth date) {
    final LocalDate now = LocalDate.now();

    if(date == null) {
      return now;
    }

    final LocalDate endOfMonth = date.atEndOfMonth();
    return now.isBefore(endOfMonth) ? now : endOfMonth;
  }

  @Override
  public MilestoneDataChart build(final DashboardParameters parameters) {
    final Long idBaseline = parameters.getBaselineId();
    final Long idWorkpack = parameters.getWorkpackId();
    final YearMonth yearMonth = parameters.getYearMonth();
    final LocalDate refDate = getMinOfNowAnd(yearMonth);
    return getMilestoneDataChart(idWorkpack, idBaseline, refDate);
  }

  @Override
  public MilestoneDataChart build(
    final Long worpackId,
    final YearMonth yearMonth
  ) {
    final LocalDate refDate = getMinOfNowAnd(yearMonth);
    return getMilestoneDataChart(worpackId, null, refDate);
  }

  private MilestoneDataChart getMilestoneDataChart(Long idWorkpack, Long idBaseline, LocalDate refDate) {
    final List<Milestone> milestones = this.repository.findByParentId(idWorkpack, idBaseline);
    Long concluded = 0L;
    Long lateConcluded = 0L;
    Long late = 0L;
    Long onTime = 0L;
    for (Milestone milestone : milestones) {
      if (milestone.isConcluded() && !milestone.isLateConcluded()) {
        concluded++;
        continue;
      }
      if (milestone.isLateConcluded()) {
        lateConcluded++;
        continue;
      }
      if (milestone.isLate(refDate)) {
        late++;
        continue;
      }
      if (milestone.isOnTime(refDate)) {
        onTime++;
      }
    }
    final Long quantity = concluded + lateConcluded + late + onTime;
    return new MilestoneDataChart(
      quantity,
      concluded,
      lateConcluded,
      late,
      onTime
    );
  }

  private Long getQuantity(
    final Long concluded,
    final Long lateConcluded,
    final Long late,
    final Long onTime
  ) {
    return Optional.ofNullable(concluded).orElse(0L) +
           Optional.ofNullable(lateConcluded).orElse(0L) +
           Optional.ofNullable(late).orElse(0L) +
           Optional.ofNullable(onTime).orElse(0L);
  }

  private Long getConcluded(
    final Long baselineId,
    final Long idWorkpack
  ) {
    final Set<Long> concluded = this.repository.concluded(idWorkpack);
    final Set<Long> lateConcluded = this.repository.lateConcluded(idWorkpack);
    concluded.removeAll(lateConcluded);

    final Set<Long> concludedBaseline = Optional.ofNullable(baselineId)
      .map(id -> this.repository.concluded(id, idWorkpack))
      .orElse(new HashSet<>());

    concluded.addAll(concludedBaseline);
    return (long) concluded.size();
  }

  public boolean isConcluded(
    final Long milestoneId,
    final Long parentId,
    final Long workpackId,
    final Long baselineId
  ) {
    final Set<Long> concluded = this.repository.concluded(parentId);
    final Set<Long> lateConcluded = this.repository.lateConcluded(baselineId, workpackId);
    concluded.removeAll(lateConcluded);
    if (concluded.contains(milestoneId)) {
      return true;
    }

    final Set<Long> concludedBaseline = Optional.ofNullable(baselineId)
      .map(id -> this.repository.concluded(id, workpackId))
      .orElse(new HashSet<>());

    return concludedBaseline.contains(milestoneId);
  }

  private Long getLateConcluded(
    final Long baselineId,
    final Long idWorkpack
  ) {
    final Set<Long> lateConcluded = Optional.ofNullable(baselineId)
      .map(id -> this.repository.lateConcluded(id, idWorkpack))
      .orElse(this.repository.lateConcluded(idWorkpack));

    return (long) lateConcluded.size();
  }

  public boolean isLateConcluded(
    final Long milestoneId,
    final Long workpackId,
    final Long baselineId
  ) {
    final Set<Long> lateConcluded = this.repository.lateConcluded(baselineId, workpackId);
    return lateConcluded.contains(milestoneId);
  }

  private Long getLate(
    final Long baselineId,
    final Long idWorkpack,
    final LocalDate refDate
  ) {
    if(refDate == null) {
      return null;
    }

    final Set<Long> late = this.repository.late(idWorkpack, refDate);

    final Set<Long> lateBaseline = Optional.ofNullable(baselineId)
      .map(id -> this.repository.late(id, idWorkpack, refDate))
      .orElse(new HashSet<>());

    late.addAll(lateBaseline);
    return (long) late.size();
  }

  public boolean isLate(
    final Long milestoneId,
    final Long parentId,
    final Long workpackId,
    final Long baselineId
  ) {
    final LocalDate refDate = LocalDate.now();

    final Set<Long> late = this.repository.late(parentId, refDate);
    if (late.contains(milestoneId)) {
      return true;
    }

    final Set<Long> lateBaseline = Optional.ofNullable(baselineId)
      .map(id -> this.repository.late(id, workpackId, refDate))
      .orElse(new HashSet<>());

    return lateBaseline.contains(milestoneId);
  }

  private Long getOnTime(
    final Long baselineId,
    final Long idWorkpack,
    final LocalDate refDate
  ) {
    if(refDate == null) {
      return null;
    }

    final Set<Long> onTime = this.repository.onTime(idWorkpack, refDate);

    final Set<Long> onTimeBaseline = Optional.ofNullable(baselineId)
      .map(id -> this.repository.onTime(id, idWorkpack, refDate))
      .orElse(new HashSet<>());

    onTime.addAll(onTimeBaseline);
    return (long) onTime.size();
  }

  public boolean isOnTime(
    final Long milestoneId,
    final Long parentId,
    final Long workpackId,
    final Long baselineId
  ) {
    final LocalDate refDate = LocalDate.now();

    final Set<Long> onTime = this.repository.onTime(parentId, refDate);
    if (onTime.contains(milestoneId)) {
      return true;
    }

    final Set<Long> onTimeBaseline = Optional.ofNullable(baselineId)
      .map(id -> this.repository.onTime(id, workpackId, refDate))
      .orElse(new HashSet<>());

    return onTimeBaseline.contains(milestoneId);
  }

}
