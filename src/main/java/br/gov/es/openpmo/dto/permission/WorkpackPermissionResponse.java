package br.gov.es.openpmo.dto.permission;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class WorkpackPermissionResponse {

  private final Long idWorkpack;
  private final Collection<PermissionDto> permissions;
  private final Boolean canceled;
  private final LocalDate endManagementDate;


  private WorkpackPermissionResponse(
    final Long idWorkpack,
    final Collection<PermissionDto> permissions,
    final Boolean canceled,
    final LocalDate endManagementDate
  ) {
    this.idWorkpack = idWorkpack;
    this.permissions = permissions;
    this.canceled = canceled;
    this.endManagementDate = endManagementDate;
  }

  public static WorkpackPermissionResponse of(
    final Long id,
    final Collection<PermissionDto> permissions,
    final Boolean canceled,
    final LocalDate endManagementDate
  ) {
    return new WorkpackPermissionResponse(
      id,
      permissions,
      canceled,
      endManagementDate
    );
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public Collection<PermissionDto> getPermissions() {
    return Optional.ofNullable(this.permissions)
      .map(Collections::unmodifiableCollection)
      .orElse(null);
  }

  public Boolean getCanceled() {
    return this.canceled;
  }

  public LocalDate getEndManagementDate() {
    return this.endManagementDate;
  }

}
