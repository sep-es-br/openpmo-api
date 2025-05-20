package br.gov.es.openpmo.dto.process;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProcessUpdateDto {

  @NotNull
  private final Long id;
  @NotNull
  @NotEmpty
  @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
  private final String name;

  @Size(max = 600, message = "A anotação deve ter no máximo 600 caracteres")
  private final String note;

  @JsonCreator
  public ProcessUpdateDto(
    final Long id,
    final String name,
    final String note
  ) {
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
