package br.gov.es.openpmo.dto.process;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ProcessUpdateDto {
  @NotNull
  private final Long id;
  @NotNull
  @NotEmpty
  private final String name;

  private final String note;

  @JsonCreator
  public ProcessUpdateDto(final Long id, final String name, final String note) {
    this.id = id;
    this.name = name;
    this.note = note;
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getNote() {
    return this.note;
  }
}
