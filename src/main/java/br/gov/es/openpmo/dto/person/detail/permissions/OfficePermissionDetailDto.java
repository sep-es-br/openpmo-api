package br.gov.es.openpmo.dto.person.detail.permissions;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.utils.ApplicationMessage;

import java.util.List;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

public class OfficePermissionDetailDto {

  private Long id;
  private PermissionLevelEnum accessLevel;
  private List<PlanPermissionDetailDto> planPermissions;

  public OfficePermissionDetailDto(final CanAccessOffice canAccessOffice) {
    requireNonNull(canAccessOffice, ApplicationMessage.OFFICE_PERMISSION_NOT_FOUND);
    this.id = canAccessOffice.getId();
    this.accessLevel = canAccessOffice.getPermissionLevel();
  }

  public OfficePermissionDetailDto(
    final Long id,
    final PermissionLevelEnum accessLevel,
    final List<PlanPermissionDetailDto> planPermissions
  ) {
    this.id = id;
    this.accessLevel = accessLevel;
    this.planPermissions = planPermissions;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public PermissionLevelEnum getAccessLevel() {
    return this.accessLevel;
  }

  public void setAccessLevel(final PermissionLevelEnum accessLevel) {
    this.accessLevel = accessLevel;
  }

  public List<PlanPermissionDetailDto> getPlanPermissions() {
    return this.planPermissions;
  }

  public void setPlanPermissions(final List<PlanPermissionDetailDto> planPermissions) {
    this.planPermissions = planPermissions;
  }

  @Override
  public int hashCode() {
    return hash(this.id);
  }

  @Override
  public boolean equals(final Object o) {
    if(this == o) return true;
    if(o == null || this.getClass() != o.getClass()) return false;
    final OfficePermissionDetailDto that = (OfficePermissionDetailDto) o;
    return this.id.equals(that.id);
  }
}
