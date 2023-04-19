package br.gov.es.openpmo.dto.workpack.breakdown.structure;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;

public class WorkpackBreakdownStructure {

  @JsonUnwrapped
  private WorkpackRepresentation representation;

  private List<WorkpackModelBreakdownStructure> workpackModels;

  public WorkpackRepresentation getRepresentation() {
    return representation;
  }

  public void setRepresentation(WorkpackRepresentation representation) {
    this.representation = representation;
  }

  public List<WorkpackModelBreakdownStructure> getWorkpackModels() {
    return workpackModels;
  }

  public void setWorkpackModels(List<WorkpackModelBreakdownStructure> workpackModels) {
    this.workpackModels = workpackModels;
  }

}
