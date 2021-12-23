package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.dto.plan.PlanDto;
import br.gov.es.openpmo.dto.workpackmodel.details.WorkpackModelDetailDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.swagger.annotations.ApiModel;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({@Type(value = PortfolioDto.class, name = "Portfolio"),
  @Type(value = ProgramDto.class, name = "Program"),
  @Type(value = OrganizerDto.class, name = "Organizer"),
  @Type(value = DeliverableDto.class, name = "Deliverable"),
  @Type(value = ProjectDto.class, name = "Project"),
  @Type(value = MilestoneDto.class, name = "Milestone")})
@ApiModel(subTypes = {PortfolioDto.class, ProgramDto.class, OrganizerDto.class, DeliverableDto.class,
  ProjectDto.class,
  MilestoneDto.class}, discriminator = "type", description = "Supertype of all Workpack.")
public abstract class WorkpackDto {

  private Long id;
  private WorkpackModelDetailDto model;
  private PlanDto plan;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public WorkpackModelDetailDto getModel() {
    return this.model;
  }

  public void setModel(final WorkpackModelDetailDto model) {
    this.model = model;
  }

  public PlanDto getPlan() {
    return this.plan;
  }

  public void setPlan(final PlanDto plan) {
    this.plan = plan;
  }

}
