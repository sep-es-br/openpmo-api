package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpackmodel.GetNextPositionRequest;
import br.gov.es.openpmo.dto.workpackmodel.GetNextPositionResponse;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.repository.PlanModelRepository;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

@Component
public class GetNextPosition {

  private static final long ONE = 1;

  private final WorkpackModelRepository workpackModelRepository;

  private final PlanModelRepository planModelRepository;

  @Autowired
  public GetNextPosition(
    final WorkpackModelRepository workpackModelRepository,
    final PlanModelRepository planModelRepository
  ) {
    this.workpackModelRepository = workpackModelRepository;
    this.planModelRepository = planModelRepository;
  }

  public GetNextPositionResponse execute(final GetNextPositionRequest request) {
    if (request.hasIdWorkpackModel()) {
      return handleNextPosition(this.workpackModelRepository::findActualPosition, request.getIdWorkpackModel());
    }
    if (request.hasIdPlanModel()) {
      return handleNextPosition(this.planModelRepository::findActualPosition, request.getIdPlanModel());
    }
    throw new NegocioException("idWorkpackModel e idPlanModel nulos.");
  }

  private static GetNextPositionResponse handleNextPosition(
    final Function<? super Long, Long> nextPositionFunction,
    final Long id
  ) {
    final Long actualPosition = nextPositionFunction.apply(id);
    if (Objects.isNull(actualPosition)) return GetNextPositionResponse.of(ONE);
    return GetNextPositionResponse.of(actualPosition + ONE);
  }

}
