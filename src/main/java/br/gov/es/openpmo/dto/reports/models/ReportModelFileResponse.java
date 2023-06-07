package br.gov.es.openpmo.dto.reports.models;

import org.springframework.web.multipart.MultipartFile;

public class ReportModelFileResponse {

  private final String userGivenName;

  private final String uniqueNameKey;

  private final String mimeType;

  private ReportModelFileResponse(final String userGivenName, final String uniqueNameKey, final String mimeType) {
    this.userGivenName = userGivenName;
    this.uniqueNameKey = uniqueNameKey;
    this.mimeType = mimeType;
  }

  public static ReportModelFileResponse of(final MultipartFile file, final String uniqueNameKey) {
    return new ReportModelFileResponse(
      file.getOriginalFilename(),
      uniqueNameKey,
      file.getContentType()
    );
  }
  
  public static ReportModelFileResponse of(final String userGivenName, final String uniqueNameKey,
			final String mimeType) {
	return new ReportModelFileResponse(userGivenName, uniqueNameKey, mimeType);
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

}
