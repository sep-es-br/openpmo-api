package br.gov.es.openpmo.dto.plan;

import br.gov.es.openpmo.model.office.plan.PlanModel;

public class PlanModelDto {

  private final Long id;
  private final String name;
  private final String fullName;

  public PlanModelDto(Long id, String name, String fullName) {
    this.id = id;
    this.name = name;
    this.fullName = fullName;
  }

  public static PlanModelDto of(final PlanModel planModel) {
    return new PlanModelDto(
      planModel.getId(),
      planModel.getName(),
      planModel.getFullName()
    );
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getFullName() {
    return fullName;
  }

}
