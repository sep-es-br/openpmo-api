package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.UpdateResponse;

import java.util.List;

@FunctionalInterface
public interface IGetBaselineUpdatesService {

  List<UpdateResponse> getUpdates(Long idWorkpack);

}
