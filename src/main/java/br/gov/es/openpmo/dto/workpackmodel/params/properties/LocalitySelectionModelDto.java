package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import javax.validation.constraints.NotNull;
import java.util.List;

public class LocalitySelectionModelDto extends PropertyModelDto {

  private boolean multipleSelection;
  @NotNull
  private Long idDomain;
  private List<Long> defaults;

  public boolean isMultipleSelection() {
    return this.multipleSelection;
  }

  public void setMultipleSelection(final boolean multipleSelection) {
    this.multipleSelection = multipleSelection;
  }

  public Long getIdDomain() {
    return this.idDomain;
  }

  public void setIdDomain(final Long idDomain) {
    this.idDomain = idDomain;
  }

  public List<Long> getDefaults() {
    return this.defaults;
  }

  public void setDefaults(final List<Long> defaults) {
    this.defaults = defaults;
  }
}
