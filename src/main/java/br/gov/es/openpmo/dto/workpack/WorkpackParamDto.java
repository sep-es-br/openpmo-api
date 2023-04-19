package br.gov.es.openpmo.dto.workpack;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = PortfolioParamDto.class, name = "Portfolio"),
  @JsonSubTypes.Type(value = ProgramParamDto.class, name = "Program"),
  @JsonSubTypes.Type(value = OrganizerParamDto.class, name = "Organizer"),
  @JsonSubTypes.Type(value = DeliverableParamDto.class, name = "Deliverable"),
  @JsonSubTypes.Type(value = ProjectParamDto.class, name = "Project"),
  @JsonSubTypes.Type(value = MilestoneParamDto.class, name = "Milestone")})
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

  public String getReason() {
    if (this.properties == null || this.properties.isEmpty()) {
      return null;
    }
    final Optional<DateDto> dateDto = this.properties.stream()
      .filter(DateDto.class::isInstance)
      .map(DateDto.class::cast)
      .findFirst();
    return dateDto.map(DateDto::getReason)
      .orElse(null);
  }

}
