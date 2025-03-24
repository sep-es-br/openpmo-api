package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpackmodel.params.GetCostAccountModelResponse;
import br.gov.es.openpmo.model.workpacks.models.CostAccountModel;
import br.gov.es.openpmo.repository.CostAccountModelRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetCostAccountModelByIdPlanModel {

  private final CostAccountModelRepository costAccountModelRepository;
  private final GetCostAccountModelResponseByModel getCostAccountModelResponseByModel;

  public GetCostAccountModelByIdPlanModel(
    CostAccountModelRepository costAccountModelRepository,
    GetCostAccountModelResponseByModel getCostAccountModelResponseByModel
  ) {
    this.costAccountModelRepository = costAccountModelRepository;
    this.getCostAccountModelResponseByModel = getCostAccountModelResponseByModel;
  }

  public GetCostAccountModelResponse execute(Long idPlanModel) {
    final Optional<CostAccountModel> costAccountModelByPlainModel = getCostAccountModelByIdPlainModel(idPlanModel);
    return costAccountModelByPlainModel.map(this::getResponse).orElse(null);
  }

  private Optional<CostAccountModel> getCostAccountModelByIdPlainModel(Long idPlanModel) {
    return this.costAccountModelRepository.findByPlanModelId(idPlanModel);
  }

  private GetCostAccountModelResponse getResponse(CostAccountModel costAccountModel) {
    return this.getCostAccountModelResponseByModel.execute(costAccountModel);
  }

}
