package br.gov.es.openpmo.dto.ccbmembers;

public class MemberAs {

  private String role;
  private String workLocation;
  private Boolean active;

  public MemberAs() {
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
    this.workLocation = workLocation;
  }

  public Boolean getActive() {
    return this.active;
  }

  public void setActive(final Boolean active) {
    this.active = active;
  }

}
