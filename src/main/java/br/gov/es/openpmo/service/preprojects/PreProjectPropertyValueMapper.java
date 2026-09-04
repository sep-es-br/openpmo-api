package br.gov.es.openpmo.service.preprojects;

import br.gov.es.openpmo.dto.preprojects.PreProjectCriteriaTabValuesDto;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectCriteriaListValueDto;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectCriteriaSelectionValueDto;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectListItemDto;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectPropertyValueDto;
import br.gov.es.openpmo.model.properties.CriteriaGroup;
import br.gov.es.openpmo.model.properties.CriteriaList;
import br.gov.es.openpmo.model.properties.CriteriaSelection;
import br.gov.es.openpmo.model.properties.CriteriaTab;
import br.gov.es.openpmo.model.properties.Property;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PreProjectPropertyValueMapper {

  public PreProjectCriteriaTabValuesDto execute(final CriteriaTab criteriaTab) {
    final List<PreProjectPropertyValueDto> values = new ArrayList<>();
    this.collect(criteriaTab.getValue(), values);
    return new PreProjectCriteriaTabValuesDto(
      criteriaTab.getId(),
      criteriaTab.getDriver() == null ? null : criteriaTab.getDriver().getId(),
      values
    );
  }

  private void collect(
    final Collection<? extends Property> properties,
    final Collection<PreProjectPropertyValueDto> values
  ) {
    if (properties == null) {
      return;
    }
    properties.forEach(property -> {
      if (property instanceof CriteriaList) {
        values.add(this.mapList((CriteriaList) property));
      } else if (property instanceof CriteriaSelection) {
        values.add(this.mapSelection((CriteriaSelection) property));
      } else if (property instanceof CriteriaGroup) {
        this.collect(((CriteriaGroup) property).getValue(), values);
      }
    });
  }

  private PreProjectCriteriaListValueDto mapList(final CriteriaList property) {
    final PreProjectCriteriaListValueDto dto = new PreProjectCriteriaListValueDto();
    this.copyIdentity(property, dto);
    dto.setItems(property.getValue() == null
      ? Collections.emptyList()
      : property.getValue().stream()
        .map(PreProjectListItemDto::of)
        .collect(Collectors.toList()));
    return dto;
  }

  private PreProjectCriteriaSelectionValueDto mapSelection(final CriteriaSelection property) {
    final PreProjectCriteriaSelectionValueDto dto = new PreProjectCriteriaSelectionValueDto();
    this.copyIdentity(property, dto);
    dto.setSelectedOptionIds(property.getValue() == null
      ? Collections.emptyList()
      : property.getValue().stream()
        .filter(Objects::nonNull)
        .map(option -> option.getId())
        .collect(Collectors.toList()));
    return dto;
  }

  private void copyIdentity(
    final Property property,
    final PreProjectPropertyValueDto dto
  ) {
    dto.setId(property.getId());
    dto.setIdPropertyModel(
      property.getPropertyModel() == null ? null : property.getPropertyModel().getId()
    );
  }

}
