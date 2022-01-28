package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;

public interface IBaselineComposeService {

  boolean isSnapshotOfWorkpackComposingBaseline(
      final Baseline baseline,
      final Workpack workpack
  );

  boolean isSnapshotOfMasterComposingBaseline(
      final Baseline baseline,
      final Workpack workpack
  );

}
