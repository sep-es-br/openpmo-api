package br.gov.es.openpmo.dto.workpackreuse;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@JsonPropertyOrder({"id", "name", "icon", "reusable", "children"})
public class ReusableWorkpackModelHierarchyDto {

  private final Long id;
  private final String name;
  private final String icon;
  private final Set<ReusableWorkpackModelHierarchyDto> children;
  @JsonIgnore
  private final Set<ReusableWorkpackModelHierarchyDto> parent;
  private boolean reusable;

  private ReusableWorkpackModelHierarchyDto(
    final Long id,
    final String name,
    final String icon,
    final Collection<ReusableWorkpackModelHierarchyDto> parent,
    final boolean reusable
  ) {
    this.id = id;
    this.name = name;
    this.icon = icon;
    this.children = new HashSet<>();
    this.parent = parent == null ? new HashSet<>() : new HashSet<>(parent);
    this.reusable = reusable;
  }

  public static ReusableWorkpackModelHierarchyDto of(final WorkpackModel model) {
    final ReusableWorkpackModelHierarchyDto reusableWorkpack = new ReusableWorkpackModelHierarchyDto(
      model.getId(),
      model.getModelName(),
      model.getFontIcon(),
      null,
      true
    );

    reusableWorkpack.addChildren(model.getChildren());

    return reusableWorkpack;
  }

  public static ReusableWorkpackModelHierarchyDto ofChild(
    final WorkpackModel model,
    final ReusableWorkpackModelHierarchyDto dto
  ) {
    final ReusableWorkpackModelHierarchyDto reusableWorkpack = new ReusableWorkpackModelHierarchyDto(
      model.getId(),
      model.getModelName(),
      model.getFontIcon(),
      Collections.singletonList(dto),
      true
    );

    reusableWorkpack.addChildren(model.getChildren());

    return reusableWorkpack;
  }

  private void addChildren(final Collection<WorkpackModel> children) {
    if(children == null) return;
    this.children.addAll(
      children.stream()
        .map(child -> ofChild(child, this))
        .collect(Collectors.toSet())
    );
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getIcon() {
    return this.icon;
  }

  public Boolean getReusable() {
    return this.reusable;
  }

  public void doNotReuseChildren() {
    this.getChildren().forEach(ReusableWorkpackModelHierarchyDto::doNotReuse);
  }

  public Set<ReusableWorkpackModelHierarchyDto> getChildren() {
    return Collections.unmodifiableSet(this.children);
  }

  public void doNotReuse() {
    this.reusable = false;
  }

  public void reuse() {
    this.reusable = true;
  }

  public void doNotReuseParent() {
    this.parent.forEach(ReusableWorkpackModelHierarchyDto::doNotReuse);
  }

  public Set<ReusableWorkpackModelHierarchyDto> getParent() {
    return Collections.unmodifiableSet(this.parent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  @Override
  public boolean equals(final Object o) {
    if(this == o) return true;
    if(!(o instanceof ReusableWorkpackModelHierarchyDto)) return false;
    final ReusableWorkpackModelHierarchyDto that = (ReusableWorkpackModelHierarchyDto) o;
    return this.id.equals(that.id);
  }

}
