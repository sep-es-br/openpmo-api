package br.gov.es.openpmo.dto.menu;

import br.gov.es.openpmo.model.office.plan.Plan;

import java.util.Objects;

public class PlanMenuDto {

  private Long id;
  private String name;

  public PlanMenuDto() {}

  public PlanMenuDto(
    final Long id,
    final String name
  ) {
    this.id = id;
    this.name = name;
  }

  public static PlanMenuDto of(final Plan plan) {
    return new PlanMenuDto(plan.getId(), plan.getName());
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

  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.name);
  }

  @Override
  public boolean equals(final Object o) {
    if(this == o) {
      return true;
    }
    if(o == null || this.getClass() != o.getClass()) {
      return false;
    }
    final PlanMenuDto that = (PlanMenuDto) o;
    return Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name);
  }

}
