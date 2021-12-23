package br.gov.es.openpmo.dto.domain;

import br.gov.es.openpmo.utils.ApplicationMessage;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class DomainStoreDto {

  @NotEmpty(message = ApplicationMessage.NAME_NOT_BLANK)
  private String name;

  @NotEmpty(message = ApplicationMessage.FULLNAME_NOT_BLANK)
  private String fullName;

  @NotNull(message = ApplicationMessage.OFFICE_NOT_NULL)
  private Long idOffice;

  @NotNull(message = ApplicationMessage.LOCALITY_ROOT_NOT_NULL)
  private LocalityStoreDto localityRoot;

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

  public Long getIdOffice() {
    return this.idOffice;
  }

  public void setIdOffice(final Long idOffice) {
    this.idOffice = idOffice;
  }

  public LocalityStoreDto getLocalityRoot() {
    return this.localityRoot;
  }

  public void setLocalityRoot(final LocalityStoreDto localityRoot) {
    this.localityRoot = localityRoot;
  }
}
