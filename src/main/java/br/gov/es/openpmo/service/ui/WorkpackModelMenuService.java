package br.gov.es.openpmo.service.ui;

import br.gov.es.openpmo.dto.menu.PlanModelMenuResponse;
import br.gov.es.openpmo.dto.menu.WorkpackModelMenuResponse;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.OfficeRepository;
import br.gov.es.openpmo.repository.PlanModelRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WorkpackModelMenuService {

  private final OfficeRepository officeRepository;

  private final PlanModelRepository planModelRepository;

  public WorkpackModelMenuService(
    final OfficeRepository officeRepository,
    final PlanModelRepository planModelRepository
  ) {
    this.officeRepository = officeRepository;
    this.planModelRepository = planModelRepository;
  }

  public List<PlanModelMenuResponse> getResponses(final Long idOffice) {
    final List<PlanModelMenuResponse> planModelMenuResponses = new ArrayList<>();
    final List<PlanModel> planModels = this.officeRepository.findAllPlanModelsByOfficeId(idOffice);

    for(final PlanModel planModel : planModels) {
      final PlanModelMenuResponse planModelMenuResponse = new PlanModelMenuResponse();

      planModelMenuResponse.setId(planModel.getId());
      planModelMenuResponse.setName(planModel.getName());
      planModelMenuResponse.setFullName(planModel.getFullName());

      final List<WorkpackModel> workpackModels =
        this.planModelRepository.findAllWorkpackModelsByPlanModelId(planModel.getId());

      final Collection<WorkpackModelMenuResponse> workpackModelMenuResponses = new HashSet<>();

      for(final WorkpackModel workpackModel : workpackModels) {
        final WorkpackModelMenuResponse response = this.getResponse(planModel, workpackModel);
        workpackModelMenuResponses.add(response);
      }

      final Set<WorkpackModelMenuResponse> sortedWorkpackModels = workpackModelMenuResponses.stream()
        .sorted(Comparator.comparing(WorkpackModelMenuResponse::getName))
        .collect(Collectors.toCollection(LinkedHashSet::new));
      planModelMenuResponse.setWorkpackModels(sortedWorkpackModels);
      planModelMenuResponses.add(planModelMenuResponse);
    }

    planModelMenuResponses.sort(Comparator.comparing(PlanModelMenuResponse::getName));
    return planModelMenuResponses;
  }

  private WorkpackModelMenuResponse getResponse(
    final PlanModel planModel,
    final WorkpackModel workpackModel
  ) {
    final WorkpackModelMenuResponse item = new WorkpackModelMenuResponse();

    item.setId(workpackModel.getId());
    item.setIdPlanModel(planModel.getId());
    item.setName(workpackModel.getModelName());
    item.setFontIcon(workpackModel.getFontIcon());

    final Set<WorkpackModelMenuResponse> children = new HashSet<>();

    if(workpackModel.getChildren() == null) {
      item.setChildren(null);
      return item;
    }

    for(final WorkpackModel child : workpackModel.getChildren()) {
      final WorkpackModelMenuResponse response = this.getResponse(planModel, child);
      children.add(response);
    }

    item.setChildren(children);
    return item;
  }

}
