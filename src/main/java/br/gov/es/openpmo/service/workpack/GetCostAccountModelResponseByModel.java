package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpackmodel.params.GetCostAccountModelResponse;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.workpacks.models.CostAccountModel;
import br.gov.es.openpmo.service.reports.models.GetPropertyModelDtosFromEntities;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class GetCostAccountModelResponseByModel {

  private final GetPropertyModelDtosFromEntities getPropertyModelDtosFromEntities;

  public GetCostAccountModelResponseByModel(GetPropertyModelDtosFromEntities getPropertyModelDtosFromEntities) {
    this.getPropertyModelDtosFromEntities = getPropertyModelDtosFromEntities;
  }

  public GetCostAccountModelResponse execute(CostAccountModel costAccountModel) {
    final Long id = costAccountModel.getId();
    final List<? extends PropertyModelDto> propertyModelDtos = getPropertyModelDtos(costAccountModel);
    return new GetCostAccountModelResponse(id, propertyModelDtos);
  }

  private List<? extends PropertyModelDto> getPropertyModelDtos(CostAccountModel costAccountModel) {
    final Set<PropertyModel> properties = costAccountModel.getProperties();
    return this.getPropertyModelDtosFromEntities.execute(properties);
  }

}
