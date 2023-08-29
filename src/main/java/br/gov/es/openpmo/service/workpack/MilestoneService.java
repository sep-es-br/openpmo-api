package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpack.MilestoneDateQueryResult;
import br.gov.es.openpmo.dto.workpack.MilestoneDetailDto;
import br.gov.es.openpmo.dto.workpack.MilestoneDetailParentDto;
import br.gov.es.openpmo.enumerator.MilestoneStatus;
import br.gov.es.openpmo.model.properties.Date;
import br.gov.es.openpmo.repository.MilestoneRepository;
import br.gov.es.openpmo.repository.dashboards.DashboardMilestoneRepository;
import br.gov.es.openpmo.service.dashboards.v2.DashboardMilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class MilestoneService {

  private final MilestoneRepository milestoneRepository;

  private final DashboardMilestoneRepository dashboardRepository;

  private final DashboardMilestoneService dashboardService;

  @Autowired
  public MilestoneService(
    final MilestoneRepository milestoneRepository,
    final DashboardMilestoneRepository dashboardRepository,
    final DashboardMilestoneService dashboardService
  ) {
    this.milestoneRepository = milestoneRepository;
    this.dashboardRepository = dashboardRepository;
    this.dashboardService = dashboardService;
  }

  public void addStatus(
    final Long milestoneId,
    final MilestoneDetailDto milestoneDetailDto
  ) {
    final Long parentId = this.dashboardRepository.findParentIdByMilestoneId(milestoneId);
    final Long workpackId = this.dashboardRepository.findWorkpackIdByMilestoneId(milestoneId);
    final Long baselineId = this.dashboardRepository.findBaselineIdByMilestoneId(milestoneId);

    final boolean late = this.dashboardService.isLate(
      milestoneId,
      parentId,
      workpackId,
      baselineId
    );
    if (late) {
      milestoneDetailDto.setMilestoneStatus(MilestoneStatus.LATE);
      return;
    }

    final boolean onTime = this.dashboardService.isOnTime(
      milestoneId,
      parentId,
      workpackId,
      baselineId
    );
    if (onTime) {
      milestoneDetailDto.setMilestoneStatus(MilestoneStatus.ON_TIME);
      return;
    }

    final boolean concluded = this.dashboardService.isConcluded(
      milestoneId,
      parentId,
      workpackId,
      baselineId
    );
    if (concluded) {
      milestoneDetailDto.setMilestoneStatus(MilestoneStatus.CONCLUDED);
      return;
    }

    final boolean lateConcluded = this.dashboardService.isLateConcluded(
      milestoneId,
      workpackId,
      baselineId
    );
    if (lateConcluded) {
      milestoneDetailDto.setMilestoneStatus(MilestoneStatus.LATE_CONCLUDED);
    }
  }

  public void addStatus(
    final Long milestoneId,
    final MilestoneDetailParentDto milestoneDetailDto
  ) {
    final Long parentId = this.dashboardRepository.findParentIdByMilestoneId(milestoneId);
    final Long workpackId = this.dashboardRepository.findWorkpackIdByMilestoneId(milestoneId);
    final Long baselineId = this.dashboardRepository.findBaselineIdByMilestoneId(milestoneId);

    final boolean late = this.dashboardService.isLate(
      milestoneId,
      parentId,
      workpackId,
      baselineId
    );
    if (late) {
      milestoneDetailDto.setMilestoneStatus(MilestoneStatus.LATE);
      return;
    }

    final boolean onTime = this.dashboardService.isOnTime(
      milestoneId,
      parentId,
      workpackId,
      baselineId
    );
    if (onTime) {
      milestoneDetailDto.setMilestoneStatus(MilestoneStatus.ON_TIME);
      return;
    }

    final boolean concluded = this.dashboardService.isConcluded(
      milestoneId,
      parentId,
      workpackId,
      baselineId
    );
    if (concluded) {
      milestoneDetailDto.setMilestoneStatus(MilestoneStatus.CONCLUDED);
      return;
    }

    final boolean lateConcluded = this.dashboardService.isLateConcluded(
      milestoneId,
      workpackId,
      baselineId
    );
    if (lateConcluded) {
      milestoneDetailDto.setMilestoneStatus(MilestoneStatus.LATE_CONCLUDED);
    }
  }

  public void addDate(
    final Long milestoneId,
    final MilestoneDetailDto milestoneDetailDto
  ) {
    this.milestoneRepository.fetchMilestoneDate(milestoneId)
      .map(Date::getValue)
      .map(LocalDateTime::toLocalDate)
      .ifPresent(milestoneDetailDto::setMilestoneDate);

    this.milestoneRepository.fetchMilestoneBaselineDate(milestoneId)
      .map(Date::getValue)
      .map(LocalDateTime::toLocalDate)
      .ifPresent(milestoneDetailDto::setBaselineDate);

    final LocalDate milestoneDate = milestoneDetailDto.getMilestoneDate();
    final LocalDate baselineDate = milestoneDetailDto.getBaselineDate();

    LocalDateTime milestoneOrToday;
    final boolean concluded = milestoneRepository.isConcluded(milestoneId);
    if (!concluded && LocalDate.now().isAfter(milestoneDate)) {
      milestoneOrToday = LocalDate.now().atStartOfDay();
    } else {
      milestoneOrToday = milestoneDate.atStartOfDay();
    }

    if (milestoneDate != null && baselineDate != null) {
      final Duration between = Duration.between(
        baselineDate.atStartOfDay(),
        milestoneOrToday
      );
      final long days = between.toDays();
      milestoneDetailDto.setDelayInDays(days);
    }

    final MilestoneDateQueryResult queryResult = this.milestoneRepository.getMilestoneDateQueryResult(milestoneId);

    Optional.ofNullable(queryResult)
      .map(MilestoneDateQueryResult::getExpirationDate)
      .map(ZonedDateTime::toLocalDate)
      .ifPresent(milestoneDetailDto::setExpirationDate);
  }

  public void addDate(
    final Long milestoneId,
    final MilestoneDetailParentDto milestoneDetailDto
  ) {
    this.milestoneRepository.fetchMilestoneDate(milestoneId)
      .map(Date::getValue)
      .map(LocalDateTime::toLocalDate)
      .ifPresent(milestoneDetailDto::setMilestoneDate);
  }

  public LocalDate getMilestoneDate(final Long milestoneId) {
    return this.milestoneRepository.fetchMilestoneDate(milestoneId)
      .map(Date::getValue)
      .map(LocalDateTime::toLocalDate)
      .orElse(null);
  }

  public LocalDate getExpirationDate(final Long milestoneId) {
    final MilestoneDateQueryResult queryResult = this.milestoneRepository.getMilestoneDateQueryResult(milestoneId);
    return Optional.ofNullable(queryResult)
      .map(MilestoneDateQueryResult::getExpirationDate)
      .map(ZonedDateTime::toLocalDate)
      .orElse(null);
  }

  public LocalDate getBaselineDate(final Long milestoneId) {
    return this.milestoneRepository.fetchMilestoneBaselineDate(milestoneId)
      .map(Date::getValue)
      .map(LocalDateTime::toLocalDate)
      .orElse(null);
  }

  public MilestoneStatus getStatus(final Long milestoneId) {
    final Long parentId = this.dashboardRepository.findParentIdByMilestoneId(milestoneId);
    final Long workpackId = this.dashboardRepository.findWorkpackIdByMilestoneId(milestoneId);
    final Long baselineId = this.dashboardRepository.findBaselineIdByMilestoneId(milestoneId);
    final boolean late = this.dashboardService.isLate(
      milestoneId,
      parentId,
      workpackId,
      baselineId
    );
    if (late) {
      return MilestoneStatus.LATE;
    }
    final boolean onTime = this.dashboardService.isOnTime(
      milestoneId,
      parentId,
      workpackId,
      baselineId
    );
    if (onTime) {
      return MilestoneStatus.ON_TIME;
    }
    final boolean concluded = this.dashboardService.isConcluded(
      milestoneId,
      parentId,
      workpackId,
      baselineId
    );
    if (concluded) {
      return MilestoneStatus.CONCLUDED;
    }
    final boolean lateConcluded = this.dashboardService.isLateConcluded(
      milestoneId,
      workpackId,
      baselineId
    );
    if (lateConcluded) {
      return MilestoneStatus.LATE_CONCLUDED;
    }
    return null;
  }

}
