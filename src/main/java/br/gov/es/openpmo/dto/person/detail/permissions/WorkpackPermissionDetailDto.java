package br.gov.es.openpmo.dto.person.detail.permissions;

import br.gov.es.openpmo.enumerator.PermissionLevelEnum;

import java.util.List;

import org.springframework.data.neo4j.annotation.QueryResult;

import com.fasterxml.jackson.annotation.JsonIgnore;

@QueryResult
public class WorkpackPermissionDetailDto {

  private Long id;
  private String name;
  @JsonIgnore
  private Long idPlan;
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


  public Long getIdPlan() {
    return idPlan;
  }

  public void setIdPlan(Long idPlan) {
    this.idPlan = idPlan;
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

  public void removeBasicRead() {
    if (this.accessLevel == PermissionLevelEnum.BASIC_READ) {
      this.accessLevel = PermissionLevelEnum.NONE;
    }
  }
}
