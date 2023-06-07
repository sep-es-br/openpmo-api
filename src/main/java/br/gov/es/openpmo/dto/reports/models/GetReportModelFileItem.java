package br.gov.es.openpmo.dto.reports.models;

import br.gov.es.openpmo.model.actors.File;

public class GetReportModelFileItem {

  private final Long id;

  private final String mimeType;

  private final String userGivenName;

  private final String uniqueNameKey;

  private final Boolean compiled;

  private final Boolean main;

  public GetReportModelFileItem(
    final Long id,
    final String mimeType,
    final String userGivenName,
    final String uniqueNameKey,
    Boolean compiled, final Boolean main
  ) {
    this.id = id;
    this.mimeType = mimeType;
    this.userGivenName = userGivenName;
    this.uniqueNameKey = uniqueNameKey;
    this.compiled = compiled;
    this.main = main;
  }

  public static GetReportModelFileItem of(final File file) {
    return new GetReportModelFileItem(
      file.getId(),
      file.getMimeType(),
      file.getUserGivenName(),
      file.getUniqueNameKey(),
      file.hasCompiledFile(),
      file.getMain()
    );
  }

  public Long getId() {
    return this.id;
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

  public Boolean getCompiled() {
    return compiled;
  }
}
