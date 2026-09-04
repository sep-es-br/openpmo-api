package br.gov.es.openpmo.dto.preprojects.properties;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class PreProjectCriteriaListValueDto extends PreProjectPropertyValueDto {

  @Valid
  @NotNull
  private List<PreProjectListItemDto> items;

  public List<PreProjectListItemDto> getItems() {
    return this.items;
  }

  public void setItems(final List<PreProjectListItemDto> items) {
    this.items = items;
  }

}
