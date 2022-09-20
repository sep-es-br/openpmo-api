package br.gov.es.openpmo.dto.workpack;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WorkpackPasteResponse {

  @JsonProperty("canPaste")
  private Boolean canPaste;

  @JsonProperty("incompatiblesProperties")
  private Boolean incompatiblesProperties;

  public WorkpackPasteResponse() {
  }

  public WorkpackPasteResponse(
    final Boolean canPaste,
    final Boolean incompatiblesProperties
  ) {
    this.canPaste = canPaste;
    this.incompatiblesProperties = incompatiblesProperties;
  }

  public Boolean getCanPaste() {
    return this.canPaste;
  }

  public void setCanPaste(final Boolean canPaste) {
    this.canPaste = canPaste;
  }

  public Boolean getIncompatiblesProperties() {
    return this.incompatiblesProperties;
  }

  public void setIncompatiblesProperties(final Boolean incompatiblesProperties) {
    this.incompatiblesProperties = incompatiblesProperties;
  }

}
