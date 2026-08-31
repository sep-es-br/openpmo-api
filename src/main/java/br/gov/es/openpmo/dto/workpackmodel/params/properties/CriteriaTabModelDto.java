package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.enumerator.CriteriaOperation;
import br.gov.es.openpmo.model.properties.models.CriteriaTabModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.utils.PropertyModelInstanceType;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class CriteriaTabModelDto extends TabModelDto {

  private Double weight;

  private CriteriaOperation operation;

  public static CriteriaTabModelDto of(final PropertyModel propertyModel) {
    final CriteriaTabModelDto instance = (CriteriaTabModelDto) PropertyModelDto.of(
      propertyModel,
      CriteriaTabModelDto::new
    );
    final CriteriaTabModel criteriaTabModel = (CriteriaTabModel) propertyModel;
    instance.setWeight(criteriaTabModel.getWeight());
    instance.setOperation(criteriaTabModel.getOperation());
    instance.setOrganizedProperties(Optional.ofNullable(criteriaTabModel.getOrganizedProperties())
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
