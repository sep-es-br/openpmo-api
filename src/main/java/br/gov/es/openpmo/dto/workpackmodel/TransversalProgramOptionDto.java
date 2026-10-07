package br.gov.es.openpmo.dto.workpackmodel;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class TransversalProgramOptionDto {

  private Long idTransversalProgram;
  private Long idWorkpack;
  private Long idWorkpackModel;
  private Long idParent;
  private Long idTransversalView;
  private Long idPlan;
  private Long idPlanModel;
  private String fontIcon;
  private String name;
  private String fullName;
  private String type;
  private String classification;

  public Long getIdTransversalProgram() { return idTransversalProgram; }
  public void setIdTransversalProgram(Long idTransversalProgram) { this.idTransversalProgram = idTransversalProgram; }
  public Long getIdWorkpack() { return idWorkpack; }
  public void setIdWorkpack(Long idWorkpack) { this.idWorkpack = idWorkpack; }
  public Long getIdWorkpackModel() { return idWorkpackModel; }
  public Long getIdParent() { return idParent; }
  public void setIdParent(Long idParent) { this.idParent = idParent; }
  public void setIdWorkpackModel(Long idWorkpackModel) { this.idWorkpackModel = idWorkpackModel; }
  public Long getIdTransversalView() { return idTransversalView; }
  public void setIdTransversalView(Long idTransversalView) { this.idTransversalView = idTransversalView; }
  public Long getIdPlan() { return idPlan; }
  public void setIdPlan(Long idPlan) { this.idPlan = idPlan; }
  public Long getIdPlanModel() { return idPlanModel; }
  public void setIdPlanModel(Long idPlanModel) { this.idPlanModel = idPlanModel; }
  public String getFontIcon() { return fontIcon; }
  public void setFontIcon(String fontIcon) { this.fontIcon = fontIcon; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public String getClassification() { return classification; }
  public void setClassification(String classification) { this.classification = classification; }
}
