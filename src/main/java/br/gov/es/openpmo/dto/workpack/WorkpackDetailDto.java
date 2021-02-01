package br.gov.es.openpmo.dto.workpack;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.plan.PlanDto;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelDetailDto;
import io.swagger.annotations.ApiModel;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = PortfolioDetailDto.class, name = "Portfolio"),
                  @JsonSubTypes.Type(value = ProgramDetailDto.class, name = "Program"),
                  @JsonSubTypes.Type(value = OrganizerDetailDto.class, name = "Organizer"),
                  @JsonSubTypes.Type(value = DeliverableDetailDto.class, name = "Deliverable"),
                  @JsonSubTypes.Type(value = ProjectDetailDto.class, name = "Project"),
                  @JsonSubTypes.Type(value = MilestoneDetailDto.class, name = "Milestone") })
@ApiModel(subTypes = { PortfolioDetailDto.class, ProgramDetailDto.class, OrganizerDetailDto.class, DeliverableDetailDto.class,
    ProjectDetailDto.class,
    MilestoneDto.class }, discriminator = "type", description = "Supertype of all Workpack.")
public abstract class WorkpackDetailDto {

    private Long id;
    private PlanDto plan;
    private WorkpackModelDetailDto model;
    private Set<WorkpackDetailDto> children;
    private List<? extends PropertyDto> properties;
    private Set<CostAccountDto> costs;
    private List<PermissionDto> permissions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanDto getPlan() {
        return plan;
    }

    public void setPlan(PlanDto plan) {
        this.plan = plan;
    }

    public WorkpackModelDetailDto getModel() {
        return model;
    }

    public void setModel(WorkpackModelDetailDto model) {
        this.model = model;
    }

    public Set<WorkpackDetailDto> getChildren() {
        return children;
    }

    public void setChildren(Set<WorkpackDetailDto> children) {
        this.children = children;
    }

    public List<? extends PropertyDto> getProperties() {
        return properties;
    }

    public void setProperties(List<? extends PropertyDto> properties) {
        this.properties = properties;
    }

    public Set<CostAccountDto> getCosts() {
        return costs;
    }

    public void setCosts(Set<CostAccountDto> costs) {
        this.costs = costs;
    }

    public List<PermissionDto> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionDto> permissions) {
        this.permissions = permissions;
    }
}
