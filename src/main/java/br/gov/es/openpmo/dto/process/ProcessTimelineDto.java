package br.gov.es.openpmo.dto.process;

import br.gov.es.pmo.administrative_process_core.model.AdministrativeProcessHistoryDto;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessTimelineDto {

  private Long daysDuration;
  @JsonFormat(pattern = "dd/MM/yyyy")
  private final LocalDateTime updateDate;
  private final Object organizationName;
  private final String sector;
  private final String descricaoTipo;

  private ProcessTimelineDto(final AdministrativeProcessHistoryDto item) {
    this.daysDuration = item.getDaysDuration();
    this.updateDate = item.getUpdateDate();
    this.organizationName = item.getOrganizationName();
    this.sector = item.getSector();
    this.descricaoTipo = item.getDescriptionType();
  }

  public static List<ProcessTimelineDto> of(
    final Collection<AdministrativeProcessHistoryDto> timeline
  ) {
    return timeline.stream()
      .map(ProcessTimelineDto::new)
      .collect(Collectors.toList());
  }

  public Long getDaysDuration() { return this.daysDuration; }
  public void clearDaysDuration() { this.daysDuration = null; }
  public LocalDateTime getUpdateDate() { return this.updateDate; }
  public Object getOrganizationName() { return this.organizationName; }
  public String getSector() { return this.sector; }
  public String getDescricaoTipo() { return this.descricaoTipo; }
}
