package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.UpdateResponse;
import br.gov.es.openpmo.model.baselines.Baseline;

import java.util.List;

@FunctionalInterface
public interface IGetBaselineUpdatesFromAnotherBaselineService {

  List<UpdateResponse> getUpdates(
    Baseline baseline,
    Baseline anotherBaseline
  );

}
