package br.gov.es.openpmo.dto.workpack;

public class WorkpackHasChildrenResponse {

  private final Boolean hasChildren;
  private final Boolean hasOnlyBasicRead;

  public WorkpackHasChildrenResponse(
    final Boolean hasChildren,
    final Boolean hasOnlyBasicRead
  ) {
    this.hasChildren = hasChildren;
    this.hasOnlyBasicRead = hasOnlyBasicRead;
  }

  public static WorkpackHasChildrenResponse of(
    final boolean hasChildren,
    final boolean hasOnlyBasicRead
  ) {
    return new WorkpackHasChildrenResponse(hasChildren, hasOnlyBasicRead);
  }

  public Boolean getHasChildren() {
    return this.hasChildren;
  }

  public Boolean getHasOnlyBasicRead() {
    return this.hasOnlyBasicRead;
  }
}
