package br.gov.es.openpmo.dto.workpack.breakdown.structure;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;

public class WorkpackModelBreakdownStructure {

  @JsonUnwrapped
  private WorkpackModelRepresentation representation;

  private List<WorkpackBreakdownStructure> workpacks;

  public WorkpackModelRepresentation getRepresentation() {
    return representation;
  }

  public void setRepresentation(WorkpackModelRepresentation representation) {
    this.representation = representation;
  }

  public List<WorkpackBreakdownStructure> getWorkpacks() {
    return workpacks;
  }

  public void setWorkpacks(List<WorkpackBreakdownStructure> workpacks) {
    this.workpacks = workpacks;
  }

}
