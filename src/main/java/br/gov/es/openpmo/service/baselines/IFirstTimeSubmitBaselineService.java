package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.UpdateRequest;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;

import java.util.List;

@FunctionalInterface
public interface IFirstTimeSubmitBaselineService {

  void submit(
    final Baseline baseline,
    final Workpack workpack,
    final List<UpdateRequest> updates
  );

}
