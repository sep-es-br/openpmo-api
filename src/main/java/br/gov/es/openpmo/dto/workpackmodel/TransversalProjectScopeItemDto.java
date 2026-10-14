package br.gov.es.openpmo.dto.workpackmodel;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class TransversalProjectScopeItemDto {

  private Long idProject;
  private Long idWorkpack;
  private Long idWorkpackModel;
  private String modelNameInPlural;
  private String fontIcon;
  private boolean linked;

  public Long getIdProject() { return idProject; }
  public void setIdProject(final Long idProject) { this.idProject = idProject; }
  public Long getIdWorkpack() { return idWorkpack; }
  public void setIdWorkpack(final Long idWorkpack) { this.idWorkpack = idWorkpack; }
  public Long getIdWorkpackModel() { return idWorkpackModel; }
  public void setIdWorkpackModel(final Long idWorkpackModel) { this.idWorkpackModel = idWorkpackModel; }
  public String getModelNameInPlural() { return modelNameInPlural; }
  public void setModelNameInPlural(final String modelNameInPlural) { this.modelNameInPlural = modelNameInPlural; }
  public String getFontIcon() { return fontIcon; }
  public void setFontIcon(final String fontIcon) { this.fontIcon = fontIcon; }
  public boolean isLinked() { return linked; }
  public void setLinked(final boolean linked) { this.linked = linked; }
}
