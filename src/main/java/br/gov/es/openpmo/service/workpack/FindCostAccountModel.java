package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.workpacks.models.CostAccountModel;
import br.gov.es.openpmo.repository.CostAccountModelRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FindCostAccountModel {

  private final CostAccountModelRepository costAccountModelRepository;

  public FindCostAccountModel(CostAccountModelRepository costAccountModelRepository) {
    this.costAccountModelRepository = costAccountModelRepository;
  }

  public CostAccountModel execute(Long id) {
    Objects.requireNonNull(id, ApplicationMessage.ID_COST_ACCOUNT_MODEL_NOT_NULL);
    return this.costAccountModelRepository.findByIdWithRelationships(id)
      .orElseThrow(() -> new RegistroNaoEncontradoException(ApplicationMessage.COST_ACCOUNT_MODEL_NOT_FOUND));
  }

}
