package br.gov.es.openpmo.dto.costaccount;

import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;

import java.util.List;

public class CostAccountDto {
  private Long id;
  private List<? extends PropertyDto> properties;
  private List<PropertyModelDto> models;
  private Long idWorkpack;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public List<? extends PropertyDto> getProperties() {
    return this.properties;
  }

  public void setProperties(final List<? extends PropertyDto> properties) {
    this.properties = properties;
  }

  public List<PropertyModelDto> getModels() {
    return this.models;
  }

  public void setModels(final List<PropertyModelDto> models) {
    this.models = models;
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public void setIdWorkpack(final Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }
}
