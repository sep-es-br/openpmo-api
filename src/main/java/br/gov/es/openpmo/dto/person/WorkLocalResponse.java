package br.gov.es.openpmo.dto.person;

import br.gov.es.openpmo.model.actors.Person;

public class WorkLocalResponse {

  private Long idOffice;

  private Long idPlan;

  private Long idWorkpack;

  private Long idWorkpackModelLinked;

  private String classification;

  private Long idTransversalRoot;

  public static WorkLocalResponse from(final Person person) {
    final WorkLocalResponse dto = new WorkLocalResponse();
    dto.setIdOffice(person.getIdOffice());
    dto.setIdPlan(person.getIdPlan());
    dto.setIdWorkpack(person.getIdWorkpack());
    dto.setIdWorkpackModelLinked(person.getIdWorkpackModelLinked());
    dto.setClassification(person.getClassification());
    dto.setIdTransversalRoot(person.getIdTransversalRoot());
    return dto;
  }

  public WorkLocalResponse orNull() {
    if (isInvalid()) {
      return null;
    }
    return this;
  }

  private boolean isInvalid() {
    return this.idOffice == null
      && this.idPlan == null
      && this.idWorkpack == null
      && this.idWorkpackModelLinked == null
      && this.classification == null
      && this.idTransversalRoot == null;
  }

  public Long getIdOffice() {
    return idOffice;
  }

  public void setIdOffice(Long idOffice) {
    this.idOffice = idOffice;
  }

  public Long getIdPlan() {
    return idPlan;
  }

  public void setIdPlan(Long idPlan) {
    this.idPlan = idPlan;
  }

  public Long getIdWorkpack() {
    return idWorkpack;
  }

  public void setIdWorkpack(Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }

  public Long getIdWorkpackModelLinked() {
    return idWorkpackModelLinked;
  }

  public void setIdWorkpackModelLinked(Long idWorkpackModelLinked) {
    this.idWorkpackModelLinked = idWorkpackModelLinked;
  }

  public String getClassification() {
    return classification;
  }

  public void setClassification(String classification) {
    this.classification = classification;
  }

  public Long getIdTransversalRoot() {
    return idTransversalRoot;
  }

  public void setIdTransversalRoot(Long idTransversalRoot) {
    this.idTransversalRoot = idTransversalRoot;
  }
}
