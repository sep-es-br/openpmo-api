package br.gov.es.openpmo.dto.workpack;

public class WorkpackHasChildrenResponse {

  private final Boolean hasChildren;

  public WorkpackHasChildrenResponse(final Boolean hasChildren) {
    this.hasChildren = hasChildren;
  }

  public Boolean getHasChildren() {
    return this.hasChildren;
  }

}
