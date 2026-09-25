package br.gov.es.openpmo.dto.workpackmodel;

import java.util.List;

public class CreateTransversalProgramDto {

  private Long idPlan;
  private Long idWorkpackModel;
  private String name;
  private String fullName;
  private List<?> properties;

  public Long getIdPlan() { return idPlan; }
  public void setIdPlan(Long idPlan) { this.idPlan = idPlan; }
  public Long getIdWorkpackModel() { return idWorkpackModel; }
  public void setIdWorkpackModel(Long idWorkpackModel) { this.idWorkpackModel = idWorkpackModel; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }
  public List<?> getProperties() { return properties; }
  public void setProperties(List<?> properties) { this.properties = properties; }
}
