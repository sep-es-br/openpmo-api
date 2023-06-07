package br.gov.es.openpmo.dto.process;

import br.gov.es.openpmo.apis.edocs.ProcessTimeline;
import br.gov.es.openpmo.apis.edocs.response.ProcessResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    final List<ProcessTimeline> timeline = process.timeline().stream()
      .sorted(Comparator.comparing(ProcessFromEDocsDto::getDate).reversed())
      .collect(Collectors.toList());

    final String currentOrganization = process.getCurrentOrganizationAbbreviation();

    LocalDateTime date = LocalDateTime.now();
    for (final ProcessTimeline processTimeline : timeline) {
      if (!Objects.equals(processTimeline.detail().getAbbreviation(), currentOrganization)) {
        break;
      }
      date = processTimeline.detail().getDate();
    }
    List<ProcessTimelineDto> timelineDtos = ProcessTimelineDto.of(timeline);
    LocalDateTime until = LocalDateTime.now();
    if (Objects.equals(process.getStatus(), "Encerrado")) {
      final Optional<ProcessTimelineDto> encerramento = timelineDtos.stream()
        .filter(dto -> Objects.equals(dto.getDescricaoTipo(), "Encerramento"))
        .max(Comparator.comparing(ProcessTimelineDto::getUpdateDate));
      until = date;
      date = encerramento
        .map(ProcessTimelineDto::getUpdateDate)
        .orElse(until);
      encerramento.ifPresent(ProcessTimelineDto::clearDaysDuration);
    }
    final long lengthOfStayOn = Duration.between(date, until).abs().toDays();

    return new ProcessFromEDocsDto(
      process.getProcessNumber(),
      process.getStatus(),
      process.getSubject(),
      currentOrganization,
      lengthOfStayOn,
      process.getPriority(),
      timelineDtos
    );
  }

  @JsonIgnore
  private static LocalDateTime getDate(final ProcessTimeline processTimeline) {
    return processTimeline.detail().getDate();
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
