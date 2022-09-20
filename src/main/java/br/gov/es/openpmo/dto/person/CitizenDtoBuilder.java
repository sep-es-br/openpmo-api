package br.gov.es.openpmo.dto.person;

import java.util.List;

public final class CitizenDtoBuilder {

  private Long id;
  private String name;
  private String fullName;
  private String phoneNumber;
  private String address;
  private String key;
  private String email;
  private String contactEmail;
  private boolean administrator;
  private Boolean isUser;
  private List<RoleResource> roles;

  private CitizenDtoBuilder() {
  }

  public static CitizenDtoBuilder aCitizenDto() {
    return new CitizenDtoBuilder();
  }

  public CitizenDtoBuilder withId(final Long id) {
    this.id = id;
    return this;
  }

  public CitizenDtoBuilder withName(final String name) {
    this.name = name;
    return this;
  }

  public CitizenDtoBuilder withFullName(final String fullName) {
    this.fullName = fullName;
    return this;
  }

  public CitizenDtoBuilder withPhoneNumber(final String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  public CitizenDtoBuilder withAddress(final String address) {
    this.address = address;
    return this;
  }

  public CitizenDtoBuilder withKey(final String key) {
    this.key = key;
    return this;
  }

  public CitizenDtoBuilder withEmail(final String email) {
    this.email = email;
    return this;
  }

  public CitizenDtoBuilder withContactEmail(final String contactEmail) {
    this.contactEmail = contactEmail;
    return this;
  }

  public CitizenDtoBuilder withAdministrator(final boolean administrator) {
    this.administrator = administrator;
    return this;
  }

  public CitizenDtoBuilder withIsUser(final boolean isUser) {
    this.isUser = isUser;
    return this;
  }

  public CitizenDtoBuilder withRoles(final List<RoleResource> roles) {
    this.roles = roles;
    return this;
  }

  public CitizenDto build() {
    final CitizenDto personDto = new CitizenDto();
    personDto.setId(this.id);
    personDto.setName(this.name);
    personDto.setFullName(this.fullName);
    personDto.setPhoneNumber(this.phoneNumber);
    personDto.setAddress(this.address);
    personDto.setKey(this.key);
    personDto.setEmail(this.email);
    personDto.setContactEmail(this.contactEmail);
    personDto.setAdministrator(this.administrator);
    personDto.setIsUser(this.isUser);
    personDto.getRoles().addAll(this.roles);
    return personDto;
  }

}
