package br.gov.es.openpmo.dto.plan;

import java.time.LocalDate;
import java.util.List;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.model.Plan;

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

    public PlanDto(Plan plan) {
        this.id = plan.getId();
        if (plan.getPlanModel() != null) {
            this.idPlanModel = plan.getPlanModel().getId();
        }
        this.idOffice = plan.getOffice().getId();
        this.name = plan.getName();
        this.fullName = plan.getFullName();
        this.start = plan.getStart();
        this.finish = plan.getFinish();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPlanModel() {
        return this.idPlanModel;
    }

    public void setIdPlanModel(Long idPlanModel) {
        this.idPlanModel = idPlanModel;
    }

    public Long getIdOffice() {
        return this.idOffice;
    }

    public void setIdOffice(Long idOffice) {
        this.idOffice = idOffice;
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

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getFinish() {
        return finish;
    }

    public void setFinish(LocalDate finish) {
        this.finish = finish;
    }

    public List<PermissionDto> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionDto> permissions) {
        this.permissions = permissions;
    }
}
