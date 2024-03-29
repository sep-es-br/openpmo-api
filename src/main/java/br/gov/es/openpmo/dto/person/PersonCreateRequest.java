package br.gov.es.openpmo.dto.person;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PersonCreateRequest {

  @NotNull
  @NotEmpty
  @NotBlank
  private final String name;
  @NotNull
  @NotEmpty
  @NotBlank
  private final String fullName;
  @NotNull
  @NotEmpty
  @NotBlank
  private final String key;
  @NotNull
  @NotEmpty
  @NotBlank
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
    return this.key;
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
