package br.gov.es.openpmo.dto.preprojects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Identifies where the project created from a pre-project will be placed.
 * The ProjectModel is obtained from the selected parent's model.
 */
public class CreateProjectFromPreProjectRequest {

  @NotNull
  private Long idPlan;

  @NotNull
  private Long idParent;

  @Size(max = 2000)
  private String observations;

  public Long getIdPlan() {
    return this.idPlan;
  }

  public void setIdPlan(final Long idPlan) {
    this.idPlan = idPlan;
  }

  public Long getIdParent() {
    return this.idParent;
  }

  public void setIdParent(final Long idParent) {
    this.idParent = idParent;
  }

  public String getObservations() {
    return this.observations;
  }

  public void setObservations(final String observations) {
    this.observations = observations;
  }

}
