package br.gov.es.openpmo.dto.journals;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.journals.JournalAction;
import br.gov.es.openpmo.model.journals.JournalType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class JournalResponse {

  private JournalType type;

  private PermissionLevelEnum level;

  @JsonProperty("status")
  private JournalAction action;

  @JsonFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
  private LocalDateTime date;

  @JsonProperty("workpack")
  private WorkpackField workpackField;

  @JsonProperty("author")
  private PersonField authorField;

  @JsonProperty("target")
  private PersonField targetField;

  @JsonProperty("office")
  private OfficeField officeField;

  @JsonProperty("plan")
  private PlanField planField;

  @JsonProperty("information")
  private InformationField informationField;

  @JsonProperty("evidences")
  private Set<EvidenceField> evidenceFieldSet;

  public JournalType getType() {
    return this.type;
  }

  public void setType(final JournalType type) {
    this.type = type;
  }

  public JournalAction getAction() {
    return this.action;
  }

  public void setAction(final JournalAction action) {
    this.action = action;
  }

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

  public OfficeField getOfficeField() {
    return this.officeField;
  }

  public void setOfficeField(final OfficeField officeField) {
    this.officeField = officeField;
  }

  public PlanField getPlanField() {
    return this.planField;
  }

  public void setPlanField(final PlanField planField) {
    this.planField = planField;
  }

  public PersonField getTargetField() {
    return this.targetField;
  }

  public void setTargetField(final PersonField targetField) {
    this.targetField = targetField;
  }

  public PermissionLevelEnum getLevel() {
    return this.level;
  }

  public void setLevel(final PermissionLevelEnum level) {
    this.level = level;
  }

}
