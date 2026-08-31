package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.TabModel;
import br.gov.es.openpmo.utils.PropertyModelInstanceType;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TabModelDto extends PropertyModelDto {

  private List<? extends PropertyModelDto> organizedProperties;

  public static TabModelDto of(final PropertyModel propertyModel) {
    final TabModelDto instance = (TabModelDto) PropertyModelDto.of(propertyModel, TabModelDto::new);
    instance.setOrganizedProperties(Optional.of(propertyModel)
      .map(TabModel.class::cast)
      .map(TabModel::getOrganizedProperties)
      .map(properties -> properties.stream().map(PropertyModelInstanceType::map).collect(Collectors.toList()))
      .orElse(Collections.emptyList()));
    return instance;
  }

  public List<? extends PropertyModelDto> getOrganizedProperties() {
    return this.organizedProperties;
  }

  public void setOrganizedProperties(final List<? extends PropertyModelDto> organizedProperties) {
    this.organizedProperties = organizedProperties;
  }

}
