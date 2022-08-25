package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.model.properties.Integer;
import br.gov.es.openpmo.model.properties.Number;
import br.gov.es.openpmo.model.properties.*;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.workpacks.Workpack;

import static br.gov.es.openpmo.service.workpack.GetPropertyByModel.getPropertyByModel;
import static br.gov.es.openpmo.utils.PropertyInstanceTypeDeprecated.*;

public final class GetPropertyValue {

  private GetPropertyValue() {
  }

  public static Object getValueProperty(final Workpack workpack, final PropertyModel sortBy) {
    final Property property = getPropertyByModel(workpack, sortBy);
    return getValueProperty(property);
  }

  public static Object getValueProperty(final Property property) {
    Object object = null;
    if(property != null) {
      switch(property.getClass().getTypeName()) {
        case TYPE_MODEL_NAME_INTEGER:
          final Integer integer = (Integer) property;
          object = integer.getValue();
          break;
        case TYPE_MODEL_NAME_TEXT:
          final Text text = (Text) property;
          object = text.getValue();
          break;
        case TYPE_MODEL_NAME_DATE:
          final Date date = (Date) property;
          object = date.getValue();
          break;
        case TYPE_MODEL_NAME_TOGGLE:
          final Toggle toggle = (Toggle) property;
          object = toggle.getValue();
          break;
        case TYPE_MODEL_NAME_UNIT_SELECTION:
          final UnitSelection unitSelection = (UnitSelection) property;
          object = unitSelection.getValue();
          break;
        case TYPE_MODEL_NAME_SELECTION:
          final Selection selection = (Selection) property;
          object = selection.getValue();
          break;
        case TYPE_MODEL_NAME_TEXT_AREA:
          final TextArea textArea = (TextArea) property;
          object = textArea.getValue();
          break;
        case TYPE_MODEL_NAME_NUMBER:
          final Number decimal = (Number) property;
          object = decimal.getValue();
          break;
        case TYPE_MODEL_NAME_CURRENCY:
          final Currency currency = (Currency) property;
          object = currency.getValue();
          break;
        case TYPE_MODEL_NAME_LOCALITY_SELECTION:
          final LocalitySelection localitySelection = (LocalitySelection) property;
          object = localitySelection.getValue();
          break;
        case TYPE_MODEL_NAME_ORGANIZATION_SELECTION:
          final OrganizationSelection organizationSelection = (OrganizationSelection) property;
          object = organizationSelection.getValue();
          break;
      }

    }
    return object;
  }
}
