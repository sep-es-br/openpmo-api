package br.gov.es.openpmo.dto.menu;

import java.util.HashSet;
import java.util.Set;

public class MenuOfficeDto {
    private Long id;
    private String nome;
    private String fullName;
    private Set<PlanMenuDto> plans = new HashSet<>(0);

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

    public Set<PlanMenuDto> getPlans() {
        return plans;
    }

    public void setPlans(Set<PlanMenuDto> plans) {
        this.plans = plans;
    }

}
