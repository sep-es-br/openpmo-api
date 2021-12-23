package br.gov.es.openpmo.dto.menu;

import java.util.HashSet;
import java.util.Set;

public class MenuOfficeDto {
  private Long id;
  private String name;
  private String fullName;
  private Set<PlanMenuDto> plans = new HashSet<>(0);

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String nome) {
    this.name = nome;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public Set<PlanMenuDto> getPlans() {
    return this.plans;
  }

  public void setPlans(final Set<PlanMenuDto> plans) {
    this.plans = plans;
  }

}
