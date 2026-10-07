package br.gov.es.openpmo.dto.workpackmodel;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class TransversalWorkpackDto {

  private Long idWorkpack;
  private Long idPlan;
  private Long idWorkpackModel;
  private Long originalParentId;
  private String name;
  private String fullName;
  private boolean canceled;
  private boolean alreadyIncluded;
  private boolean currentlyEligible;
  private boolean coveredByIncludedAncestor;

  public Long getIdWorkpack() { return idWorkpack; }
  public void setIdWorkpack(final Long idWorkpack) { this.idWorkpack = idWorkpack; }
  public Long getIdPlan() { return idPlan; }
  public void setIdPlan(final Long idPlan) { this.idPlan = idPlan; }
  public Long getIdWorkpackModel() { return idWorkpackModel; }
  public void setIdWorkpackModel(final Long idWorkpackModel) { this.idWorkpackModel = idWorkpackModel; }
  public Long getOriginalParentId() { return originalParentId; }
  public void setOriginalParentId(final Long originalParentId) { this.originalParentId = originalParentId; }
  public String getName() { return name; }
  public void setName(final String name) { this.name = name; }
  public String getFullName() { return fullName; }
  public void setFullName(final String fullName) { this.fullName = fullName; }
  public boolean isCanceled() { return canceled; }
  public void setCanceled(final boolean canceled) { this.canceled = canceled; }
  public boolean isAlreadyIncluded() { return alreadyIncluded; }
  public void setAlreadyIncluded(final boolean alreadyIncluded) { this.alreadyIncluded = alreadyIncluded; }
  public boolean isCurrentlyEligible() { return currentlyEligible; }
  public void setCurrentlyEligible(final boolean currentlyEligible) { this.currentlyEligible = currentlyEligible; }
  public boolean isCoveredByIncludedAncestor() { return coveredByIncludedAncestor; }
  public void setCoveredByIncludedAncestor(final boolean coveredByIncludedAncestor) { this.coveredByIncludedAncestor = coveredByIncludedAncestor; }
}
