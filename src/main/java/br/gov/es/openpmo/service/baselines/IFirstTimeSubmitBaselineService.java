package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.UpdateRequest;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface IFirstTimeSubmitBaselineService {

  List<Long> submit(
    final Baseline baseline,
    final Long workpackId,
    final Optional<Long> parentId
  );

}
