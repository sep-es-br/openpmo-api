package br.gov.es.openpmo.service.office;


import br.gov.es.openpmo.dto.treeview.OfficeTreeViewDto;
import br.gov.es.openpmo.dto.treeview.PlanTreeViewDto;
import br.gov.es.openpmo.dto.treeview.WorkpackTreeViewDto;
import br.gov.es.openpmo.dto.treeview.query.OfficeTreeViewQuery;
import br.gov.es.openpmo.dto.workpack.WorkpackName;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.OfficeRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.OFFICE_NOT_FOUND;

@Service
public class OfficeTreeViewService {


  private final OfficeRepository officeRepository;
  private final WorkpackRepository workpackRepository;


  @Autowired
  public OfficeTreeViewService(
    final OfficeRepository officeRepository,
    final WorkpackRepository workpackRepository
  ) {
    this.officeRepository = officeRepository;
    this.workpackRepository = workpackRepository;
  }

  private static boolean hasChildren(final Workpack workpack) {
    return !CollectionUtils.isEmpty(workpack.getChildren());
  }

  public OfficeTreeViewDto findOfficeTreeViewById(final Long idOffice) {
    final OfficeTreeViewQuery officeTreeViewQuery = this.findOfficeAsTreeViewQuery(idOffice);

    if(officeTreeViewQuery == null) {
      return null;
    }

    final OfficeTreeViewDto treeView = new OfficeTreeViewDto(officeTreeViewQuery.getOffice());

    final Set<PlanTreeViewDto> plans = officeTreeViewQuery.getPlans().stream()
      .map(this::buildPlanTreeView)
      .collect(Collectors.toSet());

    treeView.setPlans(plans);

    return treeView;
  }

  private OfficeTreeViewQuery findOfficeAsTreeViewQuery(final Long idOffice) {
    return this.officeRepository
      .findOfficeTreeViewById(idOffice)
      .orElseThrow(() -> new NegocioException(OFFICE_NOT_FOUND));
  }

  private PlanTreeViewDto buildPlanTreeView(final Plan plan) {
    final PlanTreeViewDto planTreeViewDto = new PlanTreeViewDto(plan);

    final Set<Workpack> workpacks = this.workpackRepository.findAllByPlanWithProperties(plan.getId());

    final Set<WorkpackTreeViewDto> workpackTreeViewDto = this.buildWorkpackTreeView(workpacks);

    planTreeViewDto.setWorkpacks(workpackTreeViewDto);
    return planTreeViewDto;
  }

  private Set<WorkpackTreeViewDto> buildWorkpackTreeView(final Iterable<? extends Workpack> workpacks) {

    final Set<WorkpackTreeViewDto> workpacksTreeView = new HashSet<>();

    for(final Workpack workpack : workpacks) {
      final String workpackName = this.findWorkpackName(workpack);
      final WorkpackTreeViewDto dto = WorkpackTreeViewDto.of(workpack, workpackName);
      if(hasChildren(workpack)) {
        final Set<WorkpackTreeViewDto> children = this.buildWorkpackTreeView(workpack.getChildren());
        dto.setChildren(children);
      }
      workpacksTreeView.add(dto);
    }

    return workpacksTreeView;
  }

  private String findWorkpackName(final Workpack workpack) {
    return this.workpackRepository.findWorkpackNameAndFullname(workpack.getId())
      .map(WorkpackName::getName)
      .orElse(null);
  }

}
