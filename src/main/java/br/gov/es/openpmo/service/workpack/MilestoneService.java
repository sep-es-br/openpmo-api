package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpack.MilestoneDateQueryResult;
import br.gov.es.openpmo.dto.workpack.MilestoneDetailDto;
import br.gov.es.openpmo.enumerator.MilestoneStatus;
import br.gov.es.openpmo.model.properties.Date;
import br.gov.es.openpmo.repository.MilestoneRepository;
import br.gov.es.openpmo.repository.dashboards.DashboardMilestoneRepository;
import br.gov.es.openpmo.service.dashboards.v2.DashboardMilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    final boolean late = this.dashboardService.isLate(milestoneId, parentId, workpackId, baselineId);
    if(late) {
      milestoneDetailDto.setMilestoneStatus(MilestoneStatus.LATE);
      return;
    }

    final boolean onTime = this.dashboardService.isOnTime(milestoneId, parentId, workpackId, baselineId);
    if(onTime) {
      milestoneDetailDto.setMilestoneStatus(MilestoneStatus.ON_TIME);
      return;
    }

    final boolean concluded = this.dashboardService.isConcluded(milestoneId, parentId, workpackId, baselineId);
    if(concluded) {
      milestoneDetailDto.setMilestoneStatus(MilestoneStatus.CONCLUDED);
      return;
    }

    final boolean lateConcluded = this.dashboardService.isLateConcluded(milestoneId, workpackId, baselineId);
    if(lateConcluded) {
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

    final MilestoneDateQueryResult queryResult = this.milestoneRepository.getMilestoneDateQueryResult(milestoneId);

    Optional.ofNullable(queryResult)
      .map(MilestoneDateQueryResult::getExpirationDate)
      .map(ZonedDateTime::toLocalDate)
      .ifPresent(milestoneDetailDto::setExpirationDate);

    Optional.ofNullable(queryResult)
      .map(MilestoneDateQueryResult::isWithinAWeek)
      .ifPresent(milestoneDetailDto::setWithinAWeek);
  }

}
