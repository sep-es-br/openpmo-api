package br.gov.es.openpmo.dto.preprojects;

import br.gov.es.openpmo.dto.preprojects.properties.PreProjectPropertyValueDto;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class SavePreProjectCriteriaTabValuesRequest {

  @Valid
  @NotNull
  private List<PreProjectPropertyValueDto> values;

  public List<PreProjectPropertyValueDto> getValues() {
    return this.values;
  }

  public void setValues(final List<PreProjectPropertyValueDto> values) {
    this.values = values;
  }

}
