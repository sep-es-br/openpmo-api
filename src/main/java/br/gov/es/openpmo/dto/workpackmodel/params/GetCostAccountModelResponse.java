package br.gov.es.openpmo.dto.workpackmodel.params;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;

import java.util.List;

public class GetCostAccountModelResponse {

  private final Long id;

  private final List<? extends PropertyModelDto> properties;

  public GetCostAccountModelResponse(
    Long id,
    List<? extends PropertyModelDto> properties
  ) {
    this.id = id;
    this.properties = properties;
  }

  public Long getId() {
    return id;
  }

  public List<? extends PropertyModelDto> getProperties() {
    return properties;
  }

}
