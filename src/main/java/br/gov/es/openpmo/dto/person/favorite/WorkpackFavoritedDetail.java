package br.gov.es.openpmo.dto.person.favorite;

public class WorkpackFavoritedDetail {

  private final Long id;

  private final String name;

  private final String fullName;

  private final String icon;

  private final boolean canAccess;

  public WorkpackFavoritedDetail(
    final Long id,
    final String name,
    final String fullName,
    final String icon,
    final boolean canAccess
  ) {
    this.id = id;
    this.name = name;
    this.fullName = fullName;
    this.icon = icon;
    this.canAccess = canAccess;
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getIcon() {
    return this.icon;
  }

  public boolean isCanAccess() {
    return this.canAccess;
  }

  public String getFullName() {
    return this.fullName;
  }
}
