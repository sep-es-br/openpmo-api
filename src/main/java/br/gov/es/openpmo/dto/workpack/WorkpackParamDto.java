package br.gov.es.openpmo.dto.workpack;

import br.gov.es.openpmo.model.workpacks.Workpack;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import io.swagger.annotations.ApiModel;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

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

  private String name;

  private String fullName;

  @NotNull
  private Long idWorkpackModel;

  @NotNull
  private Long idPlan;

  private LocalDateTime date;

  private String reason;

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public abstract Workpack getWorkpack(ModelMapper modelMapper);

}
