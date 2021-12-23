package br.gov.es.openpmo.dto.workpack;


import java.util.List;

public class GroupDto extends PropertyDto {

  private List<? extends PropertyDto> groupedProperties;

  public List<? extends PropertyDto> getGroupedProperties() {
    return this.groupedProperties;
  }

  public void setGroupedProperties(final List<? extends PropertyDto> groupedProperties) {
    this.groupedProperties = groupedProperties;
  }

}
