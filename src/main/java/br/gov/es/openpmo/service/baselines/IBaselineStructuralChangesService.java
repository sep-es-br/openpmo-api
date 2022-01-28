package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;

public interface IBaselineStructuralChangesService {

  boolean hasStructureChanges(
      final Baseline baseline,
      final Workpack workpack
  );

  boolean hasBaselineStructureChanges(
      Baseline baseline,
      Workpack workpack
  );

}
