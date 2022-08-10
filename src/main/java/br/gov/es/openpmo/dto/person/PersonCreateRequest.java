package br.gov.es.openpmo.dto.person;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PersonCreateRequest {

  @NotNull
  @NotEmpty
  private final String name;
  @NotNull
  @NotEmpty
  private final String fullName;
  @NotNull
  @NotEmpty
  private final String key;
  @NotNull
  @NotEmpty
  private final String email;
  @NotNull
  private final Boolean administrator;

  public PersonCreateRequest(
    final String name,
    final String fullName,
    final String key,
    final String email,
    final boolean administrator
  ) {
    this.name = name;
    this.fullName = fullName;
    this.key = key;
    this.email = email;
    this.administrator = administrator;
  }

  public String getKey() {
    return key;
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
