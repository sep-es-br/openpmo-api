package br.gov.es.openpmo.dto.workpackmodel.params.properties;


import br.gov.es.openpmo.model.properties.models.GroupModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.utils.PropertyModelInstanceType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GroupModelDto extends PropertyModelDto {

  private List<? extends PropertyModelDto> groupedProperties;

  public static GroupModelDto of(final PropertyModel propertyModel) {
    final GroupModelDto instance = (GroupModelDto) PropertyModelDto.of(
      propertyModel,
      GroupModelDto::new
    );
    Optional.of(propertyModel)
      .map(GroupModel.class::cast)
      .map(GroupModel::getGroupedProperties)
      .map(properties -> properties.stream().map(PropertyModelInstanceType::map).collect(Collectors.toList()))
      .ifPresent(instance::setGroupedProperties);
    return instance;
  }

  public List<? extends PropertyModelDto> getGroupedProperties() {
    return this.groupedProperties;
  }

  public void setGroupedProperties(final List<? extends PropertyModelDto> groupedProperties) {
    this.groupedProperties = groupedProperties;
  }

}
