package br.gov.es.openpmo.service.reports.models;

import br.gov.es.openpmo.dto.workpack.SimpleResource;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.CurrencyModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.CriteriaGroupModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.CriteriaListModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.CriteriaSelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.CriteriaTabModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.DateModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.GroupModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.IntegerModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.LocalitySelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.ListModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.NumberModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.OrganizationSelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.SelectionModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.TextAreaModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.TextModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.TabModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.ToggleModelDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.UnitSelectionModelDto;
import br.gov.es.openpmo.model.properties.models.GroupModel;
import br.gov.es.openpmo.model.properties.models.CriteriaGroupModel;
import br.gov.es.openpmo.model.properties.models.CriteriaTabModel;
import br.gov.es.openpmo.model.properties.models.LocalitySelectionModel;
import br.gov.es.openpmo.model.properties.models.OrganizationSelectionModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.UnitSelectionModel;
import br.gov.es.openpmo.model.properties.models.TabModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_CURRENCY;
import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_CRITERIA_GROUP;
import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_CRITERIA_LIST;
import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_CRITERIA_SELECTION;
import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_CRITERIA_TAB;
import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_DATE;
import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_GROUP;
import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_INTEGER;
import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_LOCALITY_SELECTION;
import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_LIST;
import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_NUMBER;
import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_ORGANIZATION_SELECTION;
import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_SELECTION;
import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_TEXT;
import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_TEXT_AREA;
import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_TAB;
import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_TOGGLE;
import static br.gov.es.openpmo.utils.PropertyModelType.TYPE_NAME_MODEL_UNIT_SELECTION;

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
          localityModel.getDefaultValue().forEach(l -> {
            localityDto.getDefaults().add(l.getId());
            localityDto.getDefaultsDetails().add(SimpleResource.of(l.getId(), l.getName(), l.getFullName()));
          });
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
      case TYPE_NAME_MODEL_TAB:
        final TabModelDto tabModelDto = TabModelDto.of(propertyModel);
        final TabModel tabModel = (TabModel) propertyModel;
        final List<PropertyModelDto> organizedProperties = new ArrayList<>();
        if (tabModel.getOrganizedProperties() != null) {
          tabModel.getOrganizedProperties().forEach(property -> organizedProperties.add(this.execute(property)));
        }
        tabModelDto.setOrganizedProperties(organizedProperties);
        return tabModelDto;
      case TYPE_NAME_MODEL_LIST:
        return ListModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_CRITERIA_TAB:
        final CriteriaTabModelDto criteriaTabModelDto = CriteriaTabModelDto.of(propertyModel);
        final CriteriaTabModel criteriaTabModel = (CriteriaTabModel) propertyModel;
        final List<PropertyModelDto> organizedCriteria = new ArrayList<>();
        final List<PropertyModelDto> standaloneCriteria = new ArrayList<>();
        if (criteriaTabModel.getOrganizedProperties() != null) {
          criteriaTabModel.getOrganizedProperties().forEach(property -> {
            final PropertyModelDto propertyModelDto = this.execute(property);
            if (property instanceof GroupModel) {
              organizedCriteria.add(propertyModelDto);
            } else {
              standaloneCriteria.add(propertyModelDto);
            }
          });
        }
        criteriaTabModelDto.setOrganized(organizedCriteria);
        criteriaTabModelDto.setProperties(standaloneCriteria);
        return criteriaTabModelDto;
      case TYPE_NAME_MODEL_CRITERIA_GROUP:
        final CriteriaGroupModelDto criteriaGroupModelDto = CriteriaGroupModelDto.of(propertyModel);
        final CriteriaGroupModel criteriaGroupModel = (CriteriaGroupModel) propertyModel;
        final List<PropertyModelDto> groupedCriteria = new ArrayList<>();
        if (criteriaGroupModel.getGroupedProperties() != null) {
          criteriaGroupModel.getGroupedProperties().forEach(property -> groupedCriteria.add(this.execute(property)));
        }
        criteriaGroupModelDto.setProperties(groupedCriteria);
        return criteriaGroupModelDto;
      case TYPE_NAME_MODEL_CRITERIA_SELECTION:
        return CriteriaSelectionModelDto.of(propertyModel);
      case TYPE_NAME_MODEL_CRITERIA_LIST:
        return CriteriaListModelDto.of(propertyModel);
      default:
        return null;
    }
  }

}
