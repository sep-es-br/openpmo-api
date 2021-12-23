package br.gov.es.openpmo.dto.plan;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.model.office.plan.Plan;

import java.time.LocalDate;
import java.util.List;

public class PlanDto {

  private Long id;
  private Long idPlanModel;
  private Long idOffice;
  private String name;
  private String fullName;
  private LocalDate start;
  private LocalDate finish;
  private List<PermissionDto> permissions;

  public PlanDto() {

  }

  public static PlanDto of(final Plan plan) {
    final PlanDto dto = new PlanDto();
    dto.setId(plan.getId());
    if(plan.getPlanModel() != null) {
      dto.setIdPlanModel(plan.getPlanModel().getId());
    }
    dto.setIdOffice(plan.getOffice().getId());
    dto.setName(plan.getName());
    dto.setFullName(plan.getFullName());
    dto.setStart(plan.getStart());
    dto.setFinish(plan.getFinish());
    return dto;
  }

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

  public Long getIdOffice() {
    return this.idOffice;
  }

  public void setIdOffice(final Long idOffice) {
    this.idOffice = idOffice;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public LocalDate getStart() {
    return this.start;
  }

  public void setStart(final LocalDate start) {
    this.start = start;
  }

  public LocalDate getFinish() {
    return this.finish;
  }

  public void setFinish(final LocalDate finish) {
    this.finish = finish;
  }

  public List<PermissionDto> getPermissions() {
    return this.permissions;
  }

  public void setPermissions(final List<PermissionDto> permissions) {
    this.permissions = permissions;
  }
}
