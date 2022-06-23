package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.EvaluationItem;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.BaselineDetailCCBMemberResponse;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintOutput;
import br.gov.es.openpmo.dto.workpack.WorkpackName;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.baselines.calculators.ITripleConstraintsCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.gov.es.openpmo.utils.ApplicationMessage.BASELINE_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;

@Service
public class GetBaselineAsCCBMemberViewService implements IGetBaselineAsCCBMemberViewService {

  private final WorkpackRepository workpackRepository;

  private final BaselineRepository repository;

  private final ITripleConstraintsCalculator tripleConstraintsCalculator;

  private final IGetAllBaselineEvaluations getAllBaselineEvaluations;

  @Autowired
  public GetBaselineAsCCBMemberViewService(
    final WorkpackRepository workpackRepository,
    final BaselineRepository repository,
    final ITripleConstraintsCalculator tripleConstraintsCalculator,
    final IGetAllBaselineEvaluations getAllBaselineEvaluations
  ) {
    this.workpackRepository = workpackRepository;
    this.repository = repository;
    this.tripleConstraintsCalculator = tripleConstraintsCalculator;
    this.getAllBaselineEvaluations = getAllBaselineEvaluations;
  }

  @Override
  public BaselineDetailCCBMemberResponse getById(final Long idBaseline, final Long idPerson) {
    final Baseline baseline = this.findBaselineById(idBaseline);
    final WorkpackName workpackName = this.findWorkpackNameById(baseline);
    final TripleConstraintOutput output = this.tripleConstraintsCalculator.calculate(idBaseline);
    final List<EvaluationItem> evaluations = this.getAllEvaluations(idBaseline, idPerson);

    return BaselineDetailCCBMemberResponse.of(
      baseline,
      workpackName.getName(),
      output,
      evaluations
    );
  }

  private List<EvaluationItem> getAllEvaluations(final Long idBaseline, final Long idPerson) {
    final List<EvaluationItem> evaluations = this.getAllBaselineEvaluations.getEvaluations(idBaseline);
    evaluations.forEach(item -> item.applySelfEvaluation(idPerson));
    return evaluations;
  }

  private WorkpackName findWorkpackNameById(final Baseline baseline) {
    return this.workpackRepository.findWorkpackNameAndFullname(baseline.getIdWorkpack())
      .orElseThrow(() -> new NegocioException(WORKPACK_NOT_FOUND));
  }

  private Baseline findBaselineById(final Long idBaseline) {
    return this.repository.findBaselineDetailById(idBaseline)
      .orElseThrow(() -> new NegocioException(BASELINE_NOT_FOUND));
  }

}
