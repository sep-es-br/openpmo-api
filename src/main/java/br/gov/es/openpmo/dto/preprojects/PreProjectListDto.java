package br.gov.es.openpmo.dto.preprojects;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class PreProjectListDto {

  private Long id;

  private Long idPreProject;

  private Long idProject;

  private String name;

  private String fullName;

  private String status;

  private Long idPlan;

  private Long idWorkpack;

  private Long idOrganization;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getIdPreProject() {
    return this.idPreProject;
  }

  public void setIdPreProject(final Long idPreProject) {
    this.idPreProject = idPreProject;
  }

  public Long getIdProject() {
    return this.idProject;
  }

  public void setIdProject(final Long idProject) {
    this.idProject = idProject;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public Long getIdPlan() {
    return this.idPlan;
  }

  public void setIdPlan(final Long idPlan) {
    this.idPlan = idPlan;
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public void setIdWorkpack(final Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }

  public Long getIdOrganization() {
    return this.idOrganization;
  }

  public void setIdOrganization(final Long idOrganization) {
    this.idOrganization = idOrganization;
  }
}
