package br.gov.es.openpmo.service.preprojects;

import br.gov.es.openpmo.dto.preprojects.PreProjectCriteriaTabValuesDto;
import br.gov.es.openpmo.dto.preprojects.PreProjectCriteriaGroupValueDto;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectCriteriaListValueDto;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectCriteriaSelectionValueDto;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectListItemDto;
import br.gov.es.openpmo.dto.preprojects.properties.PreProjectPropertyValueDto;
import br.gov.es.openpmo.model.properties.CriteriaGroup;
import br.gov.es.openpmo.model.properties.CriteriaList;
import br.gov.es.openpmo.model.properties.CriteriaSelection;
import br.gov.es.openpmo.model.properties.CriteriaTab;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.models.CriteriaGroupModel;
import br.gov.es.openpmo.model.properties.models.CriteriaListModel;
import br.gov.es.openpmo.model.properties.models.CriteriaSelectionModel;
import br.gov.es.openpmo.model.properties.models.CriteriaTabModel;
import br.gov.es.openpmo.model.properties.models.GroupModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
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
    final List<PreProjectCriteriaGroupValueDto> groups = new ArrayList<>();
    this.collect(criteriaTab.getValue(), values, groups);
    return new PreProjectCriteriaTabValuesDto(
      criteriaTab.getId(),
      criteriaTab.getDriver() == null ? null : criteriaTab.getDriver().getId(),
      values,
      groups
    );
  }

  /**
   * Builds the initial payload shown by a criterion tab without persisting any
   * runtime Property node. The model id is used as a temporary identity and is
   * resolved to the newly created runtime property when the user saves.
   */
  public PreProjectCriteriaTabValuesDto preview(final CriteriaTabModel criteriaTabModel) {
    final List<PreProjectPropertyValueDto> values = new ArrayList<>();
    final List<PreProjectCriteriaGroupValueDto> groups = new ArrayList<>();
    this.collectPreview(criteriaTabModel.getOrganizedProperties(), values, groups);
    return new PreProjectCriteriaTabValuesDto(
      null,
      criteriaTabModel.getId(),
      values,
      groups
    );
  }

  private void collect(
    final Collection<? extends Property> properties,
    final Collection<PreProjectPropertyValueDto> values,
    final Collection<PreProjectCriteriaGroupValueDto> groups
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
        final CriteriaGroup group = (CriteriaGroup) property;
        final PreProjectCriteriaGroupValueDto groupValue = new PreProjectCriteriaGroupValueDto();
        groupValue.setId(group.getId());
        groupValue.setIdPropertyModel(
          group.getPropertyModel() == null ? null : group.getPropertyModel().getId()
        );
        groupValue.setActive(group.isActive());
        // A group is a property too. Keep its runtime state in the same
        // property-value collection consumed by the front end.
        values.add(groupValue);
        groups.add(groupValue);
        this.collect(group.getValue(), values, groups);
      }
    });
  }

  private void collectPreview(
    final Collection<? extends PropertyModel> properties,
    final Collection<PreProjectPropertyValueDto> values,
    final Collection<PreProjectCriteriaGroupValueDto> groups
  ) {
    if (properties == null) {
      return;
    }
    properties.forEach(property -> {
      if (property instanceof CriteriaListModel) {
        final PreProjectCriteriaListValueDto dto = new PreProjectCriteriaListValueDto();
        this.copyPreviewIdentity(property, dto);
        dto.setItems(Collections.emptyList());
        values.add(dto);
      } else if (property instanceof CriteriaSelectionModel) {
        final PreProjectCriteriaSelectionValueDto dto = new PreProjectCriteriaSelectionValueDto();
        this.copyPreviewIdentity(property, dto);
        dto.setSelectedOptionIds(Collections.emptyList());
        values.add(dto);
      } else if (property instanceof CriteriaGroupModel) {
        final CriteriaGroupModel group = (CriteriaGroupModel) property;
        final PreProjectCriteriaGroupValueDto dto = new PreProjectCriteriaGroupValueDto();
        this.copyPreviewIdentity(group, dto);
        dto.setActive(!group.isEnablementKey());
        values.add(dto);
        groups.add(dto);
        this.collectPreview(group.getGroupedProperties(), values, groups);
      } else if (property instanceof GroupModel) {
        this.collectPreview(((GroupModel) property).getGroupedProperties(), values, groups);
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

  private void copyPreviewIdentity(
    final PropertyModel property,
    final PreProjectPropertyValueDto dto
  ) {
    // The value is sent back on the first PUT and the service resolves it by
    // idPropertyModel after creating the criterion structure.
    dto.setId(property.getId());
    dto.setIdPropertyModel(property.getId());
  }

}
