package br.gov.es.openpmo.repository.custom.filters;

import br.gov.es.openpmo.enumerator.BaselineViewStatus;
import br.gov.es.openpmo.repository.BaselineRepository;
import org.springframework.stereotype.Component;

@Component
public class FindAllBaselineWaitingMyEvaluationUsingCustomFilter
  extends FindAllBaselineUsingCustomFilter {

  public FindAllBaselineWaitingMyEvaluationUsingCustomFilter(BaselineRepository baselineRepository) {
    super(baselineRepository);
  }

  @Override
  protected BaselineViewStatus getStatus() {
    return BaselineViewStatus.WAITING_MY_EVALUATION;
  }

}
