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
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public boolean isAdministrator() {
    return administrator;
  }

  public void setAdministrator(boolean administrator) {
    this.administrator = administrator;
  }

  public boolean isUser() {
    return isUser;
  }

  public void setUser(boolean user) {
    isUser = user;
  }

  public List<RoleResource> getRoles() {
    return roles;
  }

  public void setRoles(List<RoleResource> roles) {
    this.roles = roles;
  }
}
