package br.gov.es.openpmo.dto.process;

import br.gov.es.openpmo.utils.ApplicationMessage;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ProcessReadonlyDetailDto {

  @NotEmpty
  @NotNull(message = ApplicationMessage.PROCESS_NUMBER_NOT_NULL)
  private final String processNumber;
  @NotEmpty
  @NotNull
  private final String status;
  @NotEmpty
  @NotNull
  private final String subject;
  @NotEmpty
  @NotNull
  private final String currentOrganization;
  @NotNull
  private final Long lengthOfStayOn;
  @NotNull
  private final Boolean priority;

  @JsonCreator
  public ProcessReadonlyDetailDto(
    final String processNumber,
    final String status,
    final String subject,
    final String currentOrganization,
    final Long lengthOfStayOn,
    final boolean priority
  ) {
    this.processNumber = processNumber;
    this.status = status;
    this.subject = subject;
    this.currentOrganization = currentOrganization;
    this.lengthOfStayOn = lengthOfStayOn;
    this.priority = priority;
  }

  public String getProcessNumber() {
    return this.processNumber;
  }

  public String getStatus() {
    return this.status;
  }

  public String getSubject() {
    return this.subject;
  }

  public String getCurrentOrganization() {
    return this.currentOrganization;
  }

  public Long getLengthOfStayOn() {
    return this.lengthOfStayOn;
  }

  public Boolean getPriority() {
    return this.priority;
  }

}
