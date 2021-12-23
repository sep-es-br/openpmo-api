package br.gov.es.openpmo.dto.filter;

import br.gov.es.openpmo.model.filter.SortByDirectionEnum;

import java.util.List;

public class CustomFilterDto {

  private Long id;
  private String name;
  private Boolean favorite;
  private SortByDirectionEnum sortByDirection;
  private String sortBy;
  private List<CustomFilterRulesDto> rules;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Boolean getFavorite() {
    return this.favorite;
  }

  public void setFavorite(final Boolean favorite) {
    this.favorite = favorite;
  }

  public SortByDirectionEnum getSortByDirection() {
    return this.sortByDirection;
  }

  public void setSortByDirection(final SortByDirectionEnum sortByDirection) {
    this.sortByDirection = sortByDirection;
  }

  public String getSortBy() {
    return this.sortBy;
  }

  public void setSortBy(final String sortBy) {
    this.sortBy = sortBy;
  }

  public List<CustomFilterRulesDto> getRules() {
    return this.rules;
  }

  public void setRules(final List<CustomFilterRulesDto> rules) {
    this.rules = rules;
  }

}
