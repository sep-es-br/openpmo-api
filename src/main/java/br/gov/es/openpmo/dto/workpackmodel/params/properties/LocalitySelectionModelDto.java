package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.dto.workpack.SimpleResource;
import br.gov.es.openpmo.model.office.Locality;
import br.gov.es.openpmo.model.properties.models.LocalitySelectionModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalitySelectionModelDto extends PropertyModelDto {

  private boolean multipleSelection;
  @NotNull
  private Long idDomain;
  private List<Long> defaults;
  private List<SimpleResource> defaultsDetails;

  public static LocalitySelectionModelDto of(final PropertyModel propertyModel) {
    final LocalitySelectionModelDto instance = (LocalitySelectionModelDto) PropertyModelDto.of(
      propertyModel,
      LocalitySelectionModelDto::new
    );
    Optional.of(propertyModel)
      .map(LocalitySelectionModel.class::cast)
      .map(LocalitySelectionModel::getDefaultValue)
      .map(locality -> locality.stream().map(Locality::getId).collect(Collectors.toList()))
      .ifPresent(instance::setDefaults);
    Optional.of(propertyModel)
      .map(LocalitySelectionModel.class::cast)
      .map(LocalitySelectionModel::getDefaultValue)
      .map(values -> values.stream()
        .map(value -> SimpleResource.of(value.getId(), value.getName(), value.getFullName()))
        .collect(Collectors.toList())
      )
      .ifPresent(instance::setDefaultsDetails);
    instance.setMultipleSelection(((LocalitySelectionModel) propertyModel).isMultipleSelection());
    instance.setIdDomain(((LocalitySelectionModel) propertyModel).getDomainId());
    return instance;
  }


  public boolean isMultipleSelection() {
    return this.multipleSelection;
  }

  public void setMultipleSelection(final boolean multipleSelection) {
    this.multipleSelection = multipleSelection;
  }

  public Long getIdDomain() {
    return this.idDomain;
  }

  public void setIdDomain(final Long idDomain) {
    this.idDomain = idDomain;
  }

  public List<Long> getDefaults() {
    return this.defaults;
  }

  public void setDefaults(final List<Long> defaults) {
    this.defaults = defaults;
  }

  public List<SimpleResource> getDefaultsDetails() {
    return this.defaultsDetails;
  }

  public void setDefaultsDetails(final List<SimpleResource> defaultsDetails) {
    this.defaultsDetails = defaultsDetails;
  }
}
