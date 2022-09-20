package br.gov.es.openpmo.dto.person.detail.permissions;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;

import java.util.List;

public class WorkpackPermissionDetailDto {

  private Long id;
  private String name;
  private List<String> roles;
  private String icon;
  private PermissionLevelEnum accessLevel;
  private Boolean isCcbMember;

  public WorkpackPermissionDetailDto() {
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

  public List<String> getRoles() {
    return this.roles;
  }

  public void setRoles(final List<String> roles) {
    this.roles = roles;
  }

  public String getIcon() {
    return this.icon;
  }

  public void setIcon(final String icon) {
    this.icon = icon;
  }

  public PermissionLevelEnum getAccessLevel() {
    return this.accessLevel;
  }

  public void setAccessLevel(final PermissionLevelEnum accessLevel) {
    this.accessLevel = accessLevel;
  }

  public Boolean getCcbMember() {
    return this.isCcbMember;
  }

  public void setCcbMember(final Boolean ccbMember) {
    this.isCcbMember = ccbMember;
  }

}
