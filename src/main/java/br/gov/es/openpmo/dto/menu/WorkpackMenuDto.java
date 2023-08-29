package br.gov.es.openpmo.dto.menu;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.service.properties.SorterProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkpackMenuDto {

  private Long id;
  private Long idPlan;
  private Long idWorkpackModel;
  private String name;
  private String fullName;
  private String fontIcon;
  private List<PermissionDto> permissions;
  private Long idWorkpackModelLinked;
  @JsonIgnore
  private SorterProperty<?> sorter;
  private Set<WorkpackMenuDto> children = new HashSet<>(0);

  public WorkpackMenuDto(
    final Long id,
    final Long idPlan,
    final String name,
    final String fullName,
    final List<PermissionDto> permissions,
    final String fontIcon,
    final Long idWorkpackModelLinked,
    final Long idWorkpackModel,
    final SorterProperty<?> sorter
  ) {
    this.id = id;
    this.idPlan = idPlan;
    this.name = name;
    this.fullName = fullName;
    this.permissions = permissions;
    this.fontIcon = fontIcon;
    this.idWorkpackModelLinked = idWorkpackModelLinked;
    this.idWorkpackModel = idWorkpackModel;
    this.sorter = sorter;
  }

  public WorkpackMenuDto() {}

  public static WorkpackMenuDto of(
    final Workpack workpack,
    final Long idPlan,
    final SorterProperty<?> sorter
  ) {
    return new WorkpackMenuDto(
      workpack.getId(),
      idPlan,
      workpack.getPropertyName().map(Property::getValue)
        .map(String.class::cast)
        .orElse(null),
      workpack.getPropertyFullName().map(Property::getValue)
        .map(String.class::cast)
        .orElse(null),
      null,
      workpack.getIcon(),
      null,
      workpack.getIdWorkpackModel(),
      sorter
    );
  }

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

  public Long getIdWorkpackModel() {
    return this.idWorkpackModel;
  }

  public void setIdWorkpackModel(final Long idWorkpackModel) {
    this.idWorkpackModel = idWorkpackModel;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public boolean isEmpty() {
    return this.children.isEmpty();
  }

  public SorterProperty<?> getSorter() {
    return this.sorter;
  }

  public boolean sameModel(final WorkpackMenuDto menu) {
    if (menu == null) return false;
    return this.idWorkpackModel.equals(menu.idWorkpackModel);
  }

}
