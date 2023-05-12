package br.gov.es.openpmo.dto.menu;

import br.gov.es.openpmo.model.office.Office;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MenuOfficeDto {

  private Long id;
  private String name;
  private String fullName;
  private List<PlanMenuDto> plans;

  public MenuOfficeDto(
    final Long id,
    final String name,
    final String fullName
  ) {
    this.id = id;
    this.name = name;
    this.fullName = fullName;
    this.plans = new ArrayList<>();
  }

  public static MenuOfficeDto of(final Office office) {
    return new MenuOfficeDto(
      office.getId(),
      office.getName(),
      office.getFullName()
    );
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

  public void setName(final String nome) {
    this.name = nome;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public List<PlanMenuDto> getPlans() {
    return this.plans;
  }

  public void setPlans(final List<PlanMenuDto> plans) {
    this.plans = plans;
  }

}
