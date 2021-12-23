package br.gov.es.openpmo.dto.administrator;


import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;

import static br.gov.es.openpmo.utils.ApplicationMessage.AUTH_SERVICE_NOT_FOUND;

public class AdministratorDto {

  private final Long id;
  private final String name;
  private final String fullName;
  private final String email;
  private final Boolean administrator;

  public AdministratorDto(final Long id, final String name, final String fullName, final String email, final Boolean administrator) {
    this.id = id;
    this.name = name;
    this.fullName = fullName;
    this.email = email;
    this.administrator = administrator;
  }

  public AdministratorDto(final Person person, final String serverName) {
    this.id = person.getId();
    this.name = person.getName();
    this.fullName = person.getFullName();
    this.administrator = person.getAdministrator();
    final IsAuthenticatedBy isAuthenticatedBy = person.findAuthenticationDataBy(serverName)
      .orElseThrow(() -> new IllegalStateException(AUTH_SERVICE_NOT_FOUND));
    this.email = isAuthenticatedBy.getEmail();
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

  public String getEmail() {
    return this.email;
  }

  public Boolean getAdministrator() {
    return this.administrator;
  }
}
