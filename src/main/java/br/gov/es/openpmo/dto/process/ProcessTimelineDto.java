package br.gov.es.openpmo.dto.process;

import br.gov.es.openpmo.apis.edocs.ProcessTimeline;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessTimelineDto {

  private final long daysDuration;
  @JsonFormat(pattern = "dd/MM/yyyy")
  private final LocalDateTime updateDate;
  private final Object organizationName;
  private final String sector;
  private final String descricaoTipo;

  private ProcessTimelineDto(final ProcessTimeline item) {
    this.daysDuration = item.daysDuration();
    this.updateDate = item.detail().getDate();
    this.organizationName = item.detail().getAbbreviation();
    this.sector = item.detail().getName();
    this.descricaoTipo = item.detail().getDescricaoTipo();
  }

  public static List<ProcessTimelineDto> of(final List<ProcessTimeline> timeline) {
    return timeline.stream()
      .map(ProcessTimelineDto::new)
      .collect(Collectors.toList());
  }

  public long getDaysDuration() {
    return this.daysDuration;
  }

  public LocalDateTime getUpdateDate() {
    return this.updateDate;
  }

  public Object getOrganizationName() {
    return this.organizationName;
  }

  public String getSector() {
    return this.sector;
  }

  public String getDescricaoTipo() {
    return this.descricaoTipo;
  }

}
