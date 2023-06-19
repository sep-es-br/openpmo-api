package br.gov.es.openpmo.dto.reports;

import java.util.List;

import javax.validation.constraints.NotNull;

public class ReportParamsRequest {


  @NotNull
  private Long idPropertyModel;

  @NotNull
  private String type;

  private String value;

  private Long selectedValue;

  private List<Long> selectedValues;

  public Long getIdPropertyModel() {
    return idPropertyModel;
  }

  public void setIdPropertyModel(Long idPropertyModel) {
    this.idPropertyModel = idPropertyModel;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Long getSelectedValue() {
    return selectedValue;
  }

  public void setSelectedValue(Long selectedValue) {
    this.selectedValue = selectedValue;
  }

  public List<Long> getSelectedValues() {
    return selectedValues;
  }

  public void setSelectedValues(List<Long> selectedValues) {
    this.selectedValues = selectedValues;
  }

}
