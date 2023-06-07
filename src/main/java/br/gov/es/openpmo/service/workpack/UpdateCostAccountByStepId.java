package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.schedule.UpdateCostAccountByStepIdRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.repository.ConsumesRepository;
import br.gov.es.openpmo.repository.StepRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UpdateCostAccountByStepId {

  private final StepRepository stepRepository;

  private final ConsumesRepository consumesRepository;

  public UpdateCostAccountByStepId(
    StepRepository stepRepository,
    ConsumesRepository consumesRepository
  ) {
    this.stepRepository = stepRepository;
    this.consumesRepository = consumesRepository;
  }

  public void execute(
    Long idStep,
    Long idCostAccount,
    UpdateCostAccountByStepIdRequest request
  ) {
    final Step step = getStep(
      idStep,
      idCostAccount
    );
    final Consumes consumes = getConsumes(step);
    updateConsumes(
      consumes,
      request
    );
  }

  private void updateConsumes(
    Consumes consumes,
    UpdateCostAccountByStepIdRequest request
  ) {
    consumes.setActualCost(request.getActualCost());
    consumes.setPlannedCost(request.getPlannedCost());
    this.consumesRepository.save(consumes);
  }

  private static Consumes getConsumes(Step step) {
    final Set<Consumes> consumes = step.getConsumes();
    if (consumes == null || consumes.isEmpty()) {
      throw new NegocioException(ApplicationMessage.COST_ACCOUNT_NOT_FOUND);
    }
    if (consumes.size() != 1) {
      throw new NegocioException(ApplicationMessage.STEP_HAS_MORE_THAN_ONE_COST_ACCOUNT);
    }
    return consumes.stream().findFirst().get();
  }

  private Step getStep(
    Long idStep,
    Long idCostAccount
  ) {
    return this.stepRepository.findByStepIdAndCostAccountsId(
        idStep,
        idCostAccount
      )
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

}
