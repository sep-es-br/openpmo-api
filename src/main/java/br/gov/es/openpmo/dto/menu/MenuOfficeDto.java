package br.gov.es.openpmo.dto.menu;

import java.util.HashSet;
import java.util.Set;

public class MenuOfficeDto {
    private Long id;
    private String name;
    private String fullName;
    private Set<PlanMenuDto> plans = new HashSet<>(0);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String nome) {
        this.name = nome;
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
