package br.gov.es.openpmo.dto.menu;

public class BreadcrumbDto {

  private Long id;
  private Long idWorkpackModelLinked;
  private String name;
  private String fullName;
  private String type;
  private String modelName;

  public BreadcrumbDto() {

  }

  public BreadcrumbDto(
    final Long id,
    final String name,
    final String fullName,
    final String type
  ) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.fullName = fullName;
  }

  public Long getIdWorkpackModelLinked() {
    return this.idWorkpackModelLinked;
  }

  public void setIdWorkpackModelLinked(final Long idWorkpackModelLinked) {
    this.idWorkpackModelLinked = idWorkpackModelLinked;
  }

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

  public String getType() {
    return this.type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public String getModelName() {
    return this.modelName;
  }

  public void setModelName(final String modelName) {
    this.modelName = modelName;
  }
}
