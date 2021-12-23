package br.gov.es.openpmo.dto.journals;

import org.springframework.web.multipart.MultipartFile;

public class EvidenceRequest {

  private MultipartFile file;

  public MultipartFile getFile() {
    return this.file;
  }

  public void setFile(final MultipartFile file) {
    this.file = file;
  }

}
