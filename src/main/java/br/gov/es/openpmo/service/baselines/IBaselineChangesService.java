package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;

@FunctionalInterface
public interface IBaselineChangesService {

  boolean hasChanges(
      final Baseline baseline,
      final Workpack workpack,
      final boolean isSnapshot
  );

}
