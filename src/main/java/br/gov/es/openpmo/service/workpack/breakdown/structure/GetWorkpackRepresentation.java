package br.gov.es.openpmo.service.workpack.breakdown.structure;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import br.gov.es.openpmo.dto.MilestoneResultDto;
import br.gov.es.openpmo.dto.baselines.UpdateResponse;
import br.gov.es.openpmo.dto.dashboards.DashboardMonthDto;
import br.gov.es.openpmo.dto.dashboards.MilestoneDateDto;
import br.gov.es.openpmo.dto.dashboards.MilestoneDto;
import br.gov.es.openpmo.dto.dashboards.RiskDto;
import br.gov.es.openpmo.dto.dashboards.RiskResultDto;
import br.gov.es.openpmo.dto.dashboards.RiskWorkpackDto;
import br.gov.es.openpmo.dto.menu.WorkpackResultDto;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.JournalInformationDto;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.ScheduleMeasureUnit;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.WorkpackRepresentation;
import br.gov.es.openpmo.enumerator.BaselineStatus;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.office.UnitMeasure;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.UnitSelection;
import br.gov.es.openpmo.model.risk.Importance;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.baselines.GetBaselineUpdatesService;
import br.gov.es.openpmo.utils.DashboardCacheUtil;

@Component
public class GetWorkpackRepresentation {
  private final DashboardCacheUtil dashboardCacheUtil;

  private final BaselineRepository baselineRepository;

  private final WorkpackRepository workpackRepository;

  private final GetBaselineUpdatesService baselineUpdatesService;

  public GetWorkpackRepresentation(
    DashboardCacheUtil dashboardCacheUtil,
    BaselineRepository baselineRepository,
    GetBaselineUpdatesService baselineUpdatesService,
    WorkpackRepository workpackRepository
  ) {
    this.dashboardCacheUtil = dashboardCacheUtil;
    this.baselineRepository = baselineRepository;
    this.baselineUpdatesService = baselineUpdatesService;
    this.workpackRepository = workpackRepository;
  }

  public WorkpackRepresentation execute(
    final WorkpackResultDto workpackDto,
    final List<MilestoneDateDto> milestoneDates,
    final List<RiskWorkpackDto> risks,
    final List<MilestoneDateDto> milestoneWorkpacks,
    final List<Workpack> deliverables,
    final List<JournalInformationDto> journals
  ) {
    final WorkpackRepresentation workpackRepresentation = new WorkpackRepresentation();

    if (Boolean.TRUE.equals(workpackDto.getLinked())) {
      workpackRepresentation.setIdWorkpackModelLinked(workpackDto.getIdWorkpackModel());
    }
    final Long workpackId = workpackDto.getId();
    workpackRepresentation.setIdWorkpack(workpackId);
    workpackRepresentation.setWorkpackType(workpackDto.getType());
    workpackRepresentation.setWorkpackName(workpackDto.getName());
    workpackRepresentation.setJournalInformation(
        journals.stream().filter(j -> j.getIdWorkapck().equals(workpackDto.getId())).findFirst().orElse(null));

    if (this.hasDashboard(workpackDto)) {
      final DashboardMonthDto monthDto = dashboardCacheUtil.getDashboardMonthDto(
        workpackDto.getId(),
        "Deliverable".equals(workpackDto.getType()),
        workpackDto.getIdPlan()
      );
      workpackRepresentation.setDashboard(monthDto);
      workpackRepresentation.setMilestones(this.getMilestorneResultDto(milestoneDates, workpackDto));
      workpackRepresentation.setRisks(this.getRiskResultDto(risks, workpackDto));
    }

    Long projectId = (long) 1;
    String currentWorkpackType = workpackDto.getType();

    if ("Project".equals(currentWorkpackType)) {
      projectId = workpackId;
    } else if ("Organizer".equals(currentWorkpackType)) {
      projectId = this.workpackRepository.findProjectIdByOrganizerId(workpackId);
    } else if ("Milestone".equals(currentWorkpackType)) {
      projectId = this.workpackRepository.findProjectIdByMilestoneId(workpackId);
    } else if ("Deliverable".equals(currentWorkpackType)) {
      projectId = this.workpackRepository.findProjectIdByDeliverableId(workpackId);
    }

    Optional<Baseline> activeBaseline = this.baselineRepository.findActiveBaseline(projectId);
    workpackRepresentation.setHasActiveBaseline(activeBaseline.isPresent());

    if ("Milestone".equals(currentWorkpackType) || "Deliverable".equals(currentWorkpackType)) {
      BaselineStatus currentWorkpackClassification;

      if (activeBaseline.isPresent()) {
        // Possui Linha de Base ativa

        List<UpdateResponse> updates = this.baselineUpdatesService.getUpdates(projectId);
        UpdateResponse filteredUpdate = updates
          .stream()
          .filter(u -> u.getIdWorkpack().equals(workpackId))
          .findFirst()
          .orElse(null);

        if (filteredUpdate != null) {
          currentWorkpackClassification = filteredUpdate.getClassification();
        } else {
          currentWorkpackClassification = BaselineStatus.NEW;
        }
      } else {
        // Não possui Linha de Base ativa, portanto é um workpack novo
        currentWorkpackClassification = BaselineStatus.NEW;
      }

      if ("Milestone".equals(currentWorkpackType)) {
        workpackRepresentation.setMilestoneStatus(currentWorkpackClassification);

        MilestoneDateDto milestone = milestoneWorkpacks
          .stream()
          .filter(m -> m.getIdWorkpack().equals(workpackDto.getId()))
          .findFirst()
          .orElse(null);
        if (milestone != null) {
          MilestoneDto milestoneDto = MilestoneDto.setMiletoneOfMilestoneDate(milestone);
          workpackRepresentation.setMilestone(milestoneDto);
        }
      } else if ("Deliverable".equals(currentWorkpackType)) {
        workpackRepresentation.setDeliverableStatus(currentWorkpackClassification);

        Workpack deliverable = deliverables
          .stream()
          .filter(w -> w.getId().equals(workpackDto.getId()))
          .findFirst()
          .orElse(null);
        if (deliverable != null) {
          final ScheduleMeasureUnit unitMeasure = this.buildUnitMeasure((Deliverable) deliverable);
          workpackRepresentation.setUnitMeasure(unitMeasure);
        }
      }
    }

    return workpackRepresentation;
  }

