package br.gov.es.openpmo.dto.baselines;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import br.gov.es.openpmo.enumerator.BaselineStatus;

public class BaselineUpdateBreakdown {
  private Long idWorkpack;

  private Long idWorkpackModel;

  private Long idPlan;

  private String name;

  private String fullName;

  private String fontIcon;

  private String modelName;

  private String modelNameInPlural;

  private String type;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Boolean deliveryModelHasActiveSchedule;
  // ↳ Apenas é incluído se workpack for do tipo Deliverable
  // ↳ Diz se o modelo da Entrega exige Cronograma ativo

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<BaselineUpdateBreakdown> children;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private BaselineStatus classification;

  @JsonCreator
  public BaselineUpdateBreakdown(
    Long idWorkpack,
    Long idWorkpackModel,
    Long idPlan,
    String name,
    String fullName,
    String fontIcon,
    String modelName,
    String modelNameInPlural,
    String type,
    List<BaselineUpdateBreakdown> children
  ) {
    this.idWorkpack = idWorkpack;
    this.idWorkpackModel = idWorkpackModel;
    this.idPlan = idPlan;
    this.name = name;
    this.fullName = fullName;
    this.fontIcon = fontIcon;
    this.modelName = modelName;
    this.modelNameInPlural = modelNameInPlural;
    this.type = type;
    this.children = children;
  }

  public BaselineUpdateBreakdown(
    Long idWorkpack,
    Long idWorkpackModel,
    Long idPlan,
    String name,
    String fullName,
    String fontIcon,
    String modelName,
    String modelNameInPlural,
    String type,
    BaselineStatus classification
  ) {
    this.idWorkpack = idWorkpack;
    this.idWorkpackModel = idWorkpackModel;
    this.idPlan = idPlan;
    this.name = name;
    this.fullName = fullName;
    this.fontIcon = fontIcon;
    this.modelName = modelName;
    this.modelNameInPlural = modelNameInPlural;
    this.type = type;
    this.classification = classification;
    this.children = new ArrayList<BaselineUpdateBreakdown>();
  }

  public BaselineUpdateBreakdown(
    Long idWorkpack,
    Long idWorkpackModel,
    Long idPlan,
    String name,
    String fullName,
    String fontIcon,
    String modelName,
    String modelNameInPlural,
    String type
  ) {
    this.idWorkpack = idWorkpack;
    this.idWorkpackModel = idWorkpackModel;
    this.idPlan = idPlan;
    this.name = name;
    this.fullName = fullName;
    this.fontIcon = fontIcon;
    this.modelName = modelName;
    this.modelNameInPlural = modelNameInPlural;
    this.type = type;
    this.children = new ArrayList<BaselineUpdateBreakdown>();
  }

  public Long getIdWorkpack() {
    return idWorkpack;
  }
  public void setIdWorkpack(Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }

  public Long getIdWorkpackModel() {
    return idWorkpackModel;
  }
  public void setIdWorkpackModel(Long idWorkpackModel) {
    this.idWorkpackModel = idWorkpackModel;
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
  
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }

  public Boolean getDeliveryModelHasActiveSchedule() {
    return deliveryModelHasActiveSchedule;
  }
  public void setDeliveryModelHasActiveSchedule(Boolean deliveryModelHasActiveSchedule) {
    this.deliveryModelHasActiveSchedule = deliveryModelHasActiveSchedule;
  }

  public List<BaselineUpdateBreakdown> getChildren() {
    return children;
  }
  public void setChildren(List<BaselineUpdateBreakdown> children) {
    this.children = children;
  }
  public void addChild(BaselineUpdateBreakdown newChild) {
    this.children.add(newChild);
  }

  public BaselineStatus getClassification() {
    return classification;
  }
  public void setClassification(BaselineStatus classification) {
    this.classification = classification;
  }
}
