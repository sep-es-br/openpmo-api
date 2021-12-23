package br.gov.es.openpmo.dto.person;

public class RoleResource {

  private String role;

  private String workLocation;

  public RoleResource() {
  }

  public RoleResource(final String role) {
    this.role = role;
  }

  public RoleResource(final String role, final String workLocation) {
    this.role = role;
    this.workLocation = workLocation;
  }

  public static RoleResource citizen() {
    final RoleResource resource = new RoleResource();
    resource.role = "citizen";
    return resource;
  }

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
    this.workLocation = workLocation != null && workLocation.isEmpty() ? null : workLocation;
  }

}
