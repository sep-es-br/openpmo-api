package br.gov.es.openpmo.dto.workpackmodel.params.properties;

import br.gov.es.openpmo.model.properties.models.CriteriaSelectionModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

public class CriteriaSelectionModelDto extends PropertyModelDto {

  @NotNull
  private Double weight;

  private List<SelectionOptionDto> acceptedOptions;

  public static CriteriaSelectionModelDto of(final PropertyModel propertyModel) {
    final CriteriaSelectionModel criteriaSelectionModel = (CriteriaSelectionModel) propertyModel;
    final CriteriaSelectionModelDto instance = (CriteriaSelectionModelDto) PropertyModelDto.of(
      propertyModel,
      CriteriaSelectionModelDto::new
    );
    instance.setWeight(criteriaSelectionModel.getWeight());
    instance.setAcceptedOptions(Optional.ofNullable(criteriaSelectionModel.getAcceptedOptions())
      .map(options -> options.stream()
        .map(SelectionOptionDto::of)
        .sorted(Comparator.comparing(SelectionOptionDto::getPosition,
          Comparator.nullsLast(Comparator.naturalOrder())))
        .collect(Collectors.toList()))
      .orElse(Collections.emptyList()));
    return instance;
  }

  public Double getWeight() {
    return this.weight;
  }

  public void setWeight(final Double weight) {
    this.weight = weight;
  }

  public List<SelectionOptionDto> getAcceptedOptions() {
    return this.acceptedOptions;
  }

  public void setAcceptedOptions(final List<SelectionOptionDto> acceptedOptions) {
    this.acceptedOptions = acceptedOptions;
  }

}
