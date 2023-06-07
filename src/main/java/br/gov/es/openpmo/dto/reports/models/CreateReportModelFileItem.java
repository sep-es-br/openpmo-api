package br.gov.es.openpmo.dto.reports.models;

import br.gov.es.openpmo.service.reports.models.CreateReportModelTemplateSource;

public class CreateReportModelFileItem implements CreateReportModelTemplateSource.CreateTemplateSourceRequest {

  private final String mimeType;

  private final String userGivenName;

  private final String uniqueNameKey;

  private final Boolean main;

  public CreateReportModelFileItem(
    final String mimeType,
    final String userGivenName,
    final String uniqueNameKey,
    final Boolean main
  ) {
    this.mimeType = mimeType;
    this.userGivenName = userGivenName;
    this.uniqueNameKey = uniqueNameKey;
    this.main = main;
  }

  public String getMimeType() {
    return this.mimeType;
  }

  public String getUserGivenName() {
    return this.userGivenName;
  }

  public String getUniqueNameKey() {
    return this.uniqueNameKey;
  }

  public Boolean getMain() {
    return this.main;
  }

}
