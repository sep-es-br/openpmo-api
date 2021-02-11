package br.gov.es.openpmo.dto.menu;

import java.util.ArrayList;
import java.util.List;

public class MenuOfficeDto {
    private Long id;
    private String nome;
    private String fullName;
    private List<PlanMenuDto> plans = new ArrayList<>(0);

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

    public List<PlanMenuDto> getPlans() {
        return plans;
    }

    public void setPlans(List<PlanMenuDto> plans) {
        this.plans = plans;
    }
}
