package br.gov.es.openpmo.dto.domain;

import br.gov.es.openpmo.dto.office.OfficeDto;

public class DomainDto {

  private Long id;
  private String name;
  private String fullName;
  private OfficeDto office;
  private LocalityDto localityRoot;

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

  public OfficeDto getOffice() {
    return this.office;
  }

  public void setOffice(final OfficeDto office) {
    this.office = office;
  }

  public LocalityDto getLocalityRoot() {
    return this.localityRoot;
  }

  public void setLocalityRoot(final LocalityDto localityRoot) {
    this.localityRoot = localityRoot;
  }

}