  private MilestoneResultDto getMilestorneResultDto(
    List<MilestoneDateDto> milestoneDates,
    WorkpackResultDto workpackDto
  ) {
    if (CollectionUtils.isNotEmpty(milestoneDates)) {

      final List<MilestoneDateDto> milestoneDatesWorkpack = milestoneDates.stream().filter(
          m -> workpackDto.getId().equals(m.getIdWorkpack())).collect(Collectors.toList());

      final List<MilestoneDto> milestoneDtos = MilestoneDto.setMilestonesOfMiletonesDate(milestoneDatesWorkpack);

      long concluded = milestoneDtos.stream().filter(m -> Boolean.TRUE.equals(m.isCompleted())
          && (m.getSnapshotDate() == null ||
              (m.getSnapshotDate().isAfter(m.getMilestoneDate()) || m.getSnapshotDate().isEqual(m.getMilestoneDate()))))
          .count();

      long lateConcluded = milestoneDtos.stream().filter(m -> Boolean.TRUE.equals(m.isCompleted())
          && m.getSnapshotDate() != null && m.getSnapshotDate().isBefore(m.getMilestoneDate())).count();

      long late = milestoneDtos.stream().filter(m -> Boolean.FALSE.equals(m.isCompleted())
          && m.getMilestoneDate() != null && LocalDate.now().isAfter(m.getMilestoneDate())).count();

      long onTime = milestoneDtos.stream().filter(m -> Boolean.FALSE.equals(m.isCompleted())
          && m.getMilestoneDate() != null && (LocalDate.now().isBefore(m.getMilestoneDate())
              || LocalDate.now().isEqual(m.getMilestoneDate())))
          .count();

      long total = milestoneDtos.size();
      return new MilestoneResultDto(concluded, late, lateConcluded, onTime, total);

    }
    return null;
  }

  private RiskResultDto getRiskResultDto(final List<RiskWorkpackDto> risks, final WorkpackResultDto workpackDto) {
    if (CollectionUtils.isNotEmpty(risks)) {
      final List<Risk> risksWorkpack = risks.stream().filter(r -> workpackDto.getId().equals(r.getIdWorkpack()))
          .map(RiskWorkpackDto::getRisk).collect(Collectors.toList());
      final List<RiskDto> riskDtos = RiskDto.of(risksWorkpack);
      long high = riskDtos.stream().filter(r -> Importance.HIGH.equals(r.getImportance())).count();
      long low = riskDtos.stream().filter(r -> Importance.LOW.equals(r.getImportance())).count();
      long medium = riskDtos.stream().filter(r -> Importance.MEDIUM.equals(r.getImportance())).count();
      long total = riskDtos.size();
      return new RiskResultDto(high, low, medium, total);
    }
    return null;
  }

  private boolean hasDashboard(final WorkpackResultDto workpackDto) {
    return workpackDto != null && (
      "Portfolio".equals(workpackDto.getType()) ||
      "Program".equals(workpackDto.getType()) ||
      "Project".equals(workpackDto.getType()) ||
      "Organizer".equals(workpackDto.getType()) ||
      "Deliverable".equals(workpackDto.getType())
    );
  }

  private ScheduleMeasureUnit buildUnitMeasure(final Deliverable workpack) {
    final Set<Property> properties = workpack.getProperties();
    for (Property property : properties) {
      if (property instanceof UnitSelection) {
        final UnitMeasure value = ((UnitSelection) property).getValue();
        if (value != null) {
          return ScheduleMeasureUnit.of(value);
        }
      }
    }
    return null;
  }
}
