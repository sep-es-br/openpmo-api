package br.gov.es.openpmo.service.reports.models;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.properties.models.*;
import br.gov.es.openpmo.model.reports.ReportDesign;
import br.gov.es.openpmo.service.properties.PropertyModelService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.PROPERTY_UPDATE_TYPE_ERROR;
import static br.gov.es.openpmo.utils.PropertyModelType.*;

@Component
public class UpdatePropertyModel {

  private final PropertyModelService propertyModelService;

  public UpdatePropertyModel(PropertyModelService propertyModelService) {
    this.propertyModelService = propertyModelService;
  }

  public void execute(
    ReportDesign reportDesign,
    Collection<PropertyModel> properties
  ) {
    if (reportDesign.getPropertiesModel() == null) {
      reportDesign.setPropertiesModel(new HashSet<>());
    }
    final Set<PropertyModel> propertiesToUpdate = reportDesign.getPropertiesModel();
    this.verifyForPropertiesToDelete(
      properties,
      propertiesToUpdate
    );
    this.verifyForPropertiesToUpdate(
      properties,
      propertiesToUpdate
    );
  }

  private void verifyForPropertiesToUpdate(
    final Collection<PropertyModel> properties,
    final Collection<PropertyModel> propertiesToUpdate
  ) {
    if (properties != null && !properties.isEmpty()) {
      for (final PropertyModel propertyModel : properties) {
        if (propertyModel.getId() == null) {
          propertiesToUpdate.add(propertyModel);
          continue;
        }
        if (propertiesToUpdate != null) {
          propertiesToUpdate.stream()
            .filter(p -> p.getId() != null && p.getId().equals(propertyModel.getId()))
            .findFirst()
            .ifPresent(propertyModelUpdate -> this.loadPropertyUpdate(
              propertyModelUpdate,
              propertyModel
            ));
        }
      }
    }
  }

  private void verifyForPropertiesToDelete(
    final Collection<PropertyModel> properties,
    final Collection<PropertyModel> propertiesToUpdate
  ) {
    if (propertiesToUpdate != null && !propertiesToUpdate.isEmpty()) {
      final Predicate<PropertyModel> findPropertiesToDelete =
        propertyModel -> properties == null || properties.stream().noneMatch(p -> p.getId() != null && p.getId().equals(propertyModel.getId()));
      final Set<PropertyModel> propertiesToDelete = propertiesToUpdate.stream()
        .filter(findPropertiesToDelete)
        .collect(Collectors.toSet());
      if (!propertiesToDelete.isEmpty()) {
        this.verifyForGroupedPropertiesToDelete(propertiesToDelete);
        this.propertyModelService.delete(propertiesToDelete);
      }
    }
  }

