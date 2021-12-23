package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.EntityDto;
import br.gov.es.openpmo.dto.baselines.SubmitCancellingRequest;

public interface ICancelBaselineService {

  EntityDto submit(SubmitCancellingRequest request, Long personId);

}
