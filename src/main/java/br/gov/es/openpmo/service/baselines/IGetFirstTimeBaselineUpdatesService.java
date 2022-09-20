package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.UpdateResponse;
import br.gov.es.openpmo.model.workpacks.Workpack;

import java.util.List;

@FunctionalInterface
public interface IGetFirstTimeBaselineUpdatesService {

  List<UpdateResponse> getUpdates(
    final Iterable<? extends Workpack> workpacks,
    final boolean isSnapshot
  );

}
