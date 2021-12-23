package br.gov.es.openpmo.dto.office;

import javax.validation.constraints.NotBlank;

public class OfficeStoreDto {

  @NotBlank(message = "name.not.blank")
  private String name;
  @NotBlank(message = "name.not.blank")
  private String fullName;

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

}
