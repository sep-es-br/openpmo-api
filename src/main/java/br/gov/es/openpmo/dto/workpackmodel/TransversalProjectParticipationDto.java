package br.gov.es.openpmo.dto.workpackmodel;

public class TransversalProjectParticipationDto {

  private Long idTransversalProgram;
  private Long idProject;
  private boolean included;

  public Long getIdTransversalProgram() {
    return this.idTransversalProgram;
  }

  public void setIdTransversalProgram(final Long idTransversalProgram) {
    this.idTransversalProgram = idTransversalProgram;
  }

  public Long getIdProject() {
    return this.idProject;
  }

  public void setIdProject(final Long idProject) {
    this.idProject = idProject;
  }

  public boolean isIncluded() {
    return this.included;
  }

  public void setIncluded(final boolean included) {
    this.included = included;
  }
}
