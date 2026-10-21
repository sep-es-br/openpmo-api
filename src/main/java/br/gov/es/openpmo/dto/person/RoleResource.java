package br.gov.es.openpmo.dto.person;

import br.gov.es.openpmo.scheduler.updateroles.HasRole;

import java.util.Objects;
import java.util.Optional;

public class RoleResource implements HasRole {

  private static final String CITIZEN = "citizen";

  private String role;

  private String workLocation;

  private RoleResource(final String role) {
    this.role = role;
  }

  public RoleResource(
    final String role,
    final String workLocation
  ) {
    this.role = role;
    this.workLocation = workLocation;
  }

  public static RoleResource citizen() {
    return new RoleResource(CITIZEN);
  }

  @Override
  public String getRole() {
    return this.role;
  }

  public void setRole(final String role) {
    this.role = role;
  }

  public String getWorkLocation() {
    return this.workLocation;
  }

  public void setWorkLocation(final String workLocation) {
    this.workLocation = Optional.ofNullable(workLocation)
      .filter(str -> !str.isEmpty())
      .orElse(null);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.role, this.workLocation);
  }

  @Override
  public boolean equals(final Object o) {
    if(this == o) return true;
    if(o == null || this.getClass() != o.getClass()) return false;
    final RoleResource that = (RoleResource) o;
    return this.role.equals(that.role) && Objects.equals(this.workLocation, that.workLocation);
  }

}
