package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.EvaluationItem;

import java.util.List;

public interface IGetAllBaselineEvaluations {

  List<EvaluationItem> getEvaluations(Long idBaseline);

}
