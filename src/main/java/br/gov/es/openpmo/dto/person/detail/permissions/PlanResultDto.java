package br.gov.es.openpmo.dto.person.detail.permissions;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class PlanResultDto {
    private Long id;
    private String name;
    private Long idOffice;

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

    public Long getIdOffice() {
        return idOffice;
    }

    public void setIdOffice(Long idOffice) {
        this.idOffice = idOffice;
    }
}
