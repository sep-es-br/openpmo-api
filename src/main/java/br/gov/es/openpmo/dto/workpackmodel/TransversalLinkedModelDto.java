package br.gov.es.openpmo.dto.workpackmodel;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class TransversalLinkedModelDto {

  private Long idWorkpackModel;
  private String type;
  private String modelName;
  private String modelNameInPlural;
  private String fontIcon;
  private boolean currentlyConfigured;

  public Long getIdWorkpackModel() { return idWorkpackModel; }
  public void setIdWorkpackModel(final Long idWorkpackModel) { this.idWorkpackModel = idWorkpackModel; }
  public String getType() { return type; }
  public void setType(final String type) { this.type = type; }
  public String getModelName() { return modelName; }
  public void setModelName(final String modelName) { this.modelName = modelName; }
  public String getModelNameInPlural() { return modelNameInPlural; }
  public void setModelNameInPlural(final String modelNameInPlural) { this.modelNameInPlural = modelNameInPlural; }
  public String getFontIcon() { return fontIcon; }
  public void setFontIcon(final String fontIcon) { this.fontIcon = fontIcon; }
  public boolean isCurrentlyConfigured() { return currentlyConfigured; }
  public void setCurrentlyConfigured(final boolean currentlyConfigured) { this.currentlyConfigured = currentlyConfigured; }
}
