package br.gov.es.openpmo.dto.workpackmodel;

import java.util.List;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class TransversalStructuralLinkDto {

  private Long idTransversalProgram;
  private Long idWorkpack;
  private Long idWorkpackModel;
  private Long idParentWorkpack;
  private Long idRootWorkpackModel;
  private String rootModelName;
  private String name;
  private String fullName;
  private String modelName;
  private String modelNameInPlural;
  private String workpackModelType;
  private String fontIcon;
  private boolean directlyLinked;
  private boolean clickable;
  private List<TransversalProjectScopeCountDto> scopeCounts;

  public Long getIdTransversalProgram() { return idTransversalProgram; }
  public void setIdTransversalProgram(final Long idTransversalProgram) { this.idTransversalProgram = idTransversalProgram; }
  public Long getIdWorkpack() { return idWorkpack; }
  public void setIdWorkpack(final Long idWorkpack) { this.idWorkpack = idWorkpack; }
  public Long getIdWorkpackModel() { return idWorkpackModel; }
  public void setIdWorkpackModel(final Long idWorkpackModel) { this.idWorkpackModel = idWorkpackModel; }
  public Long getIdParentWorkpack() { return idParentWorkpack; }
  public void setIdParentWorkpack(final Long idParentWorkpack) { this.idParentWorkpack = idParentWorkpack; }
  public Long getIdRootWorkpackModel() { return idRootWorkpackModel; }
  public void setIdRootWorkpackModel(final Long idRootWorkpackModel) { this.idRootWorkpackModel = idRootWorkpackModel; }
  public String getRootModelName() { return rootModelName; }
  public void setRootModelName(final String rootModelName) { this.rootModelName = rootModelName; }
  public String getName() { return name; }
  public void setName(final String name) { this.name = name; }
  public String getFullName() { return fullName; }
  public void setFullName(final String fullName) { this.fullName = fullName; }
  public String getModelName() { return modelName; }
  public void setModelName(final String modelName) { this.modelName = modelName; }
  public String getModelNameInPlural() { return modelNameInPlural; }
  public void setModelNameInPlural(final String modelNameInPlural) { this.modelNameInPlural = modelNameInPlural; }
  public String getWorkpackModelType() { return workpackModelType; }
  public void setWorkpackModelType(final String workpackModelType) { this.workpackModelType = workpackModelType; }
  public String getFontIcon() { return fontIcon; }
  public void setFontIcon(final String fontIcon) { this.fontIcon = fontIcon; }
  public boolean isDirectlyLinked() { return directlyLinked; }
  public void setDirectlyLinked(final boolean directlyLinked) { this.directlyLinked = directlyLinked; }
  public boolean isClickable() { return clickable; }
  public void setClickable(final boolean clickable) { this.clickable = clickable; }
  public List<TransversalProjectScopeCountDto> getScopeCounts() { return scopeCounts; }
  public void setScopeCounts(final List<TransversalProjectScopeCountDto> scopeCounts) { this.scopeCounts = scopeCounts; }
}
