package br.gov.es.openpmo.dto.ccbmembers;

import br.gov.es.openpmo.dto.person.RoleResource;

import java.util.List;

public class PersonRequest {

  private Long id;
  private String name;
  private String fullName;
  private String email;
  private String contactEmail;
  private String phoneNumber;
  private String address;
  private boolean administrator;
  private boolean isUser;
  private List<RoleResource> roles;

  public PersonRequest() {
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

  public boolean isAdministrator() {
    return this.administrator;
  }

  public void setAdministrator(final boolean administrator) {
    this.administrator = administrator;
  }

  public boolean isUser() {
    return this.isUser;
  }

  public void setUser(final boolean user) {
    this.isUser = user;
  }

  public List<RoleResource> getRoles() {
    return this.roles;
  }

  public void setRoles(final List<RoleResource> roles) {
    this.roles = roles;
  }
}
