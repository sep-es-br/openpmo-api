package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.UpdateResponse;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;

import java.util.List;

@FunctionalInterface
public interface IGetAnotherTimeBaselineUpdatesService {

  List<UpdateResponse> getUpdates(
    final Baseline baseline,
    final Workpack workpack
  );

}
