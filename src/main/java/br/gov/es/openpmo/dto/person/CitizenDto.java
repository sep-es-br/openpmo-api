package br.gov.es.openpmo.dto.person;

import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CitizenDto {

  private Long id;
  private String name;
  private String fullName;
  private String phoneNumber;
  private String address;
  private String email;
  private String contactEmail;
  private boolean administrator;
  private Boolean isUser;
  private List<RoleResource> roles;

  public CitizenDto() {
    this.roles = new ArrayList<>();
    this.roles.add(RoleResource.citizen());
  }

  public static CitizenDto from(final Person person, final Set<IsInContactBookOf> contacts) {
    final Optional<IsInContactBookOf> maybeContact = contacts.stream()
      .filter(contact -> contact.getPersonId()
        .equals(person.getId())).findFirst();
    return from(person, maybeContact, Optional.empty());
  }

  public static CitizenDto from(
    final Person person,
    final Optional<IsInContactBookOf> maybeContact,
    final Optional<IsAuthenticatedBy> maybeAuthentication
  ) {
    final CitizenDto dto = from(person);

    final boolean isUser = (person.getAuthentications() != null &&
                            !person.getAuthentications().isEmpty()) || !(person.getAuthentications() == null);

    dto.setIsUser(isUser);
    maybeContact.ifPresent(contact -> {
      dto.setContactEmail(contact.getEmail());
      dto.setAddress(contact.getAddress());
      dto.setPhoneNumber(contact.getPhoneNumber());
    });
    maybeAuthentication.ifPresent(auth -> {
      dto.setEmail(auth.getEmail());
    });
    return dto;
  }

  private static CitizenDto from(final Person person) {
    final CitizenDto dto = new CitizenDto();
    dto.setId(person.getId());
    dto.setName(person.getName());
    dto.setFullName(person.getFullName());
    return dto;
  }

  public static CitizenDto from(
    final Person person,
    final Optional<IsInContactBookOf> maybeContact
  ) {
    return from(person, maybeContact, Optional.empty());
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
    return this.roles;
  }

  public void setRoles(final List<RoleResource> roles) {
    this.roles = roles;
  }

  public boolean haveAnyContactInformationData() {
    return this.getContactEmail() != null;
  }

  public String getContactEmail() {
    return this.contactEmail;
  }

  public void setContactEmail(final String contactEmail) {
    this.contactEmail = contactEmail;
  }
}
