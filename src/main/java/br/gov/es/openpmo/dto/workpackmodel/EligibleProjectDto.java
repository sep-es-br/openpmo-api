package br.gov.es.openpmo.dto.workpackmodel;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class EligibleProjectDto {

  private Long idProject;
  private Long idWorkpack;
  private Long idPlan;
  private Long idWorkpackModel;
  private Long idStructuralModel;
  private String name;
  private String fullName;
  private Long originalParentId;
  private boolean alreadyIncluded;
  private boolean currentlyEligible;

  public Long getIdProject() { return idProject; }
  public void setIdProject(Long idProject) { this.idProject = idProject; }
  public Long getIdWorkpack() { return idWorkpack; }
  public void setIdWorkpack(Long idWorkpack) { this.idWorkpack = idWorkpack; }
  public Long getIdPlan() { return idPlan; }
  public void setIdPlan(Long idPlan) { this.idPlan = idPlan; }
  public Long getIdWorkpackModel() { return idWorkpackModel; }
  public void setIdWorkpackModel(Long idWorkpackModel) { this.idWorkpackModel = idWorkpackModel; }
  public Long getIdStructuralModel() { return idStructuralModel; }
  public void setIdStructuralModel(Long idStructuralModel) { this.idStructuralModel = idStructuralModel; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }
  public Long getOriginalParentId() { return originalParentId; }
  public void setOriginalParentId(Long originalParentId) { this.originalParentId = originalParentId; }
  public boolean isAlreadyIncluded() { return alreadyIncluded; }
  public void setAlreadyIncluded(boolean alreadyIncluded) { this.alreadyIncluded = alreadyIncluded; }
  public boolean isCurrentlyEligible() { return currentlyEligible; }
  public void setCurrentlyEligible(boolean currentlyEligible) { this.currentlyEligible = currentlyEligible; }
}
