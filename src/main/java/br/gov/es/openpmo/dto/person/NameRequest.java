package br.gov.es.openpmo.dto.person;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class NameRequest {

  @NotNull
  @NotEmpty
  @NotBlank
  private final String name;

  @JsonCreator
  public NameRequest(final String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

}
