package br.gov.es.openpmo.dto.person.detail.permissions;

import br.gov.es.openpmo.dto.person.queries.WorkpackPermissionAndStakeholderQuery;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.model.relations.CanAccessWorkpack;
import br.gov.es.openpmo.model.workpacks.Workpack;

import java.util.stream.Collectors;

public class WorkpackPermissionDetailDto {

  private final Long id;
  private final String name;
  private final String role;
  private final String icon;
  private PermissionLevelEnum accessLevel;

  public WorkpackPermissionDetailDto(final Workpack workpack, final WorkpackPermissionAndStakeholderQuery query) {
    this.id = workpack.getId();
    this.name = workpack.getName();
    this.icon = workpack.getWorkpackModelInstance().getFontIcon();
    this.accessLevel = query.getCanAccess().stream()
      .filter(canAccess -> canAccess.getIdWorkpack().equals(workpack.getId()))
      .map(CanAccessWorkpack::getPermissionLevel)
      .findFirst().orElse(null);

    this.role = query.getIsStakeholderIn().stream()
      .filter(isStakeholderIn -> isStakeholderIn.getIdWorkpack().equals(workpack.getId()))
      .map(stakeholderIn -> stakeholderIn.getRole().trim())
      .filter(role -> !role.isEmpty())
      .collect(Collectors.joining(", "));

  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getRole() {
    return this.role;
  }

  public PermissionLevelEnum getAccessLevel() {
    return this.accessLevel;
  }

  public void setAccessLevel(final PermissionLevelEnum accessLevel) {
    this.accessLevel = accessLevel;
  }

  public String getIcon() {
    return this.icon;
  }
}
