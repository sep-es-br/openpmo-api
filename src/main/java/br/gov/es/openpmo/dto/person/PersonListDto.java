package br.gov.es.openpmo.dto.person;

import br.gov.es.openpmo.dto.file.AvatarDto;
import br.gov.es.openpmo.dto.person.queries.AllPersonInOfficeQuery;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import org.springframework.web.util.UriComponentsBuilder;

public class PersonListDto {

  private Long id;
  private String name;
  private String fullName;
  private String email;

  private AvatarDto avatar;

  private Boolean isUser;


  public static PersonListDto of(
    final Person person,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    boolean isUser = person.getAuthentications() != null &&
                      !person.getAuthentications().isEmpty();
    return new PersonListDto(
      person.getId(),
      person.getName(),
      person.getFullName(),
      person.getIsInContactBookOf().stream().findAny().map(IsInContactBookOf::getEmail).orElse(null),
      person.getAvatar(),
      uriComponentsBuilder,
      isUser
    );
  }

  public PersonListDto(
    final Long id,
    final String name,
    final String fullName,
    final String email,
    final File avatar,
    final UriComponentsBuilder uriComponentsBuilder,
    final boolean isUser
  ) {
    this.id = id;
    this.name = name;
    this.fullName = fullName;
    this.email = email;

    if (avatar != null) {
      this.avatar = new AvatarDto(avatar, uriComponentsBuilder);
    }
    this.isUser = isUser;
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

  public String getEmail() {
    return this.email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public AvatarDto getAvatar() {
    return this.avatar;
  }

  public void setAvatar(final AvatarDto avatar) {
    this.avatar = avatar;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public Boolean getIsUser() {
    return isUser;
  }

  public void setIsUser(Boolean isUser) {
    this.isUser = isUser;
  }

  
}
