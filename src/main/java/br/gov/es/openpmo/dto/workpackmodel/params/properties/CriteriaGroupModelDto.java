package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.enumerator.CriteriaOperation;
import br.gov.es.openpmo.model.properties.models.CriteriaGroupModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.utils.PropertyModelInstanceType;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

public class CriteriaGroupModelDto extends GroupModelDto {

  @NotNull
  private Double weight;

  @NotNull
  private CriteriaOperation operation;

  private boolean enablementKey;

  private Double disabledValue;

  private String legend;

  public static CriteriaGroupModelDto of(final PropertyModel propertyModel) {
    final CriteriaGroupModelDto instance = (CriteriaGroupModelDto) PropertyModelDto.of(
      propertyModel,
      CriteriaGroupModelDto::new
    );
    final CriteriaGroupModel criteriaGroupModel = (CriteriaGroupModel) propertyModel;
    instance.setWeight(criteriaGroupModel.getWeight());
    instance.setOperation(criteriaGroupModel.getOperation());
    instance.setEnablementKey(criteriaGroupModel.isEnablementKey());
    if (criteriaGroupModel.isEnablementKey()) {
      instance.setDisabledValue(criteriaGroupModel.getDisabledValue());
      instance.setLegend(criteriaGroupModel.getLegend());
    }
    instance.setGroupedProperties(Optional.ofNullable(criteriaGroupModel.getGroupedProperties())
      .map(properties -> properties.stream().map(PropertyModelInstanceType::map).collect(Collectors.toList()))
      .orElse(Collections.emptyList()));
    return instance;
  }

  public Double getWeight() {
    return this.weight;
  }

  public void setWeight(final Double weight) {
    this.weight = weight;
  }

  public CriteriaOperation getOperation() {
    return this.operation;
  }

  public void setOperation(final CriteriaOperation operation) {
    this.operation = operation;
  }

  public boolean isEnablementKey() {
    return this.enablementKey;
  }

  public void setEnablementKey(final boolean enablementKey) {
    this.enablementKey = enablementKey;
  }

  public Double getDisabledValue() {
    return this.disabledValue;
  }

  public void setDisabledValue(final Double disabledValue) {
    this.disabledValue = disabledValue;
  }

  public String getLegend() {
    return this.legend;
  }

  public void setLegend(final String legend) {
    this.legend = legend;
  }

}
