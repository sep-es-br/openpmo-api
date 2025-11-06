package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.UpdateObject;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;

import java.util.List;

@FunctionalInterface
public interface IGetAnotherTimeBaselineUpdatesService {

  List<UpdateObject> getUpdates(
      final Baseline baseline,
      final Workpack workpack);

}
