package br.gov.es.openpmo.dto.process;

import br.gov.es.openpmo.model.process.Process;
import br.gov.es.pmo.administrative_process_core.model.AdministrativeProcessDto;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProcessDetailDto {

  private final Long id;
  private final String name;
  private final String note;
  @JsonUnwrapped
  private final ProcessReadonlyDetailDto readonlyDetail;
  private final List<ProcessTimelineDto> history;

  private ProcessDetailDto(
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
    this.history = history;
    this.history.sort(Comparator.comparing(ProcessTimelineDto::getUpdateDate).reversed());
  }

  public static ProcessDetailDto of(
    final AdministrativeProcessDto externalProcess,
    final Process process
  ) {
    return new ProcessDetailDto(
      process.getId(),
      process.getName(),
      process.getNote(),
      new ProcessReadonlyDetailDto(
        process.getProcessNumber(),
        externalProcess.getStatus(),
        process.getSubject(),
        externalProcess.getCurrentOrganization(),
        externalProcess.getLengthOfStayOn(),
        process.getPriority(),
        process.getActingOrganization(),
        process.getActingSector(),
        process.getActingDate(),
        process.getLastDispatchDate(),
        externalProcess.getLengthOfStayOnSector()
      ),
      ProcessTimelineDto.of(externalProcess.getHistory())
    );
  }

  public Long getId() { return this.id; }
  public String getName() { return this.name; }
  public String getNote() { return this.note; }
  public ProcessReadonlyDetailDto getReadonlyDetail() { return this.readonlyDetail; }
  public List<ProcessTimelineDto> getHistory() { return Collections.unmodifiableList(this.history); }
}
