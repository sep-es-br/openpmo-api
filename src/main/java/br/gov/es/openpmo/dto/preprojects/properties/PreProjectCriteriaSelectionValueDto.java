package br.gov.es.openpmo.dto.preprojects.properties;

import java.util.List;
import javax.validation.constraints.NotNull;

public class PreProjectCriteriaSelectionValueDto extends PreProjectPropertyValueDto {

  @NotNull
  private List<Long> selectedOptionIds;

  public List<Long> getSelectedOptionIds() {
    return this.selectedOptionIds;
  }

  public void setSelectedOptionIds(final List<Long> selectedOptionIds) {
    this.selectedOptionIds = selectedOptionIds;
  }

}
