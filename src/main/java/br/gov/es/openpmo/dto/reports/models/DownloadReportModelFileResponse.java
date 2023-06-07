package br.gov.es.openpmo.dto.reports.models;

public class DownloadReportModelFileResponse {

  private final byte[] rawFile;

  private final String fileName;

  public DownloadReportModelFileResponse(final byte[] rawFile, final String fileName) {
    this.rawFile = rawFile.clone();
    this.fileName = fileName;
  }

  public byte[] getRawFile() {
    return this.rawFile.clone();
  }

  public String getFileName() {
    return this.fileName;
  }

  public String contentDisposition() {
    return "attachment; filename=\"" + this.fileName + "\"";
  }

  public long getFileLength() {
    return this.rawFile.length;
  }

}
