package br.gov.es.openpmo.dto.menu;

import java.util.ArrayList;
import java.util.List;

public class PlanModelMenuDto {
    private Long id;
    private String name;
    private List<WorkpackModelMenuDto> models = new ArrayList<>(0);

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

    public List<WorkpackModelMenuDto> getModels() {
        return models;
    }

    public void setModels(List<WorkpackModelMenuDto> models) {
        this.models = models;
    }
}
