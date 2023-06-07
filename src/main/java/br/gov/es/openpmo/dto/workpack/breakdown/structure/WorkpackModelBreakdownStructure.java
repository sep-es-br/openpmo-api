package br.gov.es.openpmo.dto.workpack.breakdown.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;
import java.util.Optional;

public class WorkpackModelBreakdownStructure {

  @JsonUnwrapped
  private WorkpackModelRepresentation representation;

  private List<WorkpackBreakdownStructure> workpacks;

  public List<WorkpackBreakdownStructure> getWorkpacks() {
    return this.workpacks;
  }

  public void setWorkpacks(final List<WorkpackBreakdownStructure> workpacks) {
    this.workpacks = workpacks;
  }

  @JsonIgnore
  public String getName() {
    return Optional.ofNullable(this.representation)
      .map(WorkpackModelRepresentation::getWorkpackModelName)
      .orElse(null);
  }

  @JsonIgnore
  public Long getRepresentationPosition() {
    return Optional.ofNullable(this.representation)
      .map(WorkpackModelRepresentation::getWorkpackModelPosition)
      .orElse(0L);
  }

  public WorkpackModelRepresentation getRepresentation() {
    return this.representation;
  }

  public void setRepresentation(final WorkpackModelRepresentation representation) {
    this.representation = representation;
  }

}
