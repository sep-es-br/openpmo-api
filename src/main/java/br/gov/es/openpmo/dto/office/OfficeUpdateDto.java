package br.gov.es.openpmo.dto.office;

import br.gov.es.openpmo.utils.ApplicationMessage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class OfficeUpdateDto {

  @NotNull(message = ApplicationMessage.ID_NOT_NULL)
  private Long id;
  @NotBlank(message = "name.not.blank")
  private String name;
  @NotBlank(message = "name.not.blank")
  private String fullName;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

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
