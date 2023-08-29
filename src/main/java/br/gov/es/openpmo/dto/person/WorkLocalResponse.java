package br.gov.es.openpmo.dto.person;

import br.gov.es.openpmo.model.actors.Person;

public class WorkLocalResponse {

  private Long idOffice;

  private Long idPlan;

  private Long idWorkpack;

  private Long idWorkpackModelLinked;

  public static WorkLocalResponse from(final Person person) {
    final WorkLocalResponse dto = new WorkLocalResponse();
    dto.setIdOffice(person.getIdOffice());
    dto.setIdPlan(person.getIdPlan());
    dto.setIdWorkpack(person.getIdWorkpack());
    dto.setIdWorkpackModelLinked(person.getIdWorkpackModelLinked());
    return dto;
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
}
