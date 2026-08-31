package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.properties.models.CriteriaListModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;

public class CriteriaListModelDto extends ListModelDto {

  private Double weight;

  private Double itemValue;

  public static CriteriaListModelDto of(final PropertyModel propertyModel) {
    final CriteriaListModelDto instance = (CriteriaListModelDto) PropertyModelDto.of(
      propertyModel,
      CriteriaListModelDto::new
    );
    final CriteriaListModel criteriaListModel = (CriteriaListModel) propertyModel;
    instance.setWeight(criteriaListModel.getWeight());
    instance.setItemValue(criteriaListModel.getItemValue());
    return instance;
  }

  public Double getWeight() {
    return this.weight;
  }

  public void setWeight(final Double weight) {
    this.weight = weight;
  }

  public Double getItemValue() {
    return this.itemValue;
  }

  public void setItemValue(final Double itemValue) {
    this.itemValue = itemValue;
  }

}
