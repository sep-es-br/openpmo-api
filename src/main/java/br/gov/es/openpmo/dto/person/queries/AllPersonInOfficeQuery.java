package br.gov.es.openpmo.dto.person.queries;

import br.gov.es.openpmo.model.actors.File;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class AllPersonInOfficeQuery {

  private final Long id;
  private final String name;
  private final String fullName;
  private final File avatar;
  private final String email;

  public AllPersonInOfficeQuery(
    final Long id,
    final String name,
    final String fullName,
    final File avatar,
    final String email
  ) {
    this.id = id;
    this.name = name;
    this.fullName = fullName;
    this.avatar = avatar;
    this.email = email;
  }

  public String getEmail() {
    return this.email;
  }

  public File getAvatar() {
    return this.avatar;
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

}
