package br.gov.es.openpmo.dto.menu;

import java.util.Set;

public class PlanModelMenuResponse {

    private Long id;
    private String name;
    private String fullName;
    private Set<WorkpackModelMenuResponse> workpackModels;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
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

    public Set<WorkpackModelMenuResponse> getWorkpackModels() {
        return this.workpackModels;
    }

    public void setWorkpackModels(final Set<WorkpackModelMenuResponse> workpackModels) {
        this.workpackModels = workpackModels;
    }

}
