package br.gov.es.openpmo.dto.workpackmodel.params;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class UpdateCostAccountModelRequest {

  @NotNull
  private Long id;

  @Valid
  private List<? extends PropertyModelDto> properties;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<? extends PropertyModelDto> getProperties() {
    return properties;
  }

  public void setProperties(List<? extends PropertyModelDto> properties) {
    this.properties = properties;
  }

}
