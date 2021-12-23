package br.gov.es.openpmo.dto.process;

import br.gov.es.openpmo.utils.ApplicationMessage;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ProcessCreateDto {
  @NotNull(message = ApplicationMessage.ID_WORKPACK_NOT_NULL)
  private final Long idWorkpack;
  @NotNull
  @NotEmpty
  private final String name;
  @NotNull
  @NotEmpty
  private final String note;

  private final ProcessReadonlyDetailDto readonlyDetail;

  @JsonCreator
  public ProcessCreateDto(
    final Long idWorkpack,
    final String name,
    final String note,
    final String processNumber,
    final String status,
    final String subject,
    final String currentOrganization,
    final long lengthOfStayOn,
    final boolean priority
  ) {
    this.idWorkpack = idWorkpack;
    this.name = name;
    this.note = note;
    this.readonlyDetail = new ProcessReadonlyDetailDto(
      processNumber,
      status,
      subject,
      currentOrganization,
      lengthOfStayOn,
      priority
    );
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public String getName() {
    return this.name;
  }

  public String getNote() {
    return this.note;
  }

  public ProcessReadonlyDetailDto getReadonlyDetail() {
    return this.readonlyDetail;
  }
}
