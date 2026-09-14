package br.gov.es.openpmo.dto.preprojects;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class PreProjectListDto {

  private Long id;

  private String name;

  private String fullName;

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
}
