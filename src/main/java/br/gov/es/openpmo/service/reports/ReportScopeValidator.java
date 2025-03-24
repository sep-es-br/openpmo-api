package br.gov.es.openpmo.service.reports;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportScopeValidator {

  private final GetReportScope getReportScope;

  public ReportScopeValidator(final GetReportScope getReportScope) {
    this.getReportScope = getReportScope;
  }

  public List<Long> execute(final List<Long> rawScope, final Long idPlan, final String authorization) {
    final List<Long> scopeWithPermission = this.getReportScope.execute(rawScope, idPlan, authorization);
    if (scopeWithPermission == null || scopeWithPermission.isEmpty()) {
      throw new NegocioException(ApplicationMessage.REPORT_GENERATE_SCOPE_PARAMETER_INVALID);
    }
    return scopeWithPermission;
  }

}
