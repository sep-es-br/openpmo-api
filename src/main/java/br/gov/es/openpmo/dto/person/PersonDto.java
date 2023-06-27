package br.gov.es.openpmo.dto.person;

import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PersonDto {

  private final List<RoleResource> roles = new ArrayList<>();
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
  private String guid;

  public PersonDto() {
    this.roles.add(RoleResource.citizen());
  }

  public static PersonDto from(
    final Person person,
    final Set<IsInContactBookOf> contacts
  ) {
    final Optional<IsInContactBookOf> maybeContact = contacts.stream()
      .filter(contact -> contact.getPersonId()
        .equals(person.getId())).findFirst();
    return from(person, maybeContact, Optional.empty());
  }

  public static PersonDto from(
    final Person person,
    final Optional<? extends IsInContactBookOf> maybeContact,
    final Optional<? extends IsAuthenticatedBy> maybeAuthentication
  ) {
    final PersonDto dto = from(person);
    final boolean isUser;

    if(person.getAuthentications() == null) {
      isUser = false;
    }
    else {
      isUser = !person.getAuthentications().isEmpty();
    }

    dto.setIsUser(isUser);
    maybeContact.ifPresent(contact -> {
      dto.setContactEmail(contact.getEmail());
      dto.setAddress(contact.getAddress());
      dto.setPhoneNumber(contact.getPhoneNumber());
    });
    maybeAuthentication.ifPresent(auth -> {
      dto.setKey(auth.getKey());
      dto.setEmail(auth.getEmail());
    });
    return dto;
  }

  private static PersonDto from(final Person person) {
    final PersonDto dto = new PersonDto();
    dto.setId(person.getId());
    dto.setName(person.getName());
    dto.setFullName(person.getFullName());
    dto.setAdministrator(person.getAdministrator());
    return dto;
  }

  public static PersonDto from(
    final Person person,
    final Optional<? extends IsInContactBookOf> maybeContact
  ) {
    return from(person, maybeContact, Optional.empty());
  }

  public void addAllRoles(final Collection<? extends RoleResource> roles) {
    Optional.ofNullable(roles).ifPresent(this.roles::addAll);
  }

  public void addRole(final RoleResource role) {
    this.roles.add(role);
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

  public String getKey() {
    return this.key;
  }

  public void setKey(final String key) {
    this.key = key;
  }

  public Boolean getUser() {
    return this.isUser;
  }

  public void setUser(final Boolean user) {
    this.isUser = user;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public boolean isAdministrator() {
    return this.administrator;
  }

  public void setAdministrator(final boolean administrator) {
    this.administrator = administrator;
  }

  public Boolean getIsUser() {
    return this.isUser;
  }

  public void setIsUser(final Boolean user) {
    this.isUser = user;
  }

  public List<RoleResource> getRoles() {
    this.roles.sort((a, b) -> a.getRole().compareToIgnoreCase(b.getRole()));
    return Collections.unmodifiableList(this.roles);
  }

  public boolean hasAnyContactInformationData() {
    return this.contactEmail != null;
  }

  public String getContactEmail() {
    return this.contactEmail;
  }

  public void setContactEmail(final String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public String getGuid() {
    return this.guid;
  }

  public void setGuid(final String guid) {
    this.guid = guid;
  }

}
