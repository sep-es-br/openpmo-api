package br.gov.es.openpmo.dto.workpackmodel;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class TransversalSelectionOptionDto {

  private Long id;
  private Long parentId;
  private String name;
  private String fullName;
  private Long idPlan;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getParentId() { return parentId; }
  public void setParentId(Long parentId) { this.parentId = parentId; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }
  public Long getIdPlan() { return idPlan; }
  public void setIdPlan(Long idPlan) { this.idPlan = idPlan; }
}
