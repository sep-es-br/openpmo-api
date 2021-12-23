package br.gov.es.openpmo.dto.permission;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.model.relations.CanAccessPlan;
import br.gov.es.openpmo.model.relations.CanAccessWorkpack;
import br.gov.es.openpmo.model.relations.IsSharedWith;
import br.gov.es.openpmo.model.workpacks.Workpack;

public class PermissionDto {
  private Long id;
  private String role;
  private PermissionLevelEnum level;
  private String inheritedFrom;

  private Long idPlan;

  public PermissionDto() {
  }

  public PermissionDto(final Long id, final String role, final PermissionLevelEnum level) {
    this.id = id;
    this.role = role;
    this.level = level;
  }

  public static PermissionDto of(final CanAccessWorkpack canAccessWorkpack) {
    final PermissionDto permissionDto = new PermissionDto();
    permissionDto.setId(canAccessWorkpack.getId());
    permissionDto.setRole(canAccessWorkpack.getRole());
    permissionDto.setLevel(canAccessWorkpack.getPermissionLevel());
    permissionDto.setIdPlan(canAccessWorkpack.getIdPlan());
    return permissionDto;
  }

  public static PermissionDto of(final CanAccessPlan canAccessPlan) {
    return new PermissionDto(
      canAccessPlan.getId(),
      canAccessPlan.getRole(),
      canAccessPlan.getPermissionLevel()
    );
  }

  public static PermissionDto of(final Workpack workpack) {
    final PermissionDto permissionDto = new PermissionDto();
    permissionDto.setId(workpack.getId());
    permissionDto.setLevel(workpack.getPublicLevel());
    return permissionDto;
  }

  public static PermissionDto of(final IsSharedWith isSharedWith) {
    final PermissionDto permissionDto = new PermissionDto();
    permissionDto.setId(isSharedWith.getId());
    permissionDto.setLevel(isSharedWith.getPermissionLevel());
    return permissionDto;
  }

  public static PermissionDto read() {
    final PermissionDto permissionDto = new PermissionDto();
    permissionDto.setLevel(PermissionLevelEnum.READ);
    return permissionDto;
  }

  public static PermissionDto of(final CanAccessOffice canAccessOffice) {
    return new PermissionDto(
      canAccessOffice.getId(),
      canAccessOffice.getRole(),
      canAccessOffice.getPermissionLevel()
    );
  }

  public Long getIdPlan() {
    return this.idPlan;
  }

  public void setIdPlan(final Long idPlan) {
    this.idPlan = idPlan;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getRole() {
    return this.role;
  }

  public void setRole(final String role) {
    this.role = role;
  }

  public PermissionLevelEnum getLevel() {
    return this.level;
  }

  public void setLevel(final PermissionLevelEnum level) {
    this.level = level;
  }

  public String getInheritedFrom() {
    return this.inheritedFrom;
  }

  public void setInheritedFrom(final String inheritedFrom) {
    this.inheritedFrom = inheritedFrom;
  }
}
