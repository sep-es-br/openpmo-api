package br.gov.es.openpmo.dto.organization;

import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.actors.OrganizationEnum;

public class OrganizationDto {
  private Long id;
  private String name;
  private String address;
  private String fullName;
  private String phoneNumber;
  private String email;
  private String contactEmail;
  private OrganizationEnum sector;
  private String website;

  public OrganizationDto() {

  }

  public OrganizationDto(final Organization organization) {
    this.id = organization.getId();
    this.name = organization.getName();
    this.fullName = organization.getFullName();
    this.address = organization.getAddress();
    this.phoneNumber = organization.getPhoneNumber();

    this.contactEmail = organization.getContactEmail();
    this.sector = organization.getSector();
    this.website = organization.getWebsite();
  }

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

  public String getAddress() {
    return this.address;
  }

  public void setAddress(final String address) {
    this.address = address;
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

  public OrganizationEnum getSector() {
    return this.sector;
  }

  public void setSector(final OrganizationEnum sector) {
    this.sector = sector;
  }

  public String getWebsite() {
    return this.website;
  }

  public void setWebsite(final String website) {
    this.website = website;
  }

}
