package br.gov.es.openpmo.dto.treeview;


import br.gov.es.openpmo.model.office.Office;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@QueryResult
public class OfficeTreeViewDto {

  private Long id;
  private String name;
  private Set<PlanTreeViewDto> plans;

  public OfficeTreeViewDto(final Office office) {
    this.id = office.getId();
    this.name = office.getName();
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

  public Set<PlanTreeViewDto> getPlans() {
    return Collections.unmodifiableSet(this.plans);
  }

  public void setPlans(final Set<PlanTreeViewDto> plans) {
    this.plans = Collections.unmodifiableSet(plans);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  @Override
  public boolean equals(final Object o) {
    if(this == o) return true;
    if(o == null || this.getClass() != o.getClass()) return false;
    final OfficeTreeViewDto that = (OfficeTreeViewDto) o;
    return this.id.equals(that.id);
  }

}
