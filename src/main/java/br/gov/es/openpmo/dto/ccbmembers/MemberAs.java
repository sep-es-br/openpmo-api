package br.gov.es.openpmo.dto.ccbmembers;

public class MemberAs {

  private String role;
  private String workLocation;
  private Boolean active;
  private String level;      // "WORKPACK" | "PLAN" | "OFFICE"
  private String levelName;  // nome do Plano/Escritório; null para Workpack

  public MemberAs() {
  }

  // Construtor original (nível Workpack) — mantido pra não quebrar quem já usa.
  public MemberAs(final String role, final String workLocation, final Boolean active) {
    this(role, workLocation, active, "WORKPACK", null);
  }

  public MemberAs(
    final String role,
    final String workLocation,
    final Boolean active,
    final String level,
    final String levelName
  ) {
    this.role = role;
    this.workLocation = workLocation;
    this.active = active;
    this.level = level;
    this.levelName = levelName;
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

  public String getLevel() {
    return this.level;
  }

  public void setLevel(final String level) {
    this.level = level;
  }

  public String getLevelName() {
    return this.levelName;
  }

  public void setLevelName(final String levelName) {
    this.levelName = levelName;
  }

}