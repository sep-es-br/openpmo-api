package br.gov.es.openpmo.dto.preprojects;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class PreProjectEvaluationRow {

  private Long idCriteriaTabModel;

  private String criteriaTabName;

  private String criteriaTabLabel;

  private Integer criteriaTabSortIndex;

  private Double criteriaTabWeight;

  private String criteriaTabOperation;

  private Long idGroup;

  private String groupName;

  private String groupLabel;

  private Double groupWeight;

  private String groupOperation;

  private Boolean groupActive;

  private Double groupDisabledValue;

  private Long idPropertyModel;

  private String name;

  private String label;

  private Double weight;

  private Double note;

  private Double maximumNote;

  public Long getIdCriteriaTabModel() {
    return this.idCriteriaTabModel;
  }

  public void setIdCriteriaTabModel(final Long idCriteriaTabModel) {
    this.idCriteriaTabModel = idCriteriaTabModel;
  }

  public String getCriteriaTabName() {
    return this.criteriaTabName;
  }

  public void setCriteriaTabName(final String criteriaTabName) {
    this.criteriaTabName = criteriaTabName;
  }

  public String getCriteriaTabLabel() {
    return this.criteriaTabLabel;
  }

  public void setCriteriaTabLabel(final String criteriaTabLabel) {
    this.criteriaTabLabel = criteriaTabLabel;
  }

  public Integer getCriteriaTabSortIndex() {
    return this.criteriaTabSortIndex;
  }

  public void setCriteriaTabSortIndex(final Integer criteriaTabSortIndex) {
    this.criteriaTabSortIndex = criteriaTabSortIndex;
  }

  public Double getCriteriaTabWeight() {
    return this.criteriaTabWeight;
  }

  public void setCriteriaTabWeight(final Double criteriaTabWeight) {
    this.criteriaTabWeight = criteriaTabWeight;
  }

  public String getCriteriaTabOperation() {
    return this.criteriaTabOperation;
  }

  public void setCriteriaTabOperation(final String criteriaTabOperation) {
    this.criteriaTabOperation = criteriaTabOperation;
  }

  public Long getIdGroup() {
    return this.idGroup;
  }

  public void setIdGroup(final Long idGroup) {
    this.idGroup = idGroup;
  }

  public String getGroupName() {
    return this.groupName;
  }

  public void setGroupName(final String groupName) {
    this.groupName = groupName;
  }

  public String getGroupLabel() {
    return this.groupLabel;
  }

  public void setGroupLabel(final String groupLabel) {
    this.groupLabel = groupLabel;
  }

  public Double getGroupWeight() {
    return this.groupWeight;
  }

  public void setGroupWeight(final Double groupWeight) {
    this.groupWeight = groupWeight;
  }

  public String getGroupOperation() {
    return this.groupOperation;
  }

  public void setGroupOperation(final String groupOperation) {
    this.groupOperation = groupOperation;
  }

  public Boolean getGroupActive() {
    return this.groupActive;
  }

  public void setGroupActive(final Boolean groupActive) {
    this.groupActive = groupActive;
  }

  public Double getGroupDisabledValue() {
    return this.groupDisabledValue;
  }

  public void setGroupDisabledValue(final Double groupDisabledValue) {
    this.groupDisabledValue = groupDisabledValue;
  }

  public Long getIdPropertyModel() {
    return this.idPropertyModel;
  }

  public void setIdPropertyModel(final Long idPropertyModel) {
    this.idPropertyModel = idPropertyModel;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

  public Double getWeight() {
    return this.weight;
  }

  public void setWeight(final Double weight) {
    this.weight = weight;
  }

  public Double getNote() {
    return this.note;
  }

  public void setNote(final Double note) {
    this.note = note;
  }

  public Double getMaximumNote() {
    return this.maximumNote;
  }

  public void setMaximumNote(final Double maximumNote) {
    this.maximumNote = maximumNote;
  }
}
