package br.gov.es.openpmo.service.reports.models;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.*;
import br.gov.es.openpmo.model.properties.models.GroupModel;
import br.gov.es.openpmo.model.properties.models.LocalitySelectionModel;
import br.gov.es.openpmo.model.properties.models.OrganizationSelectionModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.UnitSelectionModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static br.gov.es.openpmo.utils.PropertyModelType.*;

@Component
public class GetPropertyModelDtoFromEntity {

  public PropertyModelDto execute(final PropertyModel propertyModel) {
    switch (propertyModel.getClass().getTypeName()) {
      case TYPE_NAME_MODEL_INTEGER:
        return IntegerModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_TEXT:
        return TextModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_DATE:
        return DateModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_TOGGLE:
        return ToggleModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_UNIT_SELECTION:
        final UnitSelectionModelDto unitDto = UnitSelectionModelDto.of(propertyModel);
        final UnitSelectionModel unitModel = (UnitSelectionModel) propertyModel;
        if (unitModel.getDefaultValue() != null) {
          unitDto.setDefaults(unitModel.getDefaultValue().getId());
        }
        return unitDto;
      case TYPE_NAME_MODEL_SELECTION:
        return SelectionModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_TEXT_AREA:
        return TextAreaModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_NUMBER:
        return NumberModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_CURRENCY:
        return CurrencyModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_LOCALITY_SELECTION:
        final LocalitySelectionModelDto localityDto = LocalitySelectionModelDto.of(propertyModel);
        final LocalitySelectionModel localityModel = (LocalitySelectionModel) propertyModel;
        if (localityModel.getDefaultValue() != null && !(localityModel.getDefaultValue()).isEmpty()) {
          localityDto.setDefaults(new ArrayList<>());
          localityModel.getDefaultValue().forEach(l -> localityDto.getDefaults().add(l.getId()));
        }
        if (localityModel.getDomain() != null) {
          localityDto.setIdDomain(localityModel.getDomain().getId());
        }
        return localityDto;
      case TYPE_NAME_MODEL_ORGANIZATION_SELECTION:
        final OrganizationSelectionModelDto organizationDto = OrganizationSelectionModelDto.of(propertyModel);
        final OrganizationSelectionModel organizationModel = (OrganizationSelectionModel) propertyModel;
        if (organizationModel.getDefaultValue() != null && !(organizationModel.getDefaultValue().isEmpty())) {
          organizationDto.setDefaults(new ArrayList<>());
          organizationModel.getDefaultValue().forEach(
            l -> organizationDto.getDefaults().add(l.getId()));
        }
        return organizationDto;
      case TYPE_NAME_MODEL_GROUP:
        final GroupModelDto groupModelDto = GroupModelDto.of(propertyModel);
        final GroupModel groupModel = (GroupModel) propertyModel;
        final List<PropertyModelDto> groupedProperties = new ArrayList<>();
        final Set<PropertyModel> properties = groupModel.getGroupedProperties();
        if (properties != null && !properties.isEmpty()) {
          properties.forEach(property -> groupedProperties.add(this.execute(property)));
        }
        groupModelDto.setGroupedProperties(groupedProperties);
        return groupModelDto;
      default:
        return null;
    }
  }

}
