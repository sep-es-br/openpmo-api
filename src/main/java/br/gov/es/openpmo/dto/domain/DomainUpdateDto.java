package br.gov.es.openpmo.dto.domain;

import br.gov.es.openpmo.utils.ApplicationMessage;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class DomainUpdateDto {

  @NotNull(message = ApplicationMessage.ID_NOT_NULL)
  private Long id;

  @NotEmpty(message = ApplicationMessage.NAME_NOT_BLANK)
  private String name;

  @NotEmpty(message = ApplicationMessage.FULLNAME_NOT_BLANK)
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
