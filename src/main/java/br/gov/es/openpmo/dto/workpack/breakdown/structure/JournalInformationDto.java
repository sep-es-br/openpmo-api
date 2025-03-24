package br.gov.es.openpmo.dto.workpack.breakdown.structure;

import java.time.LocalDateTime;

import org.springframework.data.neo4j.annotation.QueryResult;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import br.gov.es.openpmo.model.journals.JournalEntry;

@QueryResult
public class JournalInformationDto {

  private Long id;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDateTime date;

  @JsonIgnore
  private Long idWorkapck;

  public static JournalInformationDto of(JournalEntry journalEntry) {
    final JournalInformationDto dto = new JournalInformationDto();
    dto.setId(journalEntry.getId());
    dto.setDate(journalEntry.getDate());
    return dto;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public Long getIdWorkapck() {
    return idWorkapck;
  }

  public void setIdWorkapck(Long idWorkapck) {
    this.idWorkapck = idWorkapck;
  }
}
