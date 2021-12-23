package br.gov.es.openpmo.dto.stakeholder;

import java.time.LocalDate;

public class RoleDto {

  private Long id;
  private String role;
  private LocalDate from;
  private LocalDate to;
  private boolean active;

  public RoleDto() {
  }

  public RoleDto(final String role) {
    this.role = role;
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

  public LocalDate getFrom() {
    return this.from;
  }

  public void setFrom(final LocalDate from) {
    this.from = from;
  }

  public LocalDate getTo() {
    return this.to;
  }

  public void setTo(final LocalDate to) {
    this.to = to;
  }

  public boolean isActive() {
    return this.active;
  }

  public void setActive(final boolean active) {
    this.active = active;
  }

}
