package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.EditDraftBaselineRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EditBaselineService implements IEditBaselineService {

  private final BaselineRepository baselineRepository;

  @Autowired
  public EditBaselineService(final BaselineRepository baselineRepository) {
    this.baselineRepository = baselineRepository;
  }

  private static void updateBaselineProperties(
    final Baseline baseline,
    final EditDraftBaselineRequest properties
  ) {
    baseline.setName(properties.getName());
    baseline.setMessage(properties.getMessage());
    baseline.setDescription(properties.getDescription());
  }

  @Override
  public void edit(
    final Long idBaseline,
    final EditDraftBaselineRequest request
  ) {
    final Baseline baseline = this.findBaselineById(idBaseline);
    baseline.ifIsNotDraftThrowsException();

    final Workpack workpack = this.findWorkpackByIdBaseline(idBaseline);
    workpack.ifIsNotProjectThrowsException();

    updateBaselineProperties(baseline, request);
    this.saveBaseline(baseline);
  }

  private Baseline findBaselineById(final Long idBaseline) {
    return this.baselineRepository.findById(idBaseline)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.BASELINE_NOT_FOUND));
  }

  private Workpack findWorkpackByIdBaseline(final Long idBaseline) {
    return this.baselineRepository.findWorkpackByBaselineId(idBaseline)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACK_NOT_FOUND));
  }

  private void saveBaseline(final Baseline baseline) {
    this.baselineRepository.save(baseline, 0);
  }

}
