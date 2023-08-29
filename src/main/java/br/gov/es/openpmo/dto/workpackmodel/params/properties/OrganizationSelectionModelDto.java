package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.properties.models.OrganizationSelectionModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrganizationSelectionModelDto extends PropertyModelDto {

  private boolean multipleSelection;

  private List<Long> defaults;

  private String sectors;

  public static OrganizationSelectionModelDto of(final PropertyModel propertyModel) {
    final OrganizationSelectionModelDto instance = (OrganizationSelectionModelDto) PropertyModelDto.of(
      propertyModel,
      OrganizationSelectionModelDto::new
    );
    Optional.of(propertyModel)
      .map(OrganizationSelectionModel.class::cast)
      .map(OrganizationSelectionModel::getDefaultValue)
      .map(organizations -> organizations.stream().map(Organization::getId).collect(Collectors.toList()))
      .ifPresent(instance::setDefaults);
    instance.setMultipleSelection(((OrganizationSelectionModel) propertyModel).isMultipleSelection());
    instance.setSectors(((OrganizationSelectionModel) propertyModel).getSectors());
    return instance;
  }

  public boolean isMultipleSelection() {
    return this.multipleSelection;
  }

  public void setMultipleSelection(final boolean multipleSelection) {
    this.multipleSelection = multipleSelection;
  }

  public List<Long> getDefaults() {
    return this.defaults;
  }

  public void setDefaults(final List<Long> defaults) {
    this.defaults = defaults;
  }

  public String getSectors() {
    return sectors;
  }

  public void setSectors(String sectors) {
    this.sectors = sectors;
  }
}
