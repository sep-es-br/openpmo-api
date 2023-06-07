package br.gov.es.openpmo.dto.reports;

import br.gov.es.openpmo.model.office.plan.Plan;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class ReportScope {

  private final Long idPlan;

  private final String name;

  private final String fullName;

  private final Collection<ReportScopeItem> children = new LinkedList<>();

  private ReportScope(final Long idPlan, final String name, final String fullName) {
    this.idPlan = idPlan;
    this.name = name;
    this.fullName = fullName;
  }

  public static ReportScope of(final Plan plan) {
    return new ReportScope(plan.getId(), plan.getName(), plan.getFullName());
  }

  public Long getIdPlan() {
    return this.idPlan;
  }

  public String getName() {
    return this.name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public Collection<ReportScopeItem> getChildren() {
    return Collections.unmodifiableCollection(this.children);
  }

  public void addChildren(final Collection<? extends ReportScopeItem> children) {
    this.children.addAll(children);
  }

}
