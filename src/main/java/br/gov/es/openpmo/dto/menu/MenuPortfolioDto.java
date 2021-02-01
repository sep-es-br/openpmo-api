package br.gov.es.openpmo.dto.menu;

import java.util.List;

public class MenuPortfolioDto {
    private Long id;
    private String nome;
    private String fullName;

    private List<WorkpackMenuDto> workpack;

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

    public List<WorkpackMenuDto> getWorkpack() {
        return workpack;
    }

    public void setWorkpack(List<WorkpackMenuDto> workpack) {
        this.workpack = workpack;
    }
}
