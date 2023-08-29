package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.model.workpacks.models.CostAccountModel;
import br.gov.es.openpmo.repository.CostAccountModelRepository;
import br.gov.es.openpmo.service.permissions.canaccess.CanAccessService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class DeleteCostAccountModel {

  private final CanAccessService canAccessService;
  private final CostAccountModelRepository costAccountModelRepository;
  private final DeletePropertyModel deletePropertyModel;

  public DeleteCostAccountModel(
    CanAccessService canAccessService,
    CostAccountModelRepository costAccountModelRepository,
    DeletePropertyModel deletePropertyModel
  ) {
    this.canAccessService = canAccessService;
    this.costAccountModelRepository = costAccountModelRepository;
    this.deletePropertyModel = deletePropertyModel;
  }

  @Transactional
  public void execute(Long id, String authorization) {
    final CostAccountModel costAccountModel = getCostAccountModel(id);
    ensureCanEditResource(costAccountModel, authorization);
    final Set<CostAccount> instances = costAccountModel.getInstances();
    if (instances != null && !instances.isEmpty()) {
      throw new NegocioException(ApplicationMessage.COST_ACCOUNT_MODEL_DELETE_RELATIONSHIP_ERROR);
    }
    final Set<PropertyModel> properties = costAccountModel.getProperties();
    if (properties != null && !properties.isEmpty()) {
      for (PropertyModel property : properties) {
        this.deletePropertyModel.execute(property.getId());
      }
    }
    this.costAccountModelRepository.delete(costAccountModel);
  }

  private void ensureCanEditResource(CostAccountModel costAccountModel, String authorization) {
    final PlanModel planModel = costAccountModel.getPlanModel();
    this.canAccessService.ensureCanEditResource(planModel.getId(), authorization);
  }

  private CostAccountModel getCostAccountModel(Long id) {
    return this.costAccountModelRepository.findById(id)
      .orElseThrow(() -> new RegistroNaoEncontradoException(ApplicationMessage.COST_ACCOUNT_MODEL_NOT_FOUND));
  }

}
