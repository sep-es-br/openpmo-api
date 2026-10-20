package br.gov.es.openpmo.dto.workpackmodel;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class TransversalContextDto {

  private Long idPlan;
  private Long idPlanModel;
  private Long idTransversalView;
  private Long idTransversalProgram;
  private Long idWorkpackModel;
  private Long idWorkpack;
  private Long idOffice;
  private String viewName;

  public Long getIdPlan() { return idPlan; }
  public void setIdPlan(Long idPlan) { this.idPlan = idPlan; }
  public Long getIdPlanModel() { return idPlanModel; }
  public void setIdPlanModel(Long idPlanModel) { this.idPlanModel = idPlanModel; }
  public Long getIdTransversalView() { return idTransversalView; }
  public void setIdTransversalView(Long idTransversalView) { this.idTransversalView = idTransversalView; }
  public Long getIdTransversalProgram() { return idTransversalProgram; }
  public void setIdTransversalProgram(Long idTransversalProgram) { this.idTransversalProgram = idTransversalProgram; }
  public Long getIdWorkpackModel() { return idWorkpackModel; }
  public void setIdWorkpackModel(Long idWorkpackModel) { this.idWorkpackModel = idWorkpackModel; }
  public Long getIdWorkpack() { return idWorkpack; }
  public void setIdWorkpack(Long idWorkpack) { this.idWorkpack = idWorkpack; }
  public Long getIdOffice() { return idOffice; }
  public void setIdOffice(Long idOffice) { this.idOffice = idOffice; }
  public String getViewName() { return viewName; }
  public void setViewName(String viewName) { this.viewName = viewName; }
}
