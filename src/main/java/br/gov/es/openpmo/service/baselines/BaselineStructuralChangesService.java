package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaselineStructuralChangesService implements IBaselineStructuralChangesService {

  private final BaselineRepository baselineRepository;

  @Autowired
  public BaselineStructuralChangesService(final BaselineRepository baselineRepository) {
    this.baselineRepository = baselineRepository;
  }

  @Override
  public boolean hasStructureChanges(
    final Baseline baseline,
    final Workpack workpack
  ) {
    return this.baselineRepository.hasStructureChanges(workpack.getId(), baseline.getId());
  }

  @Override
  public boolean hasBaselineStructureChanges(
    final Baseline baseline,
    final Workpack workpack
  ) {
    return this.baselineRepository.hasBaselineStructureChanges(workpack.getId(), baseline.getId());
  }

}
