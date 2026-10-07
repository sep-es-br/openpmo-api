package br.gov.es.openpmo.dto.workpackmodel;

public class TransversalWorkpackParticipationDto {

  private Long idTransversalProgram;
  private Long idWorkpack;
  private boolean included;

  public Long getIdTransversalProgram() { return idTransversalProgram; }
  public void setIdTransversalProgram(final Long idTransversalProgram) { this.idTransversalProgram = idTransversalProgram; }
  public Long getIdWorkpack() { return idWorkpack; }
  public void setIdWorkpack(final Long idWorkpack) { this.idWorkpack = idWorkpack; }
  public boolean isIncluded() { return included; }
  public void setIncluded(final boolean included) { this.included = included; }
}
