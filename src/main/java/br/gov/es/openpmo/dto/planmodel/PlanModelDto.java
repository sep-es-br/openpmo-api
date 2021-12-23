package br.gov.es.openpmo.dto.planmodel;

import br.gov.es.openpmo.dto.office.OfficeDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.PlanModel;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PlanModelDto {

  private Long id;
  private String name;
  private String fullName;
  private Long idOffice;

  private boolean sharedWithAll;
  private Set<OfficeDto> sharedWith;

  public PlanModelDto() {
  }

  public PlanModelDto(final PlanModel planModel) {
    this.id = planModel.getId();
    this.name = planModel.getName();
    this.fullName = planModel.getFullName();
    this.idOffice = Optional.ofNullable(planModel.getOffice()).map(Entity::getId).orElse(null);

    this.sharedWithAll = planModel.isPublicShared();

    this.sharedWith = Optional.ofNullable(planModel.getSharedWith())
      .map(offices -> offices.stream().map(Office::getDto).collect(Collectors.toSet()))
      .orElse(Collections.emptySet());
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public Long getIdOffice() {
    return this.idOffice;
  }

  public void setIdOffice(final Long idOffice) {
    this.idOffice = idOffice;
  }

  public boolean isSharedWithAll() {
    return this.sharedWithAll;
  }

  public void setSharedWithAll(final boolean sharedWithAll) {
    this.sharedWithAll = sharedWithAll;
  }

  public Set<OfficeDto> getSharedWith() {
    return this.sharedWith;
  }

  public void setSharedWith(final Set<OfficeDto> sharedWith) {
    this.sharedWith = sharedWith;
  }
}
