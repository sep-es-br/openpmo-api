package br.gov.es.openpmo.dto.journals;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleJournalResponse {

  @JsonProperty("information")
  private InformationField informationField;

  @JsonProperty("author")
  private PersonField authorField;

  @JsonFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
  private LocalDateTime date;

  @JsonProperty("workpack")
  private WorkpackField workpackField;

  @JsonProperty("evidences")
  private Set<EvidenceField> evidenceFieldSet;

  public LocalDateTime getDate() {
    return this.date;
  }

  public void setDate(final LocalDateTime date) {
    this.date = date;
  }

  public WorkpackField getWorkpackField() {
    return this.workpackField;
  }

  public void setWorkpackField(final WorkpackField workpackField) {
    this.workpackField = workpackField;
  }

  public PersonField getAuthorField() {
    return this.authorField;
  }

  public void setAuthorField(final PersonField authorField) {
    this.authorField = authorField;
  }

  public InformationField getInformationField() {
    return this.informationField;
  }

  public void setInformationField(final InformationField informationField) {
    this.informationField = informationField;
  }

  public Set<EvidenceField> getEvidenceFieldSet() {
    return Optional.ofNullable(this.evidenceFieldSet)
      .map(Collections::unmodifiableSet)
      .orElse(Collections.emptySet());
  }

  public void setEvidenceFieldSet(final Set<? extends EvidenceField> evidenceFieldSet) {
    this.evidenceFieldSet = Collections.unmodifiableSet(evidenceFieldSet);
  }

}
