package br.gov.es.openpmo.dto.workpackmodel;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModelClassification;

public class TransversalProgramDto extends TransversalContextDto {

  private Long idTransversalProgram;
  private String name;
  private String fullName;
  private String type = "Program";
  private WorkpackModelClassification classification = WorkpackModelClassification.TRANSVERSAL;

  public Long getIdTransversalProgram() { return idTransversalProgram; }
  public void setIdTransversalProgram(Long idTransversalProgram) { this.idTransversalProgram = idTransversalProgram; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public WorkpackModelClassification getClassification() { return classification; }
  public void setClassification(WorkpackModelClassification classification) { this.classification = classification; }
}
