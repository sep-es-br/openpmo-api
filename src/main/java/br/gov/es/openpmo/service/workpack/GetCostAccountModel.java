package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpackmodel.params.GetCostAccountModelResponse;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.workpacks.models.CostAccountModel;
import br.gov.es.openpmo.service.permissions.canaccess.CanAccessService;
import br.gov.es.openpmo.service.reports.models.GetPropertyModelDtosFromEntities;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class GetCostAccountModel {

  private final CanAccessService canAccessService;
  private final FindCostAccountModel findCostAccountModel;
  private final GetPropertyModelDtosFromEntities getPropertyModelDtosFromEntities;

  public GetCostAccountModel(
    CanAccessService canAccessService,
    FindCostAccountModel findCostAccountModel,
    GetPropertyModelDtosFromEntities getPropertyModelDtosFromEntities
  ) {
    this.canAccessService = canAccessService;
    this.findCostAccountModel = findCostAccountModel;
    this.getPropertyModelDtosFromEntities = getPropertyModelDtosFromEntities;
  }

  public GetCostAccountModelResponse execute(Long id, String authorization) {
    final CostAccountModel costAccountModel = getCostAccountModel(id);
    ensureCanReadResource(costAccountModel, authorization);
    return getResponse(costAccountModel);
  }

  private void ensureCanReadResource(CostAccountModel costAccountModel, String authorization) {
    final PlanModel planModel = costAccountModel.getPlanModel();
    this.canAccessService.ensureCanReadResource(planModel.getId(), authorization);
  }

  private GetCostAccountModelResponse getResponse(CostAccountModel costAccountModel) {
    final Long id = costAccountModel.getId();
    final List<? extends PropertyModelDto> propertyModelDtos = getPropertyModelDtos(costAccountModel);
    return new GetCostAccountModelResponse(id, propertyModelDtos);
  }

  private List<? extends PropertyModelDto> getPropertyModelDtos(CostAccountModel costAccountModel) {
    final Set<PropertyModel> properties = costAccountModel.getProperties();
    return this.getPropertyModelDtosFromEntities.execute(properties);
  }

  private CostAccountModel getCostAccountModel(Long id) {
    return this.findCostAccountModel.execute(id);
  }

}
