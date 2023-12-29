package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.UpdateRequest;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;

import java.util.List;

@FunctionalInterface
public interface IAnotherTimeSubmitBaselineService {

  void submit(
    Baseline baseline,
    Long workpackId,
    List<UpdateRequest> updates
  );

}
