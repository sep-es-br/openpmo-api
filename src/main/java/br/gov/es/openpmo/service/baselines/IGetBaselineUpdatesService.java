package br.gov.es.openpmo.service.baselines;

import java.util.List;
import br.gov.es.openpmo.dto.baselines.BaselineUpdateBreakdown;

@FunctionalInterface
public interface IGetBaselineUpdatesService {
  List<BaselineUpdateBreakdown> getUpdates(Long idWorkpack, Long idPlan);
}
