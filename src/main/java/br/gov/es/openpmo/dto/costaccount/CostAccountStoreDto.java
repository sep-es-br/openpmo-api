package br.gov.es.openpmo.dto.costaccount;

import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.model.budget.PlanoOrcamentario;
import br.gov.es.openpmo.model.budget.UnidadeOrcamentaria;
import br.gov.es.openpmo.utils.ApplicationMessage;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CostAccountStoreDto {

  @NotNull(message = ApplicationMessage.ID_WORKPACK_NOT_NULL)
  private Long idWorkpack;

  private List<? extends PropertyDto> properties;

  @NotNull(message = ApplicationMessage.ID_COST_ACCOUNT_MODEL_NOT_NULL)
  private Long idCostAccountModel;

  public CostAccountStoreDto(
    final Long idWorkpack,
    final Long idCostAccountModel,
    final List<? extends PropertyDto> properties
  ) {
    this.idWorkpack = idWorkpack;
    this.idCostAccountModel = idCostAccountModel;
    this.properties = Optional.ofNullable(properties)
      .map(Collections::unmodifiableList)
      .orElse(Collections.emptyList());
  }

  UnidadeOrcamentaria selectedUo;

  PlanoOrcamentario selectedPlano;

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public void setIdWorkpack(final Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }

  public List<? extends PropertyDto> getProperties() {
    return this.properties;
  }

  public void setProperties(final List<? extends PropertyDto> properties) {
    this.properties = properties;
  }

  public Long getIdCostAccountModel() {
    return idCostAccountModel;
  }

  public void setIdCostAccountModel(Long idCostAccountModel) {
    this.idCostAccountModel = idCostAccountModel;
  }

  public UnidadeOrcamentaria getSelectedUo() {
    return selectedUo;
  }

  public void setSelectedUo(UnidadeOrcamentaria selectedUo) {
    this.selectedUo = selectedUo;
  }

  public PlanoOrcamentario getSelectedPlano() {
    return selectedPlano;
  }

  public void setSelectedPlano(PlanoOrcamentario selectedPlano) {
    this.selectedPlano = selectedPlano;
  }
}
