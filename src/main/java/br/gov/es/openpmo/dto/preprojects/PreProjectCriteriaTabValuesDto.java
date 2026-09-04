package br.gov.es.openpmo.dto.preprojects;

import br.gov.es.openpmo.dto.preprojects.properties.PreProjectPropertyValueDto;
import java.util.List;

public class PreProjectCriteriaTabValuesDto {

  private final Long idCriteriaTab;

  private final Long idCriteriaTabModel;

  private final List<PreProjectPropertyValueDto> values;

  public PreProjectCriteriaTabValuesDto(
    final Long idCriteriaTab,
    final Long idCriteriaTabModel,
    final List<PreProjectPropertyValueDto> values
  ) {
    this.idCriteriaTab = idCriteriaTab;
    this.idCriteriaTabModel = idCriteriaTabModel;
    this.values = values;
  }

  public Long getIdCriteriaTab() {
    return this.idCriteriaTab;
  }

  public Long getIdCriteriaTabModel() {
    return this.idCriteriaTabModel;
  }

  public List<PreProjectPropertyValueDto> getValues() {
    return this.values;
  }

}
