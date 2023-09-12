package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpackmodel.params.GetCostAccountModelResponse;
import br.gov.es.openpmo.model.workpacks.models.CostAccountModel;
import br.gov.es.openpmo.repository.CostAccountModelRepository;
import br.gov.es.openpmo.service.permissions.canaccess.CanAccessService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetCostAccountModelByIdPlanModel {

  private final CanAccessService canAccessService;
  private final CostAccountModelRepository costAccountModelRepository;
  private final GetCostAccountModelResponseByModel getCostAccountModelResponseByModel;

  public GetCostAccountModelByIdPlanModel(
    CanAccessService canAccessService,
    CostAccountModelRepository costAccountModelRepository,
    GetCostAccountModelResponseByModel getCostAccountModelResponseByModel
  ) {
    this.canAccessService = canAccessService;
    this.costAccountModelRepository = costAccountModelRepository;
    this.getCostAccountModelResponseByModel = getCostAccountModelResponseByModel;
  }

  public GetCostAccountModelResponse execute(Long idPlanModel, String authorization) {
//    ensureCanReadResource(idPlanModel, authorization);
    final Optional<CostAccountModel> costAccountModelByPlainModel = getCostAccountModelByIdPlainModel(idPlanModel);
    return costAccountModelByPlainModel.map(this::getResponse).orElse(null);
  }

  private void ensureCanReadResource(Long idPlanModel, String authorization) {
    this.canAccessService.ensureCanReadResource(idPlanModel, authorization);
  }

  private Optional<CostAccountModel> getCostAccountModelByIdPlainModel(Long idPlanModel) {
    return this.costAccountModelRepository.findByPlanModelId(idPlanModel);
  }

  private GetCostAccountModelResponse getResponse(CostAccountModel costAccountModel) {
    return this.getCostAccountModelResponseByModel.execute(costAccountModel);
  }

}
