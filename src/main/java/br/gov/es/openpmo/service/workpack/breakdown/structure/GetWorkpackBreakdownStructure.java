package br.gov.es.openpmo.service.workpack.breakdown.structure;

import br.gov.es.openpmo.dto.dashboards.MilestoneDateDto;
import br.gov.es.openpmo.dto.dashboards.RiskWorkpackDto;
import br.gov.es.openpmo.dto.menu.WorkpackResultDto;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.WorkpackBreakdownStructure;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.WorkpackModelBreakdownStructure;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.WorkpackModelRepresentation;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.RiskRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.dashboards.DashboardMilestoneRepository;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessData;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessDataResponse;
import br.gov.es.openpmo.utils.ApplicationCacheUtil;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class GetWorkpackBreakdownStructure {

  private final GetWorkpackRepresentation getWorkpackRepresentation;


  private final ICanAccessData canAccessData;
  private final ApplicationCacheUtil cacheUtil;
  private final DashboardMilestoneRepository dashboardMilestoneRepository;
  private final RiskRepository riskRepository;
  private final WorkpackRepository workpackRepository;


  public GetWorkpackBreakdownStructure(
      final ApplicationCacheUtil cacheUtil,
      final DashboardMilestoneRepository dashboardMilestoneRepository,
      final RiskRepository riskRepository,
      final GetWorkpackRepresentation getWorkpackRepresentation,
      final ICanAccessData canAccessData,
      final WorkpackRepository workpackRepository
  ) {
    this.getWorkpackRepresentation = getWorkpackRepresentation;
    this.canAccessData = canAccessData;
    this.cacheUtil = cacheUtil;
    this.dashboardMilestoneRepository = dashboardMilestoneRepository;
    this.riskRepository = riskRepository;
    this.workpackRepository = workpackRepository;
  }

  public WorkpackBreakdownStructure execute(
    final Long idWorkpack,
    final Boolean allLevels,
    final String authorization
  ) {
    if (idWorkpack == null) {
      throw new IllegalStateException("Id do workpack nulo!");
    }
    if (this.hasOnlyBasicReadPermission(idWorkpack, authorization)) {
      return null;
    }
    WorkpackResultDto workpackDto = cacheUtil.getWorkpackBreakdownStructure(idWorkpack, null,allLevels);
    if (workpackDto != null) {
      final List<Long> idsMileston = new ArrayList<>(getIdsWorkpackByType(Collections.singleton(workpackDto), "Milestone"));
      final List<MilestoneDateDto> milestoneWorkpack = this.dashboardMilestoneRepository.findByIds(idsMileston);

      final List<Long> idsDeliverables = new ArrayList<>(getIdsWorkpackByType(Collections.singleton(workpackDto), "Deliverable"));
      final List<Workpack> deliverables = workpackRepository.findAllDeliverable(idsDeliverables);

      final List<Long> ids = new ArrayList<>(getIdsWorkpack(Collections.singleton(workpackDto), false));
      final List<MilestoneDateDto> milestoneDates = this.dashboardMilestoneRepository.findByParentIds(ids);
      final List<RiskWorkpackDto> risks = this.riskRepository.findByWorkpackIds(ids);
      final List<WorkpackModelBreakdownStructure> children = this.getChildren(workpackDto, milestoneDates, risks, milestoneWorkpack, deliverables);
      if (children.isEmpty()) {
        return null;
      }
      final WorkpackBreakdownStructure rootStructure = new WorkpackBreakdownStructure();
      rootStructure.setWorkpackModels(children);
      rootStructure.setRepresentation(this.getWorkpackRepresentation.execute(workpackDto, milestoneDates, risks, milestoneWorkpack, deliverables));
      return rootStructure;
    }
    return null;
  }

  private Set<Long> getIdsWorkpack(Set<WorkpackResultDto> workpacks, boolean onlyProject) {
    Set<Long> list = new LinkedHashSet<>(0);
    if (CollectionUtils.isNotEmpty(workpacks)) {
      for (WorkpackResultDto workpack : workpacks) {
        if (onlyProject && "Project".equals(workpack.getType())) {
          list.add(workpack.getId());
        }
        if (!onlyProject && hasDashboard(workpack)) {
          list.add(workpack.getId());
        }
        if (workpack != null && CollectionUtils.isNotEmpty(workpack.getChildren())) {
          list.addAll(getIdsWorkpack(workpack.getChildren(), onlyProject));
        }
      }
    }
    return list;
  }
  private Set<Long> getIdsWorkpackByType(Set<WorkpackResultDto> workpacks, String type) {
    Set<Long> list = new LinkedHashSet<>(0);
    if (CollectionUtils.isNotEmpty(workpacks)) {
      for (WorkpackResultDto workpack : workpacks) {
        if (type.equals(workpack.getType())) {
          list.add(workpack.getId());
        }
        if (CollectionUtils.isNotEmpty(workpack.getChildren())) {
          list.addAll(getIdsWorkpackByType(workpack.getChildren(), type));
        }
      }
    }
    return list;
  }

  private boolean hasDashboard(final WorkpackResultDto workpackDto) {
    return workpackDto != null
        && (
        "Portfolio".equals(workpackDto.getType()) ||
        "Program".equals(workpackDto.getType()) ||
        "Project".equals(workpackDto.getType()) ||
        "Organizer".equals(workpackDto.getType()) ||
        "Deliverable".equals(workpackDto.getType())
    );
  }

  private List<WorkpackModelBreakdownStructure> getChildren(
      final WorkpackResultDto workpackDto,
      final List<MilestoneDateDto> milestoneDates,
      final List<RiskWorkpackDto> risks,
      final List<MilestoneDateDto> milestoneWorkpack,
      final List<Workpack> deliverables
  ) {
    final List<WorkpackModelBreakdownStructure> structures = new ArrayList<>();

    if (workpackDto != null && CollectionUtils.isNotEmpty(workpackDto.getChildren())) {

      Map<Long, Set<WorkpackResultDto>> mapModel = workpackDto.getChildrenGroupedByModel();
      mapModel.forEach((idWorkpackModel, childrens) -> {
        WorkpackResultDto model = workpackDto.getChildren().stream().filter(w -> w.getIdWorkpackModel().equals(idWorkpackModel))
                                             .findFirst().orElse(null);
        if (model != null) {
          final List<WorkpackBreakdownStructure> workpackBreakdownStructures = new CopyOnWriteArrayList<>();
          final WorkpackModelBreakdownStructure workpackModelBreakdownStructure = new WorkpackModelBreakdownStructure();
          final WorkpackModelRepresentation summary = new WorkpackModelRepresentation();
          summary.setIdWorkpackModel(model.getIdWorkpackModel());
          summary.setWorkpackModelName(model.getModelNameInPlural());
          summary.setWorkpackModelType(model.getType());
          summary.setWorkpackModelPosition(model.getPosition());
          workpackModelBreakdownStructure.setRepresentation(summary);

          childrens.forEach(child -> {
            final WorkpackBreakdownStructure structure = this.getStructure(child, milestoneDates, risks, milestoneWorkpack, deliverables);
            workpackBreakdownStructures.add(structure);
          });
          workpackBreakdownStructures.sort(Comparator.comparing(
              WorkpackBreakdownStructure::getOrder,
              Comparator.nullsLast(Comparator.naturalOrder())
          ));
          workpackModelBreakdownStructure.setWorkpacks(workpackBreakdownStructures);
          structures.add(workpackModelBreakdownStructure);
        }
      });

      structures.sort(
          Comparator.comparing(WorkpackModelBreakdownStructure::getRepresentationPosition)
                    .thenComparing(
                        WorkpackModelBreakdownStructure::getName,
                        Comparator.nullsLast(Comparator.naturalOrder())
                    ));
    }
    return structures;
  }

  private WorkpackBreakdownStructure getStructure(
      final WorkpackResultDto workpackDto,
      final List<MilestoneDateDto> milestoneDates,
      final List<RiskWorkpackDto> risks,
      final List<MilestoneDateDto> milestoneWorkpack,
      final List<Workpack> deliverables
  ) {
    final WorkpackBreakdownStructure structure = new WorkpackBreakdownStructure();
    structure.setOrder(workpackDto.getName());
    structure.setRepresentation(this.getWorkpackRepresentation.execute(workpackDto, milestoneDates, risks, milestoneWorkpack, deliverables));
    if (CollectionUtils.isNotEmpty(workpackDto.getChildren())) {
      final List<WorkpackModelBreakdownStructure> children = this.getChildren(workpackDto, milestoneDates, risks, milestoneWorkpack, deliverables);
      structure.setWorkpackModels(children);
      return structure;
    }
    structure.setHasChildren(CollectionUtils.isNotEmpty(workpackDto.getChildren()));
    structure.setWorkpackModels(Collections.emptyList());
    return structure;
  }

  private boolean hasOnlyBasicReadPermission(
    final Long idWorkpack,
    final String authorization
  ) {
    final ICanAccessDataResponse canAccessData = this.canAccessData.execute(idWorkpack, authorization);
    if (canAccessData.getAdmin()) {
      return false;
    }
    if (canAccessData.getEdit()) {
      return false;
    }
    if (canAccessData.getRead()) {
      return false;
    }
    return canAccessData.getBasicRead();
  }

}
