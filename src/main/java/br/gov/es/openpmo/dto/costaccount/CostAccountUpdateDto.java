package br.gov.es.openpmo.dto.costaccount;

import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.model.budget.PlanoOrcamentario;
import br.gov.es.openpmo.model.budget.UnidadeOrcamentaria;
import br.gov.es.openpmo.utils.ApplicationMessage;

import javax.validation.constraints.NotNull;
import java.util.List;

public class CostAccountUpdateDto {

  @NotNull(message = ApplicationMessage.ID_NOT_NULL)
  private Long id;

  @NotNull(message = ApplicationMessage.ID_WORKPACK_NOT_NULL)
  private Long idWorkpack;

  @NotNull(message = ApplicationMessage.ID_COST_ACCOUNT_MODEL_NOT_NULL)
  private Long idCostAccountModel;

  private List<? extends PropertyDto> properties;

  private UnidadeOrcamentaria selectedUo;

  private PlanoOrcamentario selectedPlano;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public void setIdWorkpack(final Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }

  public Long getIdCostAccountModel() {
    return idCostAccountModel;
  }

  public void setIdCostAccountModel(Long idCostAccountModel) {
    this.idCostAccountModel = idCostAccountModel;
  }

  public List<? extends PropertyDto> getProperties() {
    return this.properties;
  }

  public void setProperties(final List<? extends PropertyDto> properties) {
    this.properties = properties;
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
