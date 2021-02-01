package br.gov.es.openpmo.dto.menu;

import java.util.ArrayList;
import java.util.List;

public class MenuOfficeDto {
    private Long id;
    private String nome;
    private String fullName;
    private List<PlanModelMenuDto> planModels = new ArrayList<>(0);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<PlanModelMenuDto> getPlanModels() {
        return planModels;
    }

    public void setPlanModels(List<PlanModelMenuDto> planModels) {
        this.planModels = planModels;
    }
}
