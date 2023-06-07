package br.gov.es.openpmo.dto.reports;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;

public class GeneratedReport {
  private ByteArrayResource resource;
  private MediaType contentType;
  private String filename;

  public ByteArrayResource getResource() {
    return resource;
  }

  public void setResource(ByteArrayResource resource) {
    this.resource = resource;
  }

  public MediaType getContentType() {
    return contentType;
  }

  public void setContentType(MediaType contentType) {
    this.contentType = contentType;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

}
