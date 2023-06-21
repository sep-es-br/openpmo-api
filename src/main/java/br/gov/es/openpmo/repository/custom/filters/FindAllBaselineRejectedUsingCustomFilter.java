package br.gov.es.openpmo.repository.custom.filters;

import br.gov.es.openpmo.enumerator.BaselineViewStatus;
import br.gov.es.openpmo.repository.BaselineRepository;
import org.springframework.stereotype.Component;

@Component
public class FindAllBaselineRejectedUsingCustomFilter
  extends FindAllBaselineUsingCustomFilter {

  public FindAllBaselineRejectedUsingCustomFilter(BaselineRepository baselineRepository) {
    super(baselineRepository);
  }

  @Override
  protected BaselineViewStatus getStatus() {
    return BaselineViewStatus.REJECTEDS;
  }

}
