package br.gov.es.openpmo.dto.person;

import br.gov.es.openpmo.dto.file.AvatarDto;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.web.util.UriComponentsBuilder;

public class PersonGetByIdDto {

  private Long id;

  private String name;

  private String fullName;

  private String phoneNumber;

  private String address;

  private String contactEmail;

  private boolean administrator;

  private AvatarDto avatar;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private WorkLocalResponse workLocal;

  @JsonProperty("isCcbMember")
  private Boolean isCcbMember;

  private List<String> roles = Collections.singletonList("citizen");
  
  private String key;

  public static PersonGetByIdDto from(
    final Person person,
    final Optional<IsInContactBookOf> maybeContact,
    final UriComponentsBuilder uriComponentsBuilder,
    final String serverName
  ) {
    final PersonGetByIdDto dto = from(person, serverName);
    maybeSetContact(maybeContact, dto);
    if (person.getAvatar() != null) {
      dto.setAvatar(new AvatarDto(person.getAvatar(), uriComponentsBuilder));
    }
    return dto;
  }

  private static void maybeSetContact(
    final Optional<IsInContactBookOf> maybeContact,
    final PersonGetByIdDto dto
  ) {
    maybeContact.ifPresent(contact -> {
      dto.setContactEmail(contact.getEmail());
      dto.setAddress(contact.getAddress());
      dto.setPhoneNumber(contact.getPhoneNumber());
    });
  }

  public void setAvatar(final AvatarDto avatar) {
    this.avatar = avatar;
  }

  public void setPhoneNumber(final String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void setAddress(final String address) {
    this.address = address;
  }

  private static PersonGetByIdDto from(final Person person, final String serverName) {
    final PersonGetByIdDto dto = new PersonGetByIdDto();
    dto.setId(person.getId());
    dto.setName(person.getName());
    dto.setFullName(person.getFullName());
    dto.setAdministrator(person.getAdministrator());
    dto.setWorkLocal(WorkLocalResponse.from(person).orNull());
    dto.setKey(person.findAuthenticationDataBy(serverName).map(IsAuthenticatedBy::getKey).orElse(null));
    
    
    return dto;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public void setContactEmail(final String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public String getPhoneNumber() {
    return this.phoneNumber;
  }

  public String getAddress() {
    return this.address;
  }

  public String getContactEmail() {
    return this.contactEmail;
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

  public WorkLocalResponse getWorkLocal() {
    return workLocal;
  }

  public void setWorkLocal(WorkLocalResponse workLocal) {
    this.workLocal = workLocal;
  }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
  
  
}
