package br.gov.es.openpmo.dto.menu;

import java.util.ArrayList;
import java.util.List;

public class PlanMenuDto {
    private Long id;
    private String name;
    private List<WorkpackMenuDto> workpacks = new ArrayList<>(0);

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

    public List<WorkpackMenuDto> getWorkpacks() {
        return workpacks;
    }

    public void setWorkpacks(List<WorkpackMenuDto> workpacks) {
        this.workpacks = workpacks;
    }
}
