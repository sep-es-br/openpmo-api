package br.gov.es.openpmo.dto.baselines.ccbmemberview;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;

import br.gov.es.openpmo.enumerator.BaselineStatus;

public class TripleConstraintBreakdown {
  private Long idWorkpack;

  private Long idPlan;

  private String name;

  private String fullName;

  private String fontIcon;

  private String type;

  private String modelName;

  private String modelNameInPlural;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private BaselineStatus workpackStatus;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private CostDetailItem costDetails;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private ScheduleDetailItem scheduleDetails;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private ScopeDetailItem scopeDetails;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<TripleConstraintBreakdown> children;

  @JsonCreator
  public TripleConstraintBreakdown(
    Long idWorkpack,
    Long idPlan,
    String name,
    String fullName,
    String fontIcon,
    String type,
    String modelName,
    String modelNameInPlural,
    List<TripleConstraintBreakdown> children
  ) {
    this.idWorkpack = idWorkpack;
    this.idPlan = idPlan;
    this.name = name;
    this.fullName = fullName;
    this.fontIcon = fontIcon;
    this.type = type;
    this.modelName = modelName;
    this.modelNameInPlural = modelNameInPlural;
    this.children = children;
  }

  public TripleConstraintBreakdown(
    Long idWorkpack,
    Long idPlan,
    String name,
    String fullName,
    String fontIcon,
    String type,
    String modelName,
    String modelNameInPlural
  ) {
    this.idWorkpack = idWorkpack;
    this.idPlan = idPlan;
    this.name = name;
    this.fullName = fullName;
    this.fontIcon = fontIcon;
    this.type = type;
    this.modelName = modelName;
    this.modelNameInPlural = modelNameInPlural;
    this.children = new ArrayList<TripleConstraintBreakdown>();
  }

  public Long getIdWorkpack() {
    return idWorkpack;
  }
  public void setIdWorkpack(Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }

  public Long getIdPlan() {
    return idPlan;
  }
  public void setIdPlan(Long idPlan) {
    this.idPlan = idPlan;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public String getFullName() {
    return fullName;
  }
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getFontIcon() {
    return fontIcon;
  }
  public void setFontIcon(String fontIcon) {
    this.fontIcon = fontIcon;
  }

  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }

  public String getModelName() {
    return modelName;
  }
  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  public String getModelNameInPlural() {
    return modelNameInPlural;
  }
  public void setModelNameInPlural(String modelNameInPlural) {
    this.modelNameInPlural = modelNameInPlural;
  }

  public BaselineStatus getWorkpackStatus() {
    return this.workpackStatus;
  }
  public void setWorkpackStatus(BaselineStatus workpackStatus) {
    this.workpackStatus = workpackStatus;
  }

  public CostDetailItem getCostDetails() {
    return costDetails;
  }
  public void setCostDetails(CostDetailItem costDetails) {
    this.costDetails = costDetails;
  }

  public ScheduleDetailItem getScheduleDetails() {
    return scheduleDetails;
  }
  public void setScheduleDetails(ScheduleDetailItem scheduleDetails) {
    this.scheduleDetails = scheduleDetails;
  }

  public ScopeDetailItem getScopeDetails() {
    return scopeDetails;
  }
  public void setScopeDetails(ScopeDetailItem scopeDetails) {
    this.scopeDetails = scopeDetails;
  }

  public List<TripleConstraintBreakdown> getChildren() {
    return children;
  }
  public void setChildren(List<TripleConstraintBreakdown> children) {
    this.children = children;
  }
  public void addChild(TripleConstraintBreakdown child) {
    this.children.add(child);
  }
}
