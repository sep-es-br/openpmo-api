package br.gov.es.openpmo.dto.workpack;

public class WorkpackNameResponse {

  private final String name;
  private final String fullName;


  public WorkpackNameResponse(
    final String name,
    final String fullName
  ) {
    this.name = name;
    this.fullName = fullName;
  }

  public String getName() {
    return this.name;
  }

  public String getFullName() {
    return this.fullName;
  }

}
