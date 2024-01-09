package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.MilestoneDataChart;
import br.gov.es.openpmo.repository.dashboards.DashboardMilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
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

    final Long concluded = this.getConcluded(idBaseline, idWorkpack);
    final Long lateConcluded = this.getLateConcluded(idBaseline, idWorkpack);
    final Long late = this.getLate(idBaseline, idWorkpack, refDate);
    final Long onTime = this.getOnTime(idBaseline, idWorkpack, refDate);
    final Long quantity = this.getQuantity(concluded, lateConcluded, late, onTime);

    return new MilestoneDataChart(
      quantity,
      concluded,
      lateConcluded,
      late,
      onTime
    );
  }

  @Override
  public MilestoneDataChart build(
    final Long worpackId,
    final YearMonth yearMonth
  ) {
    final LocalDate refDate = getMinOfNowAnd(yearMonth);

    final Long concluded = this.getConcluded(null, worpackId);
    final Long lateConcluded = this.getLateConcluded(null, worpackId);
    final Long late = this.getLate(null, worpackId, refDate);
    final Long onTime = this.getOnTime(null, worpackId, refDate);
    final Long quantity = this.getQuantity(concluded, lateConcluded, late, onTime);

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
    final Set<Long> concluded = Optional.ofNullable(baselineId)
      .map(blId -> this.repository.concluded(blId, idWorkpack))
      .orElse(this.repository.concluded(idWorkpack));
    return (long) concluded.size();
  }

  public boolean isConcluded(
    final Long milestoneId,
    final Long parentId,
    final Long workpackId,
    final Long baselineId
  ) {

    final Set<Long> concluded = Optional.ofNullable(baselineId)
      .map(id -> this.repository.concluded(id, workpackId))
      .orElse(this.repository.concluded(parentId));

    return concluded.contains(milestoneId);
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

    final Set<Long> late = Optional.ofNullable(baselineId)
      .map(id -> this.repository.late(id, idWorkpack, refDate))
      .orElse(this.repository.late(idWorkpack, refDate));
    return (long) late.size();
  }

  public boolean isLate(
    final Long milestoneId,
    final Long parentId,
    final Long workpackId,
    final Long baselineId
  ) {
    final LocalDate refDate = LocalDate.now();

    final Set<Long> late = Optional.ofNullable(baselineId)
      .map(id -> this.repository.late(id, workpackId, refDate))
      .orElse(this.repository.late(parentId, refDate));

    return late.contains(milestoneId);
  }

  private Long getOnTime(
    final Long baselineId,
    final Long idWorkpack,
    final LocalDate refDate
  ) {
    if(refDate == null) {
      return null;
    }

    final Set<Long> onTime = Optional.ofNullable(baselineId)
      .map(id -> this.repository.onTime(id, idWorkpack, refDate))
      .orElse(this.repository.onTime(idWorkpack, refDate));

    return (long) onTime.size();
  }

  public boolean isOnTime(
    final Long milestoneId,
    final Long parentId,
    final Long workpackId,
    final Long baselineId
  ) {
    final LocalDate refDate = LocalDate.now();

    final Set<Long> onTime = Optional.ofNullable(baselineId)
      .map(id -> this.repository.onTime(id, workpackId, refDate))
      .orElse(this.repository.onTime(parentId, refDate));

    return onTime.contains(milestoneId);
  }

}
