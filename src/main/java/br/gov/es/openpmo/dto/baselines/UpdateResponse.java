package br.gov.es.openpmo.dto.baselines;

import br.gov.es.openpmo.enumerator.BaselineStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateResponse {

  private final Long idWorkpack;

  private final String icon;

  private final String description;

  @JsonProperty("classification")
  private final BaselineStatus classification;

  private final Boolean included;

  @JsonCreator
  public UpdateResponse(
    final Long idWorkpack,
    final String icon,
    final String description,
    final BaselineStatus classification,
    final Boolean included
  ) {
    this.idWorkpack = idWorkpack;
    this.icon = icon;
    this.description = description;
    this.classification = classification;
    this.included = included;
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public String getIcon() {
    return this.icon;
  }

  public String getDescription() {
    return this.description;
  }

  public BaselineStatus getClassification() {
    return this.classification;
  }

  public Boolean isIncluded() {
    return this.included;
  }

}
