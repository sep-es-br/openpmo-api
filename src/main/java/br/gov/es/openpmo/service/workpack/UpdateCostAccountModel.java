package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpackmodel.params.UpdateCostAccountModelRequest;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.workpacks.models.CostAccountModel;
import br.gov.es.openpmo.repository.CostAccountModelRepository;
import br.gov.es.openpmo.service.permissions.canaccess.CanAccessService;
import br.gov.es.openpmo.service.reports.models.GetPropertyModelFromDto;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
public class UpdateCostAccountModel {

  private final CanAccessService canAccessService;
  private final CostAccountModelRepository costAccountModelRepository;
  private final GetPropertyModelFromDto getPropertyModelFromDto;
  private final UpdatePropertyModels updatePropertyModels;

  public UpdateCostAccountModel(
    CanAccessService canAccessService,
    CostAccountModelRepository costAccountModelRepository,
    GetPropertyModelFromDto getPropertyModelFromDto,
    UpdatePropertyModels updatePropertyModels
  ) {
    this.canAccessService = canAccessService;
    this.costAccountModelRepository = costAccountModelRepository;
    this.getPropertyModelFromDto = getPropertyModelFromDto;
    this.updatePropertyModels = updatePropertyModels;
  }

  @Transactional
  public CostAccountModel execute(UpdateCostAccountModelRequest request, String authorization) {
    final CostAccountModel costAccountModel = getCostAccountModel(request);
    ensureCanEditResource(costAccountModel, authorization);
    final Set<PropertyModel> properties = getPropertyModels(request);
    final Set<PropertyModel> propertiesToUpdate = costAccountModel.getProperties();
    this.updatePropertyModels.execute(properties, propertiesToUpdate);
    return this.costAccountModelRepository.save(costAccountModel);
  }

  private void ensureCanEditResource(CostAccountModel costAccountModel, String authorization) {
    final PlanModel planModel = costAccountModel.getPlanModel();
    this.canAccessService.ensureCanEditResource(planModel.getId(), authorization);
  }

  private CostAccountModel getCostAccountModel(UpdateCostAccountModelRequest request) {
    return this.costAccountModelRepository.findById(request.getId())
      .orElseThrow(() -> new RegistroNaoEncontradoException(ApplicationMessage.COST_ACCOUNT_MODEL_NOT_FOUND));
  }

  private Set<PropertyModel> getPropertyModels(UpdateCostAccountModelRequest request) {
    final List<? extends PropertyModelDto> properties = request.getProperties();
    return this.getPropertyModelFromDto.execute(properties);
  }

}
