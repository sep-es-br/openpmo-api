package br.gov.es.openpmo.dto.process;

import br.gov.es.pmo.administrative_process_core.model.AdministrativeProcessDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProcessFromEDocsDto {

  private final String processNumber;
  private final String status;
  private final String subject;
  private final String currentOrganization;
  private final long lengthOfStayOn;
  private final boolean priority;
  private final String actingOrganization;
  private final String actingSector;
  private final LocalDateTime actingDate;
  private final LocalDateTime lastDispatchDate;
  private final long lengthOfStayOnSector;
  private final List<ProcessTimelineDto> history;

  private ProcessFromEDocsDto(final AdministrativeProcessDto process) {
    this.processNumber = process.getProcessNumber();
    this.status = process.getStatus();
    this.subject = process.getSubject();
    this.currentOrganization = process.getCurrentOrganization();
    this.lengthOfStayOn = process.getLengthOfStayOn();
    this.priority = process.isPriority();
    this.actingOrganization = process.getActingOrganization();
    this.actingSector = process.getActingSector();
    this.actingDate = process.getActingDate();
    this.lastDispatchDate = process.getLastDispatchDate();
    this.lengthOfStayOnSector = process.getLengthOfStayOnSector();
    this.history = ProcessTimelineDto.of(process.getHistory());
    this.history.sort(Comparator.comparing(ProcessTimelineDto::getUpdateDate).reversed());
  }

  public static ProcessFromEDocsDto of(final AdministrativeProcessDto process) {
    return new ProcessFromEDocsDto(process);
  }

  public List<ProcessTimelineDto> getHistory() { return Collections.unmodifiableList(history); }
  public String getProcessNumber() { return processNumber; }
  public String getStatus() { return status; }
  public String getSubject() { return subject; }
  public String getCurrentOrganization() { return currentOrganization; }
  public long getLengthOfStayOn() { return lengthOfStayOn; }
  public boolean getPriority() { return priority; }
  public String getActingOrganization() { return actingOrganization; }
  public String getActingSector() { return actingSector; }
  public LocalDateTime getActingDate() { return actingDate; }
  public LocalDateTime getLastDispatchDate() { return lastDispatchDate; }
  public long getLengthOfStayOnSector() { return lengthOfStayOnSector; }
}
