package br.gov.es.openpmo.service.reports;

import br.gov.es.openpmo.dto.reports.ReportScope;
import br.gov.es.openpmo.dto.reports.ReportScopeItem;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class ReportScopeValidator {

  private final GetReportScope getReportScope;

  public ReportScopeValidator(final GetReportScope getReportScope) {
    this.getReportScope = getReportScope;
  }


  public void execute(final List<Long> rawScope, final Long idPlan, final String authorization) {
    final ReportScope scope = this.getReportScope.execute(idPlan, authorization);
    final List<Long> flatScope = this.flatScope(scope);

    for (final Long scopeId : rawScope) {
      if (!flatScope.contains(scopeId)) {
        throw new NegocioException(ApplicationMessage.REPORT_GENERATE_SCOPE_PARAMETER_INVALID);
      }
    }
  }

  private List<Long> flatScope(final ReportScope scope) {
    final List<Long> flatScope = new ArrayList<>();
    if (scope.getHasPermission()) {
      flatScope.add(scope.getIdPlan());
    }
    final List<Long> flatScopeChildren = this.flatScope(scope.getChildren());
    flatScope.addAll(flatScopeChildren);
    return flatScope;
  }

  private List<Long> flatScope(final Collection<ReportScopeItem> children) {
    if (children.isEmpty()) return new ArrayList<>();
    final List<Long> flatScope = new ArrayList<>();
    for (final ReportScopeItem child : children) {
      if (child.getHasPermission()) {
        flatScope.add(child.getId());
      }
      flatScope.addAll(this.flatScope(child.getChildren()));
    }
    return flatScope;
  }

}
