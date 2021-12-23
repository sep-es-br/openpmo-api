package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaselineComposeService implements IBaselineComposeService {

  private final BaselineRepository baselineRepository;

  @Autowired
  public BaselineComposeService(
    final BaselineRepository baselineRepository
  ) {
    this.baselineRepository = baselineRepository;
  }

  @Override
  public boolean isSnapshotOfWorkpackComposingBaseline(final Baseline baseline, final Workpack workpack) {
    return this.baselineRepository.isSnapshotOfWorkpackComposingBaseline(workpack.getId(), baseline.getId());
  }

  @Override
  public boolean isSnapshotOfMasterComposingBaseline(final Baseline baseline, final Workpack workpack) {
    return this.baselineRepository.isSnapshotOfMasterComposingBaseline(workpack.getId(), baseline.getId());
  }

}
