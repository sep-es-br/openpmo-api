package br.gov.es.openpmo.dto.reports;

import br.gov.es.openpmo.model.office.plan.Plan;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class ReportScope {

  private final Long idPlan;

  private final String name;

  private final String fullName;

  private final Boolean hasPermission;

  private final Collection<ReportScopeItem> children = new LinkedList<>();

  private ReportScope(final Long idPlan, final String name, final String fullName, final Boolean hasPermission) {
    this.idPlan = idPlan;
    this.name = name;
    this.fullName = fullName;
    this.hasPermission = hasPermission;
  }

  public static ReportScope of(final Plan plan, final boolean hasPermission) {
    return new ReportScope(plan.getId(), plan.getName(), plan.getFullName(), hasPermission);
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

  public Boolean getHasPermission() {
    return this.hasPermission;
  }

  public Collection<ReportScopeItem> getChildren() {
    return Collections.unmodifiableCollection(this.children);
  }

  public void addChildren(final Collection<? extends ReportScopeItem> children) {
    this.children.addAll(children);
  }

}
