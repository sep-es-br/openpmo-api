package br.gov.es.openpmo.dto.menu;

import br.gov.es.openpmo.dto.permission.PermissionDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkpackMenuDto {
  private Long id;
  private Long idPlan;
  private String name;
  private String fontIcon;
  private List<PermissionDto> permissions;
  private Long idWorkpackModelLinked;
  private Set<WorkpackMenuDto> children = new HashSet<>(0);

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getIdPlan() {
    return this.idPlan;
  }

  public void setIdPlan(final Long idPlan) {
    this.idPlan = idPlan;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getFontIcon() {
    return this.fontIcon;
  }

  public void setFontIcon(final String fontIcon) {
    this.fontIcon = fontIcon;
  }

  public Set<WorkpackMenuDto> getChildren() {
    return this.children;
  }

  public void setChildren(final Set<WorkpackMenuDto> children) {
    this.children = children;
  }

  public List<PermissionDto> getPermissions() {
    return this.permissions;
  }

  public void setPermissions(final List<PermissionDto> permissions) {
    this.permissions = permissions;
  }

  public Long getIdWorkpackModelLinked() {
    return this.idWorkpackModelLinked;
  }

  public void setIdWorkpackModelLinked(final Long idWorkpackModelLinked) {
    this.idWorkpackModelLinked = idWorkpackModelLinked;
  }
}
