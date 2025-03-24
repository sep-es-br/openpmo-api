package br.gov.es.openpmo.service.office;

import static br.gov.es.openpmo.utils.ApplicationMessage.OFFICE_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.PLAN_NOT_FOUND;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.menu.WorkpackResultDto;
import br.gov.es.openpmo.dto.treeview.OfficeTreeViewDto;
import br.gov.es.openpmo.dto.treeview.PlanTreeViewDto;
import br.gov.es.openpmo.dto.treeview.WorkpackTreeViewDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.repository.OfficeRepository;
import br.gov.es.openpmo.repository.PlanRepository;
import br.gov.es.openpmo.utils.ApplicationCacheUtil;

@Service
public class OfficeTreeViewService {


  private final OfficeRepository officeRepository;
  private final PlanRepository planRepository;
  private final ApplicationCacheUtil cacheUtil;


  @Autowired
  public OfficeTreeViewService(
    final OfficeRepository officeRepository,
    final ApplicationCacheUtil cacheUtil,
    final PlanRepository planRepository
  ) {
    this.officeRepository = officeRepository;
    this.cacheUtil = cacheUtil;
    this.planRepository = planRepository;
  }

  public OfficeTreeViewDto findOfficeTreeViewById(final Long idOffice, final Long idPlan, final Long idWorkpack) {
    Office office = this.officeRepository.findByIdThin(idOffice)
                                         .orElseThrow(() -> new NegocioException(OFFICE_NOT_FOUND));


    final OfficeTreeViewDto treeView = new OfficeTreeViewDto(office);
    Set<PlanTreeViewDto> planTreeViewDtos = new LinkedHashSet<>(0);
    List<Plan> plans = idPlan != null
                       ? Collections.singletonList(
        this.planRepository.findById(idPlan).orElseThrow(() -> new NegocioException(PLAN_NOT_FOUND)))
                       : planRepository.findAllInOffice(idOffice);
    for (Plan plan : plans) {
      List<WorkpackResultDto> workpacks = this.getListWorkpacks(plan.getId(), idWorkpack);
      PlanTreeViewDto planTree = this.buildPlanTreeView(plan, workpacks);
      planTreeViewDtos.add(planTree);
    }
    treeView.setPlans(planTreeViewDtos);
    return treeView;
  }

  private List<WorkpackResultDto> getListWorkpacks(Long idPlan, Long idWorkpack) {
    if (idWorkpack != null && idPlan != null) {
      return getListWorkpacksById(idPlan, idWorkpack);
    }
    List<WorkpackResultDto> workpacks = cacheUtil.getListWorkpackResultDtoByPlan(idPlan);
    for (WorkpackResultDto workpack : workpacks) {
      if (workpack.getIdParent() != null) {
        workpacks.stream().filter(w -> workpack.getIdParent().equals(w.getId())).findFirst().ifPresent(
            parent -> parent.getChildren().add(workpack));
      }
    }
    return workpacks.stream().filter(w -> w.getIdParent() == null).collect(Collectors.toList());
  }

  private List<WorkpackResultDto> getListWorkpacksById(Long idPlan, Long idWorkpack) {
    WorkpackResultDto workpack = this.cacheUtil.getWorkpackBreakdownStructure(idWorkpack, idPlan,true);
    if (workpack == null) return new ArrayList<>(0);
    return Collections.singletonList(workpack);
  }

  private PlanTreeViewDto buildPlanTreeView(final Plan plan, List<WorkpackResultDto> workpacks) {
    final PlanTreeViewDto planTreeViewDto = new PlanTreeViewDto(plan);
    final Set<WorkpackTreeViewDto> workpackTreeViewDto = this.buildWorkpackTreeView(workpacks);

    planTreeViewDto.setWorkpacks(workpackTreeViewDto);
    return planTreeViewDto;
  }

  private Set<WorkpackTreeViewDto> buildWorkpackTreeView(final Iterable<? extends WorkpackResultDto> workpacks) {
    final Set<WorkpackTreeViewDto> workpacksTreeView = new HashSet<>();
    for (final WorkpackResultDto workpack : workpacks) {
      final String workpackName = workpack.getName();
      final WorkpackTreeViewDto dto = WorkpackTreeViewDto.of(workpack, workpackName);
      if (CollectionUtils.isNotEmpty(workpack.getChildren())) {
        final Set<WorkpackTreeViewDto> children = this.buildWorkpackTreeView(workpack.getChildren());
        dto.setChildren(children);
      }
      workpacksTreeView.add(dto);
    }

    return workpacksTreeView;
  }


}
