package br.gov.es.openpmo.dto.reports;

import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.workpacks.Workpack;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class ReportScopeItem {

  private final Long id;

  private final String name;

  private final String fullName;

  private final String icon;

  private final Boolean hasPermission;

  private final Collection<ReportScopeItem> children = new LinkedList<>();

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
      workpack.getPropertyName()
        .map(Property::getValue)
        .map(String.class::cast)
        .orElse(null),
      workpack.getPropertyFullName()
        .map(Property::getValue)
        .map(String.class::cast)
        .orElse(null),
      workpack.getIcon(),
      canReadOrEdit
    );
  }

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

  public Collection<ReportScopeItem> getChildren() {
    return Collections.unmodifiableCollection(this.children);
  }

  public Boolean getHasPermission() {
    return this.hasPermission;
  }

  public void addChildren(final Collection<? extends ReportScopeItem> children) {
    this.children.addAll(children);
  }

}
