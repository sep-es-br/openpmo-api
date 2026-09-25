package br.gov.es.openpmo.dto.workpackmodel;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModelClassification;

public class TransversalViewDto {

  private Long idTransversalView;
  private Long idPlanModel;
  private String name;
  private String fullName;
  private WorkpackModelClassification classification = WorkpackModelClassification.TRANSVERSAL;

  public Long getIdTransversalView() { return idTransversalView; }
  public void setIdTransversalView(Long idTransversalView) { this.idTransversalView = idTransversalView; }
  public Long getIdPlanModel() { return idPlanModel; }
  public void setIdPlanModel(Long idPlanModel) { this.idPlanModel = idPlanModel; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }
  public WorkpackModelClassification getClassification() { return classification; }
  public void setClassification(WorkpackModelClassification classification) { this.classification = classification; }
}
