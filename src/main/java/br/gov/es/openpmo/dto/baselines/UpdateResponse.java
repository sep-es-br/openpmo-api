package br.gov.es.openpmo.dto.baselines;

import br.gov.es.openpmo.enumerator.BaselineStatus;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateResponse {
  private final Long idWorkpack;

  private final String icon;

  private final String description;

  @JsonProperty("classification")
  private final BaselineStatus classification;

  private final Boolean included;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String workpackType;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Boolean deliveryModelHasActiveSchedule;
  // ↳ Apenas é incluído se workpack for do tipo Deliverable
  // ↳ Diz se o modelo da Entrega exige Cronograma ativo

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Boolean deliveryHasActiveSchedule;
  // ↳ Apenas é incluído se workpack for do tipo Deliverable
  // ↳ Diz se a entrega possui um Cronograma ativo

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private BigDecimal deliverySchedulePlannedCost;
  // ↳ Apenas é incluído se workpack for do tipo Deliverable
  // ↳ Retorna o valor do escopo do cronograma (custo estimado)

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

  public String getWorkpackType() {
    return this.workpackType;
  }

  public void setWorkpackType(String workpackType) {
    this.workpackType = workpackType;
  }

  public Boolean getDeliveryModelHasActiveSchedule() {
    return this.deliveryHasActiveSchedule;
  }

  public void setDeliveryModelHasActiveSchedule(Boolean deliveryModelHasActiveSchedule) {
    this.deliveryHasActiveSchedule = deliveryModelHasActiveSchedule;
  }

  public Boolean getDeliveryHasActiveSchedule() {
    return this.deliveryHasActiveSchedule;
  }

  public void setDeliveryHasActiveSchedule(Boolean deliveryHasActiveSchedule) {
    this.deliveryHasActiveSchedule = deliveryHasActiveSchedule;
  }

  public BigDecimal getDeliverySchedulePlannedCost() {
    return this.deliverySchedulePlannedCost;
  }

  public void setDeliverySchedulePlannedCost(BigDecimal deliverySchedulePlannedCost) {
    this.deliverySchedulePlannedCost = deliverySchedulePlannedCost;
  }
}
