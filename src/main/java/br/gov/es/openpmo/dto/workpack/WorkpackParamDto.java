package br.gov.es.openpmo.dto.workpack;

import java.util.List;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import io.swagger.annotations.ApiModel;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = PortfolioParamDto.class, name = "Portfolio"),
                  @JsonSubTypes.Type(value = ProgramParamDto.class, name = "Program"),
                  @JsonSubTypes.Type(value = OrganizerParamDto.class, name = "Organizer"),
                  @JsonSubTypes.Type(value = DeliverableParamDto.class, name = "Deliverable"),
                  @JsonSubTypes.Type(value = ProjectParamDto.class, name = "Project"),
                  @JsonSubTypes.Type(value = MilestoneParamDto.class, name = "Milestone") })
@ApiModel(subTypes = { PortfolioParamDto.class, ProgramParamDto.class, OrganizerParamDto.class, DeliverableParamDto.class,
    ProjectParamDto.class,
    MilestoneParamDto.class }, discriminator = "type", description = "Supertype of all Workpack.")
public abstract class WorkpackParamDto {

    private Long id;

    private Long idParent;

    @NotNull
    private Long idWorkpackModel;

    @NotNull
    private Long idPlan;

    private List<? extends PropertyDto> properties;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdParent() {
        return idParent;
    }

    public void setIdParent(Long idParent) {
        this.idParent = idParent;
    }

    public Long getIdWorkpackModel() {
        return idWorkpackModel;
    }

    public void setIdWorkpackModel(Long idWorkpackModel) {
        this.idWorkpackModel = idWorkpackModel;
    }

    public Long getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Long idPlan) {
        this.idPlan = idPlan;
    }

    public List<? extends PropertyDto> getProperties() {
        return properties;
    }

    public void setProperties(List<? extends PropertyDto> properties) {
        this.properties = properties;
    }

}