  private void loadPropertyUpdate(
    final PropertyModel propertyModelUpdate,
    final PropertyModel propertyModel
  ) {
    if (!propertyModel.getClass().getTypeName().equals(propertyModelUpdate.getClass().getTypeName())) {
      throw new NegocioException(PROPERTY_UPDATE_TYPE_ERROR);
    }
    propertyModelUpdate.setActive(propertyModel.isActive());
    propertyModelUpdate.setFullLine(propertyModel.isFullLine());
    propertyModelUpdate.setLabel(propertyModel.getLabel());
    propertyModelUpdate.setRequired(propertyModel.isRequired());
    propertyModelUpdate.setSortIndex(propertyModel.getSortIndex());
    propertyModelUpdate.setName(propertyModel.getName());
    propertyModelUpdate.setSession(propertyModel.getSession());
    switch (propertyModelUpdate.getClass().getTypeName()) {
      case TYPE_NAME_MODEL_INTEGER:
        final IntegerModel integerModelUpdate = (IntegerModel) propertyModelUpdate;
        final IntegerModel integerModel = (IntegerModel) propertyModel;
        integerModelUpdate.setMax(integerModel.getMax());
        integerModelUpdate.setMin(integerModel.getMin());
        integerModelUpdate.setDefaultValue(integerModel.getDefaultValue());
        break;
      case TYPE_NAME_MODEL_TEXT:
        final TextModel textModelUpdate = (TextModel) propertyModelUpdate;
        final TextModel textModel = (TextModel) propertyModel;
        textModelUpdate.setMax(textModel.getMax());
        textModelUpdate.setMin(textModel.getMin());
        textModelUpdate.setDefaultValue(textModel.getDefaultValue());
        break;
      case TYPE_NAME_MODEL_DATE:
        final DateModel dateModelUpdate = (DateModel) propertyModelUpdate;
        final DateModel dateModel = (DateModel) propertyModel;
        dateModelUpdate.setMax(dateModel.getMax());
        dateModelUpdate.setMin(dateModel.getMin());
        dateModelUpdate.setDefaultValue(dateModel.getDefaultValue());
        break;
      case TYPE_NAME_MODEL_TOGGLE:
        final ToggleModel toggleModelUpdate = (ToggleModel) propertyModelUpdate;
        final ToggleModel toggleModel = (ToggleModel) propertyModel;
        toggleModelUpdate.setDefaultValue(toggleModel.isDefaultValue());
        break;
      case TYPE_NAME_MODEL_UNIT_SELECTION:
        final UnitSelectionModel unitSelectionModelUpdate = (UnitSelectionModel) propertyModelUpdate;
        final UnitSelectionModel unitSelectionModel = (UnitSelectionModel) propertyModel;
        unitSelectionModelUpdate.setDefaultValue(unitSelectionModel.getDefaultValue());
        break;
      case TYPE_NAME_MODEL_SELECTION:
        final SelectionModel selectionModelUpdate = (SelectionModel) propertyModelUpdate;
        final SelectionModel selectionModel = (SelectionModel) propertyModel;
        selectionModelUpdate.setMultipleSelection(selectionModel.isMultipleSelection());
        selectionModelUpdate.setPossibleValues(selectionModel.getPossibleValues());
        selectionModelUpdate.setDefaultValue(selectionModel.getDefaultValue());
        break;
      case TYPE_NAME_MODEL_TEXT_AREA:
        final TextAreaModel textAreaModelUpdate = (TextAreaModel) propertyModelUpdate;
        final TextAreaModel textAreaModel = (TextAreaModel) propertyModel;
        textAreaModelUpdate.setMax(textAreaModel.getMax());
        textAreaModelUpdate.setMin(textAreaModel.getMin());
        textAreaModelUpdate.setDefaultValue(textAreaModel.getDefaultValue());
        textAreaModelUpdate.setRows(textAreaModel.getRows());
        break;
      case TYPE_NAME_MODEL_NUMBER:
        final NumberModel decimalModelUpdate = (NumberModel) propertyModelUpdate;
        final NumberModel decimalModel = (NumberModel) propertyModel;
        decimalModelUpdate.setMax(decimalModel.getMax());
        decimalModelUpdate.setMin(decimalModel.getMin());
        decimalModelUpdate.setDefaultValue(decimalModel.getDefaultValue());
        decimalModelUpdate.setPrecision(decimalModel.getPrecision());
        break;
      case TYPE_NAME_MODEL_CURRENCY:
        final CurrencyModel currencyModelUpdate = (CurrencyModel) propertyModelUpdate;
        final CurrencyModel currencyModel = (CurrencyModel) propertyModel;
        currencyModelUpdate.setDefaultValue(currencyModel.getDefaultValue());
        break;
      case TYPE_NAME_MODEL_LOCALITY_SELECTION:
        final LocalitySelectionModel localitySelectionModelUpdate = (LocalitySelectionModel) propertyModelUpdate;
        final LocalitySelectionModel localitySelectionModel = (LocalitySelectionModel) propertyModel;
        localitySelectionModelUpdate.setMultipleSelection(localitySelectionModel.isMultipleSelection());
        localitySelectionModelUpdate.setDefaultValue(localitySelectionModel.getDefaultValue());
        localitySelectionModelUpdate.setDomain(localitySelectionModel.getDomain());
        break;
      case TYPE_NAME_MODEL_ORGANIZATION_SELECTION:
        final OrganizationSelectionModel organizationSelectionModelUpdate = (OrganizationSelectionModel) propertyModelUpdate;
        final OrganizationSelectionModel organizationSelectionModel = (OrganizationSelectionModel) propertyModel;
        organizationSelectionModelUpdate.setMultipleSelection(organizationSelectionModel.isMultipleSelection());
        organizationSelectionModelUpdate.setDefaultValue(organizationSelectionModel.getDefaultValue());
        break;
      case TYPE_NAME_MODEL_GROUP:
        final GroupModel groupModelUpdate = (GroupModel) propertyModelUpdate;
        final GroupModel groupModel = (GroupModel) propertyModel;
        final Set<PropertyModel> groupedProperties = groupModel.getGroupedProperties();
        if (groupModelUpdate.getGroupedProperties() == null) {
          groupModelUpdate.setGroupedProperties(new HashSet<>());
        }
        final Set<PropertyModel> groupedPropertiesToUpdate = groupModelUpdate.getGroupedProperties();
        this.verifyForPropertiesToDelete(
          groupedProperties,
          groupedPropertiesToUpdate
        );
        this.verifyForPropertiesToUpdate(
          groupedProperties,
          groupedPropertiesToUpdate
        );
        break;
    }
  }

  private void verifyForGroupedPropertiesToDelete(final Set<PropertyModel> propertiesToDelete) {
    final Set<PropertyModel> groupedProperties = extractGroupedPropertyIfExists(propertiesToDelete);
    if (!groupedProperties.isEmpty()) {
      final Collection<PropertyModel> groupedPropertiesToDelete = new HashSet<>();
      for (final PropertyModel property : groupedProperties) {
        groupedPropertiesToDelete.addAll(((GroupModel) property).getGroupedProperties());
      }
      this.propertyModelService.delete(groupedPropertiesToDelete);
    }
  }

  private static Set<PropertyModel> extractGroupedPropertyIfExists(final Set<PropertyModel> propertiesToDelete) {
    return Optional.ofNullable(propertiesToDelete)
      .map(properties -> properties.stream()
        .filter(GroupModel.class::isInstance)
        .collect(Collectors.toSet()))
      .orElse(new HashSet<>());
  }

}
