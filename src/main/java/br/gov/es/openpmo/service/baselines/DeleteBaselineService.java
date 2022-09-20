package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteBaselineService implements IDeleteBaselineService {

  private final BaselineRepository baselineRepository;

  @Autowired
  public DeleteBaselineService(final BaselineRepository baselineRepository) {
    this.baselineRepository = baselineRepository;
  }

  @Override
  public void delete(final Long idBaseline) {
    final Baseline baseline = this.findBaselineById(idBaseline);
    baseline.ifIsNotDraftThrowsException();

    final Workpack workpack = this.findWorkpackByIdBaseline(idBaseline);
    workpack.ifIsNotProjectThrowsException();

    this.deleteBaseline(baseline);
  }

  private Baseline findBaselineById(final Long idBaseline) {
    return this.baselineRepository.findById(idBaseline)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.BASELINE_NOT_FOUND));
  }

  private Workpack findWorkpackByIdBaseline(final Long idBaseline) {
    return this.baselineRepository.findWorkpackByBaselineId(idBaseline)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

  private void deleteBaseline(final Baseline baseline) {
    this.baselineRepository.delete(baseline);
  }

}
