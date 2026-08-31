package br.gov.es.openpmo.dto.preprojects;

import br.gov.es.openpmo.enumerator.CriteriaOperation;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.preprojects.models.PreProjectModel;
import java.util.List;

public class PreProjectModelDto {

  private final Long id;

  private final boolean active;

  private final CriteriaOperation operation;

  private final List<? extends PropertyModelDto> properties;

  public PreProjectModelDto(
    final PreProjectModel preProjectModel,
    final List<? extends PropertyModelDto> properties
  ) {
    this.id = preProjectModel.getId();
    this.active = preProjectModel.isActive();
    this.operation = preProjectModel.getOperation();
    this.properties = properties;
  }

  public Long getId() {
    return this.id;
  }

  public boolean isActive() {
    return this.active;
  }

  public CriteriaOperation getOperation() {
    return this.operation;
  }

  public List<? extends PropertyModelDto> getProperties() {
    return this.properties;
  }

}
