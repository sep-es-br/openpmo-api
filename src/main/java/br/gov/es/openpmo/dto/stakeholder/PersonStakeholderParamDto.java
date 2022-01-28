package br.gov.es.openpmo.dto.stakeholder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static br.gov.es.openpmo.utils.ApplicationMessage.CONTACT_EMAIL_NOT_NULL;

public class PersonStakeholderParamDto {

  private Long id;
  @NotNull
  private Boolean isUser;
  private String name;
  @NotNull
  @NotEmpty
  private String fullName;
  private String phoneNumber;
  private String address;
  private String email;
  @NotEmpty
  @NotNull(message = CONTACT_EMAIL_NOT_NULL)
  private String contactEmail;

  private String guid;

  public PersonStakeholderParamDto() {
  }

  public PersonStakeholderParamDto(final String fullName, final String contactEmail) {
    this.fullName = fullName;
    this.contactEmail = contactEmail;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(final String email) {
    this.email = email;
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

  public String getContactEmail() {
    return this.contactEmail;
  }

  public void setContactEmail(final String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public Boolean getIsUser() {
    return this.isUser;
  }

  public void setIsUser(final Boolean user) {
    this.isUser = user;
  }

  public String firstNameFromFullName() {
    return this.fullName.split(" ")[0];
  }

  public String getGuid() {
    return this.guid;
  }

  public void setGuid(final String guid) {
    this.guid = guid;
  }
}
