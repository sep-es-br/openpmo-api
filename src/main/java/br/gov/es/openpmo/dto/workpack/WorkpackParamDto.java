package br.gov.es.openpmo.dto.workpack;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({@Type(value = PortfolioParamDto.class, name = "Portfolio"),
  @Type(value = ProgramParamDto.class, name = "Program"),
  @Type(value = OrganizerParamDto.class, name = "Organizer"),
  @Type(value = DeliverableParamDto.class, name = "Deliverable"),
  @Type(value = ProjectParamDto.class, name = "Project"),
  @Type(value = MilestoneParamDto.class, name = "Milestone")})
@ApiModel(subTypes = {PortfolioParamDto.class, ProgramParamDto.class, OrganizerParamDto.class, DeliverableParamDto.class,
  ProjectParamDto.class,
  MilestoneParamDto.class}, discriminator = "type", description = "Supertype of all Workpack.")
public abstract class WorkpackParamDto {

  private Long id;

  private Long idParent;

  @NotNull
  private Long idWorkpackModel;

  @NotNull
  private Long idPlan;

  private List<? extends PropertyDto> properties;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getIdParent() {
    return this.idParent;
  }

  public void setIdParent(final Long idParent) {
    this.idParent = idParent;
  }

  public Long getIdWorkpackModel() {
    return this.idWorkpackModel;
  }

  public void setIdWorkpackModel(final Long idWorkpackModel) {
    this.idWorkpackModel = idWorkpackModel;
  }

  public Long getIdPlan() {
    return this.idPlan;
  }

  public void setIdPlan(final Long idPlan) {
    this.idPlan = idPlan;
  }

  public List<? extends PropertyDto> getProperties() {
    return this.properties;
  }

  public void setProperties(final List<? extends PropertyDto> properties) {
    this.properties = properties;
  }

}
