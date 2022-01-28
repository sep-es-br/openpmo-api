package br.gov.es.openpmo.dto.dashboards.datasheet;

public class DatasheetActor {

  private final Long id;

  private final String name;

  private final String fullName;

  private final DatasheetAvatar avatar;

  public DatasheetActor(final Long id, final String name, final String fullName, final DatasheetAvatar avatar) {
    this.id = id;
    this.name = name;
    this.fullName = fullName;
    this.avatar = avatar;
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

  public DatasheetAvatar getAvatar() {
    return this.avatar;
  }

}
