package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.enumerator.CriteriaOperation;
import br.gov.es.openpmo.model.properties.models.CriteriaGroupModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.utils.PropertyModelInstanceType;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class CriteriaGroupModelDto extends GroupModelDto {

  private Double weight;

  private CriteriaOperation operation;

  public static CriteriaGroupModelDto of(final PropertyModel propertyModel) {
    final CriteriaGroupModelDto instance = (CriteriaGroupModelDto) PropertyModelDto.of(
      propertyModel,
      CriteriaGroupModelDto::new
    );
    final CriteriaGroupModel criteriaGroupModel = (CriteriaGroupModel) propertyModel;
    instance.setWeight(criteriaGroupModel.getWeight());
    instance.setOperation(criteriaGroupModel.getOperation());
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

}
