package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.properties.SelectionOption;
import br.gov.es.openpmo.model.relations.Accepts;

public class SelectionOptionDto {

  private Long id;

  private Double value;

  private String label;

  private Long position;

  private Boolean defaultOption;

  public static SelectionOptionDto of(final Accepts accepts) {
    final SelectionOptionDto dto = new SelectionOptionDto();
    final SelectionOption option = accepts.getSelectionOption();
    if (option != null) {
      dto.setId(option.getId());
      dto.setValue(option.getValue());
      dto.setLabel(option.getLabel());
      dto.setPosition(option.getPosition());
    }
    dto.setDefaultOption(accepts.getDefaultOption());
    return dto;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Double getValue() {
    return this.value;
  }

  public void setValue(final Double value) {
    this.value = value;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

  public Long getPosition() {
    return this.position;
  }

  public void setPosition(final Long position) {
    this.position = position;
  }

  public Boolean getDefaultOption() {
    return this.defaultOption;
  }

  public void setDefaultOption(final Boolean defaultOption) {
    this.defaultOption = defaultOption;
  }

}
