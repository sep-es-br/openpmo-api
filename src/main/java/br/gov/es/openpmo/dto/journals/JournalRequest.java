package br.gov.es.openpmo.dto.journals;

public class JournalRequest {

  private Long workpackId;

  private String description;

  public Long getWorkpackId() {
    return this.workpackId;
  }

  public void setWorkpackId(final Long workpackId) {
    this.workpackId = workpackId;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

}
