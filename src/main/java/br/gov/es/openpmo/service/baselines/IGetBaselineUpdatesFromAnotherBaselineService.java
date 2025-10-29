package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.UpdateObject;
import br.gov.es.openpmo.model.baselines.Baseline;

import java.util.List;

@FunctionalInterface
public interface IGetBaselineUpdatesFromAnotherBaselineService {

  List<UpdateObject> getUpdates(
      Baseline baseline,
      Baseline anotherBaseline);

}
