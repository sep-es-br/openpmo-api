package br.gov.es.openpmo.dto.domain;

import java.util.Set;

public class LocalityPropertyDto {

  private Long id;
  private String name;
  private String fullName;
  private Set<LocalityPropertyDto> children;

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

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public Set<LocalityPropertyDto> getChildren() {
    return this.children;
  }

  public void setChildren(final Set<LocalityPropertyDto> children) {
    this.children = children;
  }

}
