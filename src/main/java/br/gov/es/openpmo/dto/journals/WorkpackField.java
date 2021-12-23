package br.gov.es.openpmo.dto.journals;

public class WorkpackField {

  private Long id;

  private String name;

  private String workpackModelName;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getWorkpackModelName() {
    return this.workpackModelName;
  }

  public void setWorkpackModelName(final String workpackModelName) {
    this.workpackModelName = workpackModelName;
  }

}
