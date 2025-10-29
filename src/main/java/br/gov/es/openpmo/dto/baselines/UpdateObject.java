package br.gov.es.openpmo.dto.baselines;

import br.gov.es.openpmo.enumerator.BaselineStatus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateObject {
  private final Long idWorkpack;

  private final String icon;

  private final String description;

  @JsonProperty("classification")
  private BaselineStatus classification;

  private final Boolean included;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String workpackType;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Boolean deliveryModelHasActiveSchedule;
  // ↳ Apenas é incluído se workpack for do tipo Deliverable
  // ↳ Diz se o modelo da Entrega exige Cronograma ativo

  @JsonCreator
  public UpdateObject(
      final Long idWorkpack,
      final String icon,
      final String description,
      final BaselineStatus classification,
      final Boolean included) {
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

  public void setClassification(BaselineStatus newStatus) {
    this.classification = newStatus;
  }

  public Boolean isIncluded() {
    return this.included;
  }

  public String getWorkpackType() {
    return this.workpackType;
  }

  public void setWorkpackType(String workpackType) {
    this.workpackType = workpackType;
  }

  public Boolean getDeliveryModelHasActiveSchedule() {
    return this.deliveryModelHasActiveSchedule;
  }

  public void setDeliveryModelHasActiveSchedule(Boolean deliveryModelHasActiveSchedule) {
    this.deliveryModelHasActiveSchedule = deliveryModelHasActiveSchedule;
  }
}
