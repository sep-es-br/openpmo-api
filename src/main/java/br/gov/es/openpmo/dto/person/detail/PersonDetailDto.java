package br.gov.es.openpmo.dto.person.detail;

import br.gov.es.openpmo.dto.file.AvatarDto;
import br.gov.es.openpmo.dto.person.detail.permissions.OfficePermissionDetailDto;
import br.gov.es.openpmo.dto.person.queries.PersonDetailQuery;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

import static br.gov.es.openpmo.utils.ApplicationMessage.CONTACT_DATA_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.PERSON_NOT_FOUND;

public class PersonDetailDto {

  private Long id;
  private String name;
  private String fullName;
  private String email;
  private String contactEmail;
  private String phoneNumber;
  private String address;
  private Boolean isUser;
  private AvatarDto avatar;
  private OfficePermissionDetailDto officePermission;

  public PersonDetailDto() {
  }

  public PersonDetailDto(
    final PersonDetailQuery query,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    this.setPersonData(query);
    this.setContactData(query);
    this.setUserAuthenticationData(query);
    this.setUserAvatar(query, uriComponentsBuilder);
  }

  private void setUserAvatar(
    final PersonDetailQuery query,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    if(query.getAvatar() != null) {
      this.avatar = new AvatarDto(query.getAvatar(), uriComponentsBuilder);
    }
  }

  private void setUserAuthenticationData(final PersonDetailQuery query) {
    final IsAuthenticatedBy authentication = query.getAuthentication();
    this.isUser = authentication != null;
    if(authentication != null) {
      this.email = authentication.getEmail();
    }
  }

  private void setPersonData(final PersonDetailQuery query) {
    final Person person = query.getPerson();
    Objects.requireNonNull(person, PERSON_NOT_FOUND);
    this.id = person.getId();
    this.name = person.getName();
    this.fullName = person.getFullName();
  }

  private void setContactData(final PersonDetailQuery query) {
    final IsInContactBookOf contact = query.getContact();
    Objects.requireNonNull(contact, CONTACT_DATA_NOT_FOUND);

    this.phoneNumber = contact.getPhoneNumber();
    this.address = contact.getAddress();
    this.contactEmail = contact.getEmail();
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

  public OfficePermissionDetailDto getOfficePermission() {
    return this.officePermission;
  }

  public void setOfficePermission(final OfficePermissionDetailDto officePermission) {
    this.officePermission = officePermission;
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

  public Boolean getIsUser() {
    return this.isUser;
  }

  public void setIsUser(final Boolean user) {
    this.isUser = user;
  }

  public AvatarDto getAvatar() {
    return this.avatar;
  }

  public void setAvatar(final AvatarDto avatar) {
    this.avatar = avatar;
  }

}
