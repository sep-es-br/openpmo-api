package br.gov.es.openpmo.dto.organization;

import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.actors.OrganizationEnum;
import br.gov.es.openpmo.model.relations.OrganizationOfficeRelationship;

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
  private String integration;
  private String suffix;
  private String guid;

  public OrganizationDto() {

  }

  public OrganizationDto(final Organization organization) {
    this.id = organization.getId();
    this.name = organization.getName();
    this.fullName = organization.getFullName();
    this.sector = organization.getSector();
    this.integration = organization.getIntegration();
    this.suffix = organization.getSuffix();
    this.guid = organization.getGuid();

    if (organization.getOrganizationOffices() != null
            && !organization.getOrganizationOffices().isEmpty()) {

      OrganizationOfficeRelationship relationship =
              organization.getOrganizationOffices().get(0);

      this.phoneNumber = relationship.getPhoneNumber();
      this.contactEmail = relationship.getContactEmail();
      this.address = relationship.getAddress();
      this.website = relationship.getWebsite();
    }
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

  public String getIntegration() {
    return this.integration;
  }

  public void setIntegration(final String integration) {
    this.integration = integration;
  }

  public String getSuffix() {
    return this.suffix;
  }

  public void setSuffix(final String suffix) {
    this.suffix = suffix;
  }

  public String getGuid() {
    return this.guid;
  }

  public void setGuid(final String guid) {
    this.guid = guid;
  }

}
