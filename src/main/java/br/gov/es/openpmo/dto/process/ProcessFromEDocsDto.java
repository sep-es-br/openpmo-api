package br.gov.es.openpmo.dto.process;

import br.gov.es.openpmo.apis.edocs.response.ProcessResponse;

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
  private final List<ProcessTimelineDto> history;

  public ProcessFromEDocsDto(
    final String processNumber,
    final String status,
    final String subject,
    final String currentOrganization,
    final long lengthOfStayOn,
    final boolean priority,
    final List<ProcessTimelineDto> history
  ) {
    this.processNumber = processNumber;
    this.status = status;
    this.subject = subject;
    this.currentOrganization = currentOrganization;
    this.lengthOfStayOn = lengthOfStayOn;
    this.priority = priority;
    this.history = Collections.unmodifiableList(history);
    history.sort(Comparator.comparing(ProcessTimelineDto::getUpdateDate).reversed());
  }

  public static ProcessFromEDocsDto of(final ProcessResponse process) {
    return new ProcessFromEDocsDto(
      process.getProcessNumber(),
      process.getStatus(),
      process.getSubject(),
      process.getCurrentOrganizationAbbreviation(),
      process.lengthOfStayOn(),
      process.getPriority(),
      ProcessTimelineDto.of(process.timeline())
    );
  }


  public List<ProcessTimelineDto> getHistory() {
    return this.history;
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

  public long getLengthOfStayOn() {
    return this.lengthOfStayOn;
  }

  public boolean getPriority() {
    return this.priority;
  }
}
