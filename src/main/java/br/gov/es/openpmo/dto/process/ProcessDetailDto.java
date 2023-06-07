package br.gov.es.openpmo.dto.process;

import br.gov.es.openpmo.apis.edocs.ProcessTimeline;
import br.gov.es.openpmo.apis.edocs.response.ProcessResponse;
import br.gov.es.openpmo.model.process.Process;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ProcessDetailDto {

  private final Long id;
  private final String name;
  private final String note;
  @JsonUnwrapped
  private final ProcessReadonlyDetailDto readonlyDetail;
  private final List<ProcessTimelineDto> history;

  public ProcessDetailDto(
    final Long id,
    final String name,
    final String note,
    final ProcessReadonlyDetailDto readonlyDetail,
    final List<ProcessTimelineDto> history
  ) {
    this.id = id;
    this.name = name;
    this.note = note;
    this.readonlyDetail = readonlyDetail;
    this.history = Collections.unmodifiableList(history);
    history.sort(Comparator.comparing(ProcessTimelineDto::getUpdateDate).reversed());
  }

  public static ProcessDetailDto of(
    final ProcessResponse processResponse,
    final Process process
  ) {
    final List<ProcessTimeline> timeline = processResponse.timeline().stream()
      .sorted(Comparator.comparing(ProcessDetailDto::getDate).reversed())
      .collect(Collectors.toList());

    final String currentOrganization = processResponse.getCurrentOrganizationAbbreviation();

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

    return new ProcessDetailDto(
      process.getId(),
      process.getName(),
      process.getNote(),
      new ProcessReadonlyDetailDto(
        process.getProcessNumber(),
        processResponse.getStatus(),
        process.getSubject(),
        currentOrganization,
        lengthOfStayOn,
        process.getPriority()
      ),
      timelineDtos
    );
  }

  private static LocalDateTime getDate(final ProcessTimeline processTimeline) {
    return processTimeline.detail().getDate();
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

  public ProcessReadonlyDetailDto getReadonlyDetail() {
    return this.readonlyDetail;
  }

  public List<ProcessTimelineDto> getHistory() {
    return this.history;
  }

}
