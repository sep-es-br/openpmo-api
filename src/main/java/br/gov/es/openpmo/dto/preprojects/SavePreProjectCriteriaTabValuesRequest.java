package br.gov.es.openpmo.dto.preprojects;

import br.gov.es.openpmo.dto.preprojects.properties.PreProjectPropertyValueDto;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class SavePreProjectCriteriaTabValuesRequest {

  @Valid
  @NotNull
  private List<PreProjectPropertyValueDto> values;

  @Valid
  private List<PreProjectCriteriaGroupValueDto> groups = java.util.Collections.emptyList();

  public List<PreProjectPropertyValueDto> getValues() {
    return this.values;
  }

  public void setValues(final List<PreProjectPropertyValueDto> values) {
    this.values = values;
  }

  public List<PreProjectCriteriaGroupValueDto> getGroups() {
    return this.groups;
  }

  public void setGroups(final List<PreProjectCriteriaGroupValueDto> groups) {
    this.groups = groups == null ? java.util.Collections.emptyList() : groups;
  }

}
