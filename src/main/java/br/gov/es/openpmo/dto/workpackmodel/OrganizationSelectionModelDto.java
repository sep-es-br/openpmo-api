package br.gov.es.openpmo.dto.workpackmodel;

import java.util.List;

public class OrganizationSelectionModelDto extends PropertyModelDto {

  private boolean multipleSelection;

  private List<Long> defaults;

  public boolean isMultipleSelection() {
    return this.multipleSelection;
  }

  public void setMultipleSelection(final boolean multipleSelection) {
    this.multipleSelection = multipleSelection;
  }

  public List<Long> getDefaults() {
    return this.defaults;
  }

  public void setDefaults(final List<Long> defaults) {
    this.defaults = defaults;
  }
}
