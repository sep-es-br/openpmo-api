package br.gov.es.openpmo.dto.treeview;

import br.gov.es.openpmo.model.office.plan.Plan;
import org.springframework.data.neo4j.repository.query.QueryResult;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@QueryResult
public class PlanTreeViewDto {

  private Long id;
  private String name;
  private Set<WorkpackTreeViewDto> workpacks;

  public PlanTreeViewDto(final Plan plan) {
    this.id = plan.getId();
    this.name = plan.getName();
  }

  public PlanTreeViewDto(
    final Long id,
    final String name,
    final Set<WorkpackTreeViewDto> workpacks
  ) {
    this.id = id;
    this.name = name;
    this.workpacks = Collections.unmodifiableSet(workpacks);
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

  public Set<WorkpackTreeViewDto> getWorkpacks() {
    return Collections.unmodifiableSet(this.workpacks);
  }

  public void setWorkpacks(final Set<WorkpackTreeViewDto> workpacks) {
    this.workpacks = Collections.unmodifiableSet(workpacks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  @Override
  public boolean equals(final Object o) {
    if(this == o) return true;
    if(o == null || this.getClass() != o.getClass()) return false;
    final PlanTreeViewDto that = (PlanTreeViewDto) o;
    return this.id.equals(that.id);
  }

}
