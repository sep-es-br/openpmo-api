package br.gov.es.openpmo.dto.workpackmodel.params.properties;


import java.util.List;

public class GroupModelDto extends PropertyModelDto {

  private List<? extends PropertyModelDto> groupedProperties;

  public List<? extends PropertyModelDto> getGroupedProperties() {
    return this.groupedProperties;
  }

  public void setGroupedProperties(final List<? extends PropertyModelDto> groupedProperties) {
    this.groupedProperties = groupedProperties;
  }
}
