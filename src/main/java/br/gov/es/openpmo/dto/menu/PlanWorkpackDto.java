package br.gov.es.openpmo.dto.menu;

import java.util.List;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class PlanWorkpackDto {
    private Long idPlan;
    private List<Long> workpacks;

    public Long getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Long idPlan) {
        this.idPlan = idPlan;
    }

    public List<Long> getWorkpacks() {
        return workpacks;
    }

    public void setWorkpacks(List<Long> workpacks) {
        this.workpacks = workpacks;
    }
}
