package br.gov.es.openpmo.dto.planmodel;

import br.gov.es.openpmo.model.PlanModel;

public class PlanModelDto {

    private Long id;
    private String name;
    private String fullName;
    private Long idOffice;

    public PlanModelDto() {

    }

    public PlanModelDto(PlanModel planModel) {
        this.id = planModel.getId();
        this.name = planModel.getName();
        this.fullName = planModel.getFullName();
        if (planModel.getOffice() != null) {
            this.idOffice = planModel.getOffice().getId();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getIdOffice() {
        return idOffice;
    }

    public void setIdOffice(Long idOffice) {
        this.idOffice = idOffice;
    }
}
