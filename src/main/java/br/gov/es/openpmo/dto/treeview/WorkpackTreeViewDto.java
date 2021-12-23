package br.gov.es.openpmo.dto.treeview;

import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Objects;
import java.util.Set;

@QueryResult
public class WorkpackTreeViewDto {

  private Long id;
  private String name;
  private String icon;
  private Set<WorkpackTreeViewDto> children;

  public static WorkpackTreeViewDto of(final Workpack workpack, final String workpackName) {
    final WorkpackTreeViewDto dto = new WorkpackTreeViewDto();
    dto.id = workpack.getId();
    dto.name = workpackName;
    dto.icon = workpack.getIcon();
    return dto;
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

  public String getIcon() {
    return this.icon;
  }

  public void setIcon(final String icon) {
    this.icon = icon;
  }

  public Set<WorkpackTreeViewDto> getChildren() {
    return this.children;
  }

  public void setChildren(final Set<WorkpackTreeViewDto> children) {
    this.children = children;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  @Override
  public boolean equals(final Object o) {
    if(this == o) return true;
    if(o == null || this.getClass() != o.getClass()) return false;
    final WorkpackTreeViewDto that = (WorkpackTreeViewDto) o;
    return this.id.equals(that.id);
  }
}
