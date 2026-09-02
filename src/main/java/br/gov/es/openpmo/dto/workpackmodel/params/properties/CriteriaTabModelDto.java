package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.enumerator.CriteriaOperation;
import br.gov.es.openpmo.model.properties.models.CriteriaTabModel;
import br.gov.es.openpmo.model.properties.models.GroupModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.utils.PropertyModelInstanceType;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CriteriaTabModelDto extends PropertyModelDto {

  @NotBlank
  private String icon;

  @Valid
  private List<? extends PropertyModelDto> organized;

  @Valid
  private List<? extends PropertyModelDto> properties;

  @NotNull
  private Double weight;

  @NotNull
  private CriteriaOperation operation;

  public static CriteriaTabModelDto of(final PropertyModel propertyModel) {
    final CriteriaTabModelDto instance = (CriteriaTabModelDto) PropertyModelDto.of(
      propertyModel,
      CriteriaTabModelDto::new
    );
    final CriteriaTabModel criteriaTabModel = (CriteriaTabModel) propertyModel;
    instance.setIcon(criteriaTabModel.getIcon());
    instance.setWeight(criteriaTabModel.getWeight());
    instance.setOperation(criteriaTabModel.getOperation());
    instance.setOrganized(Optional.ofNullable(criteriaTabModel.getOrganizedProperties())
      .map(properties -> properties.stream()
        .filter(GroupModel.class::isInstance)
        .map(PropertyModelInstanceType::map)
        .collect(Collectors.toList()))
      .orElse(Collections.emptyList()));
    instance.setProperties(Optional.ofNullable(criteriaTabModel.getOrganizedProperties())
      .map(properties -> properties.stream()
        .filter(property -> !(property instanceof GroupModel))
        .map(PropertyModelInstanceType::map)
        .collect(Collectors.toList()))
      .orElse(Collections.emptyList()));
    return instance;
  }

  public String getIcon() {
    return this.icon;
  }

  public void setIcon(final String icon) {
    this.icon = icon;
  }

  public List<? extends PropertyModelDto> getOrganized() {
    return this.organized;
  }

  public void setOrganized(final List<? extends PropertyModelDto> organized) {
    this.organized = organized;
  }

  public List<? extends PropertyModelDto> getProperties() {
    return this.properties;
  }

  public void setProperties(final List<? extends PropertyModelDto> properties) {
    this.properties = properties;
  }

  public Double getWeight() {
    return this.weight;
  }

  public void setWeight(final Double weight) {
    this.weight = weight;
  }

  public CriteriaOperation getOperation() {
    return this.operation;
  }

  public void setOperation(final CriteriaOperation operation) {
    this.operation = operation;
  }

}
