package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.BaselineDetailResponse;

@FunctionalInterface
public interface IGetBaselineService {

  BaselineDetailResponse getById(Long idBaseline);

}
