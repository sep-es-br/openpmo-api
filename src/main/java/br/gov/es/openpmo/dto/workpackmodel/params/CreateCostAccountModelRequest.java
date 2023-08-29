package br.gov.es.openpmo.dto.workpackmodel.params;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CreateCostAccountModelRequest {

  @NotNull
  private Long idPlanModel;

  @Valid
  private List<? extends PropertyModelDto> properties;

  @JsonCreator
  public CreateCostAccountModelRequest(Long idPlanModel, List<? extends PropertyModelDto> properties) {
    this.idPlanModel = idPlanModel;
    this.properties = properties;
  }

  public Long getIdPlanModel() {
    return idPlanModel;
  }

  public void setIdPlanModel(Long idPlanModel) {
    this.idPlanModel = idPlanModel;
  }

  public List<? extends PropertyModelDto> getProperties() {
    return properties;
  }

  public void setProperties(List<? extends PropertyModelDto> properties) {
    this.properties = properties;
  }

}
