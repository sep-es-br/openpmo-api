package br.gov.es.openpmo.dto.menu;

import java.util.List;

public class MenuPortfolioDto {
  private Long id;
  private String nome;
  private String fullName;

  private List<WorkpackMenuDto> workpack;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getNome() {
    return this.nome;
  }

  public void setNome(final String nome) {
    this.nome = nome;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public List<WorkpackMenuDto> getWorkpack() {
    return this.workpack;
  }

  public void setWorkpack(final List<WorkpackMenuDto> workpack) {
    this.workpack = workpack;
  }
}
