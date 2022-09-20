package br.gov.es.openpmo.dto.person;

import java.util.List;

public final class PersonDtoBuilder {

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

  private PersonDtoBuilder() {
  }

  public static PersonDtoBuilder aPersonDto() {
    return new PersonDtoBuilder();
  }

  public PersonDtoBuilder withId(final Long id) {
    this.id = id;
    return this;
  }

  public PersonDtoBuilder withName(final String name) {
    this.name = name;
    return this;
  }

  public PersonDtoBuilder withFullName(final String fullName) {
    this.fullName = fullName;
    return this;
  }

  public PersonDtoBuilder withPhoneNumber(final String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  public PersonDtoBuilder withAddress(final String address) {
    this.address = address;
    return this;
  }

  public PersonDtoBuilder withKey(final String key) {
    this.key = key;
    return this;
  }

  public PersonDtoBuilder withEmail(final String email) {
    this.email = email;
    return this;
  }

  public PersonDtoBuilder withContactEmail(final String contactEmail) {
    this.contactEmail = contactEmail;
    return this;
  }

  public PersonDtoBuilder withAdministrator(final boolean administrator) {
    this.administrator = administrator;
    return this;
  }

  public PersonDtoBuilder withIsUser(final boolean isUser) {
    this.isUser = isUser;
    return this;
  }

  public PersonDtoBuilder withRoles(final List<RoleResource> roles) {
    this.roles = roles;
    return this;
  }

  public PersonDto build() {
    final PersonDto personDto = new PersonDto();
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
