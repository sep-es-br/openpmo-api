package br.gov.es.openpmo.dto.process;

import br.gov.es.openpmo.utils.ApplicationMessage;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.LocalDateTime;

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
  @NotNull
  private final String actingOrganization;
  @NotNull
  private final String actingSector;
  @NotNull
  private final LocalDateTime actingDate;
  @NotNull
  private final LocalDateTime lastDispatchDate;
  @NotNull
  private final Long lengthOfStayOnSector;

  @JsonCreator
  public ProcessReadonlyDetailDto(
    final String processNumber,
    final String status,
    final String subject,
    final String currentOrganization,
    final Long lengthOfStayOn,
    final boolean priority,
    final String actingOrganization,
    final String actingSector,
    final LocalDateTime actingDate,
    final LocalDateTime lastDispatchDate,
    final Long lengthOfStayOnSector
  ) {
    this.processNumber = processNumber;
    this.status = status;
    this.subject = subject;
    this.currentOrganization = currentOrganization;
    this.lengthOfStayOn = lengthOfStayOn;
    this.priority = priority;
    this.actingOrganization = actingOrganization;
    this.actingSector = actingSector;
    this.actingDate = actingDate;
    this.lastDispatchDate = lastDispatchDate;
    this.lengthOfStayOnSector = lengthOfStayOnSector;
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

  public String getActingOrganization() {
    return actingOrganization;
  }

  public String getActingSector() {
    return actingSector;
  }

  public LocalDateTime getActingDate() {
    return actingDate;
  }

  public LocalDateTime getLastDispatchDate() {
    return lastDispatchDate;
  }

  public Long getLengthOfStayOnSector() {
    return lengthOfStayOnSector;
  }

  

}
