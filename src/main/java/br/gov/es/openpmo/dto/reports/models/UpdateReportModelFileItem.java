package br.gov.es.openpmo.dto.reports.models;

import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.service.reports.models.CreateReportModelTemplateSource;

public class UpdateReportModelFileItem implements CreateReportModelTemplateSource.CreateTemplateSourceRequest {

  private final Long id;

  private final String mimeType;

  private final String userGivenName;

  private final String uniqueNameKey;

  private final Boolean main;

  public UpdateReportModelFileItem(
    final Long id,
    final String mimeType,
    final String userGivenName,
    final String uniqueNameKey,
    final Boolean main
  ) {
    this.id = id;
    this.mimeType = mimeType;
    this.userGivenName = userGivenName;
    this.uniqueNameKey = uniqueNameKey;
    this.main = main;
  }

  public static UpdateReportModelFileItem of(final File file) {
    return new UpdateReportModelFileItem(
      file.getId(),
      file.getMimeType(),
      file.getUserGivenName(),
      file.getUniqueNameKey(),
      file.getMain()
    );
  }

  public Long getId() {
    return this.id;
  }

  public String getMimeType() {
    return this.mimeType;
  }

  public String getUniqueNameKey() {
    return this.uniqueNameKey;
  }

  public String getUserGivenName() {
    return this.userGivenName;
  }

  public Boolean getMain() {
    return this.main;
  }

}
