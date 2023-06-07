package br.gov.es.openpmo.dto.workpack.breakdown.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;

public class WorkpackBreakdownStructure {

  @JsonUnwrapped
  private WorkpackRepresentation representation;

  private List<WorkpackModelBreakdownStructure> workpackModels;

  @JsonIgnore
  private Comparable order;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Boolean hasChildren;

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

  public Comparable getOrder() {
    return order;
  }

  public void setOrder(Comparable order) {
    this.order = order;
  }

  public Boolean getHasChildren() {
    return hasChildren;
  }

  public void setHasChildren(Boolean hasChildren) {
    this.hasChildren = hasChildren;
  }
}
