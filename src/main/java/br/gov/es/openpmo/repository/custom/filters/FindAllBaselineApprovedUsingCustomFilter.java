package br.gov.es.openpmo.repository.custom.filters;

import br.gov.es.openpmo.enumerator.BaselineViewStatus;
import br.gov.es.openpmo.repository.BaselineRepository;
import org.springframework.stereotype.Component;

@Component
public class FindAllBaselineApprovedUsingCustomFilter
  extends FindAllBaselineUsingCustomFilter {

  public FindAllBaselineApprovedUsingCustomFilter(BaselineRepository baselineRepository) {
    super(baselineRepository);
  }

  @Override
  protected BaselineViewStatus getStatus() {
    return BaselineViewStatus.APPROVED;
  }

}
