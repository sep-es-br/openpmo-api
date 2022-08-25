package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.model.properties.Integer;
import br.gov.es.openpmo.model.properties.Number;
import br.gov.es.openpmo.model.properties.*;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.util.CollectionUtils;

import static br.gov.es.openpmo.utils.PropertyInstanceTypeDeprecated.*;

public final class GetPropertyByModel {

  private GetPropertyByModel() {
  }

  public static Property getPropertyByModel(final Workpack workpack, final PropertyModel propertyModel) {
    if(workpack == null || CollectionUtils.isEmpty(workpack.getProperties())) {
      return null;
    }
    for(final Property property : workpack.getProperties()) {
      switch(property.getClass().getTypeName()) {
        case TYPE_MODEL_NAME_INTEGER:
          final Integer integer = (Integer) property;
          if(integer.getDriver() != null && integer.getDriver().getId().equals(propertyModel.getId())) {
            return integer;
          }
          break;
        case TYPE_MODEL_NAME_TEXT:
          final Text text = (Text) property;
          if(text.getDriver() != null && text.getDriver().getId().equals(propertyModel.getId())) {
            return text;
          }
          break;
        case TYPE_MODEL_NAME_DATE:
          final Date date = (Date) property;
          if(date.getDriver() != null && date.getDriver().getId().equals(propertyModel.getId())) {
            return date;
          }
          break;
        case TYPE_MODEL_NAME_TOGGLE:
          final Toggle toggle = (Toggle) property;
          if(toggle.getDriver() != null && toggle.getDriver().getId().equals(propertyModel.getId())) {
            return toggle;
          }
          break;
        case TYPE_MODEL_NAME_UNIT_SELECTION:
          final UnitSelection unitSelection = (UnitSelection) property;
          if(unitSelection.getDriver() != null
             && unitSelection.getDriver().getId().equals(propertyModel.getId())) {
            return unitSelection;
          }
          break;
        case TYPE_MODEL_NAME_SELECTION:
          final Selection selection = (Selection) property;
          if(selection.getDriver() != null && selection.getDriver().getId().equals(propertyModel.getId())) {
            return selection;
          }
          break;
        case TYPE_MODEL_NAME_TEXT_AREA:
          final TextArea textArea = (TextArea) property;
          if(textArea.getDriver() != null && textArea.getDriver().getId().equals(propertyModel.getId())) {
            return textArea;
          }
          break;
        case TYPE_MODEL_NAME_NUMBER:
          final Number decimal = (Number) property;
          if(decimal.getDriver() != null && decimal.getDriver().getId().equals(propertyModel.getId())) {
            return decimal;
          }
          break;
        case TYPE_MODEL_NAME_CURRENCY:
          final Currency currency = (Currency) property;
          if(currency.getDriver() != null && currency.getDriver().getId().equals(propertyModel.getId())) {
            return currency;
          }
          break;
        case TYPE_MODEL_NAME_LOCALITY_SELECTION:
          final LocalitySelection localitySelection = (LocalitySelection) property;
          if(localitySelection.getDriver() != null
             && localitySelection.getDriver().getId().equals(propertyModel.getId())) {
            return localitySelection;
          }
          break;
        case TYPE_MODEL_NAME_ORGANIZATION_SELECTION:
          final OrganizationSelection organizationSelection = (OrganizationSelection) property;
          if(organizationSelection.getDriver() != null
             && organizationSelection.getDriver().getId().equals(propertyModel.getId())) {
            return organizationSelection;
          }
          break;

      }
    }
    return null;
  }

}
