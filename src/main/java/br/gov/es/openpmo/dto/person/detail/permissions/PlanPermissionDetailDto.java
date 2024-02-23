package br.gov.es.openpmo.dto.person.detail.permissions;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlanPermissionDetailDto {

  private Long id;
  private String name;
  private PermissionLevelEnum accessLevel;
  private List<WorkpackPermissionDetailDto> workpacksPermission = new ArrayList<>(0);

  public PlanPermissionDetailDto() {
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

  public PermissionLevelEnum getAccessLevel() {
    return this.accessLevel;
  }

  public void setAccessLevel(final PermissionLevelEnum accessLevel) {
    this.accessLevel = accessLevel;
  }

  public List<WorkpackPermissionDetailDto> getWorkpacksPermission() {
    return this.workpacksPermission;
  }

  public void setWorkpacksPermission(final List<WorkpackPermissionDetailDto> workpacksPermission) {
    this.workpacksPermission = workpacksPermission;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  @Override
  public boolean equals(final Object o) {
    if(this == o) return true;
    if(o == null || this.getClass() != o.getClass()) return false;
    final PlanPermissionDetailDto that = (PlanPermissionDetailDto) o;
    return this.id.equals(that.id);
  }

  public void removeBasicRead() {
    if (this.accessLevel == PermissionLevelEnum.BASIC_READ) {
      this.accessLevel = PermissionLevelEnum.NONE;
    }
    for (WorkpackPermissionDetailDto workpackPermissionDetailDto : this.workpacksPermission) {
      workpackPermissionDetailDto.removeBasicRead();
    }
  }
}
