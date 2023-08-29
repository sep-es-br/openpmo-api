package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpackmodel.params.CreateCostAccountModelRequest;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.workpacks.models.CostAccountModel;
import br.gov.es.openpmo.repository.CostAccountModelRepository;
import br.gov.es.openpmo.service.office.plan.PlanModelService;
import br.gov.es.openpmo.service.permissions.canaccess.CanAccessService;
import br.gov.es.openpmo.service.reports.models.GetPropertyModelFromDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
public class CreateCostAccountModel {

  private final CanAccessService canAccessService;
  private final PlanModelService planModelService;
  private final CostAccountModelRepository costAccountModelRepository;
  private final GetPropertyModelFromDto getPropertyModelFromDto;

  public CreateCostAccountModel(
    CanAccessService canAccessService,
    PlanModelService planModelService,
    CostAccountModelRepository costAccountModelRepository,
    GetPropertyModelFromDto getPropertyModelFromDto
  ) {
    this.canAccessService = canAccessService;
    this.planModelService = planModelService;
    this.costAccountModelRepository = costAccountModelRepository;
    this.getPropertyModelFromDto = getPropertyModelFromDto;
  }

  @Transactional
  public CostAccountModel execute(CreateCostAccountModelRequest request, String authorization) {
    ensureCanEditResource(request, authorization);
    final CostAccountModel costAccountModel = new CostAccountModel();
    final PlanModel planModel = getPlanModel(request);
    costAccountModel.setPlanModel(planModel);
    final Set<PropertyModel> propertyModels = getPropertyModels(request);
    costAccountModel.setProperties(propertyModels);
    return this.costAccountModelRepository.save(costAccountModel);
  }

  private void ensureCanEditResource(CreateCostAccountModelRequest request, String authorization) {
    this.canAccessService.ensureCanEditResource(request.getIdPlanModel(), authorization);
  }

  private PlanModel getPlanModel(CreateCostAccountModelRequest request) {
    final Long idPlanModel = request.getIdPlanModel();
    return this.planModelService.findById(idPlanModel);
  }

  private Set<PropertyModel> getPropertyModels(CreateCostAccountModelRequest request) {
    final List<? extends PropertyModelDto> properties = request.getProperties();
    return this.getPropertyModelFromDto.execute(properties);
  }

}
