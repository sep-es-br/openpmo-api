package br.gov.es.openpmo.dto.person;

import br.gov.es.openpmo.dto.file.AvatarDto;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PersonGetByIdDto {

  private Long id;

  private String name;

  private String fullName;

  private String phoneNumber;

  private String address;

  private String contactEmail;

  private boolean administrator;

  private AvatarDto avatar;

  @JsonProperty("isCcbMember")
  private Boolean isCcbMember;

  private List<String> roles = Collections.singletonList("citizen");

  public static PersonGetByIdDto from(
    final Person person,
    final Optional<IsInContactBookOf> maybeContact,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    final PersonGetByIdDto dto = from(person);
    maybeSetContact(maybeContact, dto);
    if(person.getAvatar() != null) {
      dto.setAvatar(new AvatarDto(person.getAvatar(), uriComponentsBuilder));
    }
    return dto;
  }

  private static void maybeSetContact(final Optional<IsInContactBookOf> maybeContact, final PersonGetByIdDto dto) {
    maybeContact.ifPresent(contact -> {
      dto.setContactEmail(contact.getEmail());
      dto.setAddress(contact.getAddress());
      dto.setPhoneNumber(contact.getPhoneNumber());
    });
  }

  private static PersonGetByIdDto from(final Person person) {
    final PersonGetByIdDto dto = new PersonGetByIdDto();
    dto.setId(person.getId());
    dto.setName(person.getName());
    dto.setFullName(person.getFullName());
    return dto;
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

  public boolean isAdministrator() {
    return this.administrator;
  }

  public void setAdministrator(final boolean administrator) {
    this.administrator = administrator;
  }

  public AvatarDto getAvatar() {
    return this.avatar;
  }

  public void setAvatar(final AvatarDto avatar) {
    this.avatar = avatar;
  }

  public List<String> getRoles() {
    return this.roles;
  }

  public void setRoles(final List<String> roles) {
    this.roles = roles;
  }

  public Boolean getCcbMember() {
    return this.isCcbMember;
  }

  public void setCcbMember(final Boolean CCBMember) {
    this.isCcbMember = CCBMember;
  }

}
