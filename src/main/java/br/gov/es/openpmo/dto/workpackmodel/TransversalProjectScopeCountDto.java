package br.gov.es.openpmo.dto.workpackmodel;

public class TransversalProjectScopeCountDto {

  private Long idProject;
  private Long idWorkpackModel;
  private String modelNameInPlural;
  private String fontIcon;
  private long linkedCount;
  private long totalCount;

  public Long getIdProject() { return idProject; }
  public void setIdProject(final Long idProject) { this.idProject = idProject; }
  public Long getIdWorkpackModel() { return idWorkpackModel; }
  public void setIdWorkpackModel(final Long idWorkpackModel) { this.idWorkpackModel = idWorkpackModel; }
  public String getModelNameInPlural() { return modelNameInPlural; }
  public void setModelNameInPlural(final String modelNameInPlural) { this.modelNameInPlural = modelNameInPlural; }
  public String getFontIcon() { return fontIcon; }
  public void setFontIcon(final String fontIcon) { this.fontIcon = fontIcon; }
  public long getLinkedCount() { return linkedCount; }
  public void setLinkedCount(final long linkedCount) { this.linkedCount = linkedCount; }
  public long getTotalCount() { return totalCount; }
  public void setTotalCount(final long totalCount) { this.totalCount = totalCount; }
}
