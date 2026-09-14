package br.gov.es.openpmo.dto.preprojects;

import br.gov.es.openpmo.dto.preprojects.properties.PreProjectPropertyValueDto;

public class PreProjectCriteriaGroupValueDto extends PreProjectPropertyValueDto {

  private Boolean active;

  public Boolean getActive() {
    return this.active;
  }

  public void setActive(final Boolean active) {
    this.active = active;
  }
}
