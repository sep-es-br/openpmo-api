package br.gov.es.openpmo.dto.menu;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;
import java.util.Set;

public class WorkpackModelMenuResponse {

  private Long id;

  private Long idPlanModel;

  private String name;

  private String nameInPlural;

  private String fontIcon;

  private String type;

  private Long position;

  private Set<WorkpackModelMenuResponse> children;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getIdPlanModel() {
    return this.idPlanModel;
  }

  public void setIdPlanModel(final Long idPlanModel) {
    this.idPlanModel = idPlanModel;
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

  public Set<WorkpackModelMenuResponse> getChildren() {
    return this.children;
  }

  public void setChildren(final Set<WorkpackModelMenuResponse> children) {
    this.children = children;
  }

  public String getNameInPlural() {
    return this.nameInPlural;
  }

  public void setNameInPlural(final String nameInPlural) {
    this.nameInPlural = nameInPlural;
  }

  public String getType() {
    return this.type;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public Long getPosition() {
    return this.position;
  }

  public void setPosition(final Long position) {
    this.position = position;
  }

  @JsonIgnore
  public Long getPositionOrElseZero() {
    return this.position == null ? 0 : this.position;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
    final WorkpackModelMenuResponse that = (WorkpackModelMenuResponse) o;
    return Objects.equals(this.id, that.id);
  }

}
