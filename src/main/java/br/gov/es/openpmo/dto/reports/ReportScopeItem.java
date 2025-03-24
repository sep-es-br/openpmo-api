package br.gov.es.openpmo.dto.reports;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import br.gov.es.openpmo.model.workpacks.Workpack;

public class ReportScopeItem implements Scope {

  private final Long id;

  private final String name;

  private final String fullName;

  private final String icon;

  private Boolean hasPermission;

  private final List<ReportScopeItem> children = new LinkedList<>();

  private ReportScopeItem(final Long id, final String name, final String fullName, final String icon, final Boolean hasPermission) {
    this.id = id;
    this.name = name;
    this.fullName = fullName;
    this.icon = icon;
    this.hasPermission = hasPermission;
  }

  public static ReportScopeItem of(final Workpack workpack, final boolean canReadOrEdit) {
    return new ReportScopeItem(
      workpack.getId(),
      workpack.getName(),
      workpack.getFullName(),
      workpack.getIcon(),
      canReadOrEdit
    );
  }

  @Override
  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public String getIcon() {
    return this.icon;
  }

  @Override
  public void setHasPermission(Boolean hasPermission) {
    this.hasPermission = hasPermission;
  }

  @Override
  public List<? extends Scope> getChildren() {
    return this.children;
  }

  @Override
  public Boolean getHasPermission() {
    return this.hasPermission;
  }

  public void addChildren(final Collection<? extends ReportScopeItem> children) {
    this.children.addAll(children);
  }

}
