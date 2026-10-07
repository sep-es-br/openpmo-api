package br.gov.es.openpmo.dto.workpackmodel;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class TransversalProgramModelDto {

  private Long id;
  private Long idParentModel;
  private String name;
  private String nameInPlural;

  public Long getId() { return id; }
  public Long getIdParentModel() { return idParentModel; }
  public void setIdParentModel(Long idParentModel) { this.idParentModel = idParentModel; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getNameInPlural() { return nameInPlural; }
  public void setNameInPlural(String nameInPlural) { this.nameInPlural = nameInPlural; }
}
