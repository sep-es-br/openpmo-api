package br.gov.es.openpmo.dto.preprojects;

import java.util.List;

public class PreProjectEvaluationGroupDto {

  private final Long idGroup;

  private final String name;

  private final String label;

  private final Double weight;

  private final String operation;

  private final Boolean active;

  private final Double disabledValue;

  private final List<PreProjectEvaluationItemDto> items;

  private final Double total;

  private final Double maximum;

  public PreProjectEvaluationGroupDto(
    final Long idGroup,
    final String name,
    final String label,
    final Double weight,
    final String operation,
    final Boolean active,
    final Double disabledValue,
    final List<PreProjectEvaluationItemDto> items
  ) {
    this.idGroup = idGroup;
    this.name = name;
    this.label = label;
    this.weight = weight == null ? 1D : weight;
    this.operation = operation == null ? "SUM" : operation;
    this.active = active == null || active;
    this.disabledValue = disabledValue == null ? 0D : disabledValue;
    this.items = items;
    final Double aggregate = items.stream()
      .mapToDouble(PreProjectEvaluationItemDto::getWeightedNote)
      .sum();
    final Double maximumAggregate = items.stream()
      .mapToDouble(item -> item.getMaximumNote() * item.getWeight())
      .sum();
    final Double totalWeight = items.stream()
      .mapToDouble(PreProjectEvaluationItemDto::getWeight)
      .sum();
    final boolean average = "AVERAGE".equalsIgnoreCase(this.operation);
    this.total = this.active
      ? (average && totalWeight > 0 ? aggregate / totalWeight : aggregate) * this.weight
      : this.disabledValue;
    this.maximum = this.active
      ? (average && totalWeight > 0
      ? maximumAggregate / totalWeight
      : maximumAggregate) * this.weight
      : this.disabledValue;
  }

  public Long getIdGroup() {
    return this.idGroup;
  }

  public String getName() {
    return this.name;
  }

  public String getLabel() {
    return this.label;
  }

  public Double getWeight() {
    return this.weight;
  }

  public String getOperation() {
    return this.operation;
  }

  public Boolean getActive() {
    return this.active;
  }

  public Double getDisabledValue() {
    return this.disabledValue;
  }

  public List<PreProjectEvaluationItemDto> getItems() {
    return this.items;
  }

  public Double getTotal() {
    return this.total;
  }

  public Double getMaximum() {
    return this.maximum;
  }
}
