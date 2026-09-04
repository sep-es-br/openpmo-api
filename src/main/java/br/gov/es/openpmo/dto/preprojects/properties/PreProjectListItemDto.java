package br.gov.es.openpmo.dto.preprojects.properties;

import br.gov.es.openpmo.model.properties.ListItem;

public class PreProjectListItemDto {

  private Long id;

  private String foreignKey;

  private String label;

  public static PreProjectListItemDto of(final ListItem item) {
    final PreProjectListItemDto dto = new PreProjectListItemDto();
    dto.id = item.getId();
    dto.foreignKey = item.getForeignKey();
    dto.label = item.getLabel();
    return dto;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getForeignKey() {
    return this.foreignKey;
  }

  public void setForeignKey(final String foreignKey) {
    this.foreignKey = foreignKey;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

}
