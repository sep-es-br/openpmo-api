package br.gov.es.openpmo.dto.reports;

import br.gov.es.openpmo.model.office.plan.Plan;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ReportScope implements Scope {

  private final Long idPlan;

  private final String name;

  private final String fullName;

  private Boolean hasPermission;

  private final List<ReportScopeItem> children = new LinkedList<>();

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

  @Override
  public void setHasPermission(Boolean hasPermission) {
    this.hasPermission = hasPermission;
  }

  @Override
  public Long getId() {
    return this.idPlan;
  }

  @Override
  public Boolean getHasPermission() {
    return this.hasPermission;
  }

  @Override
  public List<? extends Scope> getChildren() {
    return this.children;
  }

  public void addChildren(final Collection<? extends ReportScopeItem> children) {
    this.children.addAll(children);
  }

}
