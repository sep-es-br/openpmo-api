package br.gov.es.openpmo.dto.organization;

import br.gov.es.openpmo.model.actors.OrganizationEnum;
import br.gov.es.openpmo.utils.ApplicationMessage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class OrganizationUpdateDto {

  @NotNull(message = ApplicationMessage.ID_NOT_NULL)
  private Long id;
  @NotBlank(message = ApplicationMessage.NAME_NOT_BLANK)
  private String name;
  @NotBlank(message = ApplicationMessage.FULLNAME_NOT_BLANK)
  private String fullName;
  private String phoneNumber;
  private String address;
  private String email;
  private String contactEmail;
  private String website;
  private OrganizationEnum sector;

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

  public String getPhoneNumber() {
    return this.phoneNumber;
  }

  public void setPhoneNumber(final String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getAddress() {
    return this.address;
  }

  public void setAddress(final String address) {
    this.address = address;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public String getContactEmail() {
    return this.contactEmail;
  }

  public void setContactEmail(final String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public String getWebsite() {
    return this.website;
  }

  public void setWebsite(final String website) {
    this.website = website;
  }

  public OrganizationEnum getSector() {
    return this.sector;
  }

  public void setSector(final OrganizationEnum sector) {
    this.sector = sector;
  }

}
