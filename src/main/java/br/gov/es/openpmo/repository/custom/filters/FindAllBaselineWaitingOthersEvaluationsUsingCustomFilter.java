package br.gov.es.openpmo.repository.custom.filters;

import br.gov.es.openpmo.enumerator.BaselineViewStatus;
import br.gov.es.openpmo.repository.BaselineRepository;
import org.springframework.stereotype.Component;

@Component
public class FindAllBaselineWaitingOthersEvaluationsUsingCustomFilter
  extends FindAllBaselineUsingCustomFilter {

  public FindAllBaselineWaitingOthersEvaluationsUsingCustomFilter(BaselineRepository baselineRepository) {
    super(baselineRepository);
  }

  @Override
  protected BaselineViewStatus getStatus() {
    return BaselineViewStatus.WAITING_OTHERS_EVALUATIONS;
  }

}
