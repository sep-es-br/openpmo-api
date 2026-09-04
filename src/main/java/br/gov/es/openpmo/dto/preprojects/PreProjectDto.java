package br.gov.es.openpmo.dto.preprojects;

import br.gov.es.openpmo.model.preprojects.PreProject;
import java.time.LocalDate;
import java.util.Optional;

public class PreProjectDto {

  private final Long id;

  private final String name;

  private final String fullName;

  private final LocalDate expectedCompletionDate;

  private final String expectedDeliveries;

  private final Long idOrganization;

  private final Long idPreProjectModel;

  public PreProjectDto(final PreProject preProject) {
    this.id = preProject.getId();
    this.name = preProject.getName();
    this.fullName = preProject.getFullName();
    this.expectedCompletionDate = preProject.getExpectedCompletionDate();
    this.expectedDeliveries = preProject.getExpectedDeliveries();
    this.idOrganization = Optional.ofNullable(preProject.getOrganization())
      .map(organization -> organization.getId())
      .orElse(null);
    this.idPreProjectModel = Optional.ofNullable(preProject.getInstance())
      .map(model -> model.getId())
      .orElse(null);
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public LocalDate getExpectedCompletionDate() {
    return this.expectedCompletionDate;
  }

  public String getExpectedDeliveries() {
    return this.expectedDeliveries;
  }

  public Long getIdOrganization() {
    return this.idOrganization;
  }

  public Long getIdPreProjectModel() {
    return this.idPreProjectModel;
  }

}
