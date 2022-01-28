package br.gov.es.openpmo.dto.dashboards.datasheet;

public class DatasheetAvatar {

  private final Long id;

  private final String url;

  private final String name;

  private final String mimeType;

  public DatasheetAvatar(final Long id, final String url, final String name, final String mimeType) {
    this.id = id;
    this.url = url;
    this.name = name;
    this.mimeType = mimeType;
  }

  public Long getId() {
    return this.id;
  }

  public String getUrl() {
    return this.url;
  }

  public String getName() {
    return this.name;
  }

  public String getMimeType() {
    return this.mimeType;
  }

}
