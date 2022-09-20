package br.gov.es.openpmo.dto.ccbmembers;

import br.gov.es.openpmo.dto.person.RoleResource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PersonResponse {

  private final Collection<RoleResource> roles = new ArrayList<>();
  private Long id;
  private String name;
  private String fullName;
  private String address;
  private String contactEmail;
  private String phoneNumber;

  public PersonResponse() {
    this.roles.add(RoleResource.citizen());
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

  public String getPhoneNumber() {
    return this.phoneNumber;
  }

  public void setPhoneNumber(final String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public Collection<RoleResource> getRoles() {
    return Collections.unmodifiableCollection(this.roles);
  }

  public void addAllRoles(final Collection<? extends RoleResource> roles) {
    this.roles.addAll(roles);
  }

}
