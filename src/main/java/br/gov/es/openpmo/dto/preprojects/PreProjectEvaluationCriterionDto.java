package br.gov.es.openpmo.dto.preprojects;

import java.util.List;

/**
 * Evaluation values grouped by the criterion tab configured in the model.
 * The criterion name/label is intentionally returned from the database so
 * clients do not need to assume fixed names such as relevance or viability.
 */
public class PreProjectEvaluationCriterionDto {

  private final Long idCriteriaTabModel;

  private final String name;

  private final String label;

  private final Double weight;

  private final String operation;

  private final List<PreProjectEvaluationItemDto> items;

  private final List<PreProjectEvaluationGroupDto> groups;

  private final Double total;

  private final Double maximum;

  public PreProjectEvaluationCriterionDto(
    final Long idCriteriaTabModel,
    final String name,
    final String label,
    final Double weight,
    final String operation,
    final List<PreProjectEvaluationItemDto> items,
    final List<PreProjectEvaluationGroupDto> groups
  ) {
    this.idCriteriaTabModel = idCriteriaTabModel;
    this.name = name;
    this.label = label;
    this.weight = weight == null ? 1D : weight;
    this.operation = operation == null ? "SUM" : operation;
    this.items = items;
    this.groups = groups;
    final Double aggregate = groups.stream()
      .mapToDouble(PreProjectEvaluationGroupDto::getTotal)
      .sum() + items.stream()
      .mapToDouble(PreProjectEvaluationItemDto::getWeightedNote)
      .sum();
    final Double maximumAggregate = groups.stream()
      .mapToDouble(PreProjectEvaluationGroupDto::getMaximum)
      .sum() + items.stream()
      .mapToDouble(item -> item.getMaximumNote() * item.getWeight())
      .sum();
    final long componentCount = groups.size() + items.size();
    final Double operationResult = "AVERAGE".equalsIgnoreCase(this.operation) && componentCount > 0
      ? aggregate / componentCount
      : aggregate;
    this.total = operationResult * this.weight;
    final Double maximumResult = "AVERAGE".equalsIgnoreCase(this.operation) && componentCount > 0
      ? maximumAggregate / componentCount
      : maximumAggregate;
    this.maximum = maximumResult * this.weight;
  }

  public Long getIdCriteriaTabModel() {
    return this.idCriteriaTabModel;
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

  public List<PreProjectEvaluationItemDto> getItems() {
    return this.items;
  }

  public List<PreProjectEvaluationGroupDto> getGroups() {
    return this.groups;
  }

  public Double getTotal() {
    return this.total;
  }

  public Double getMaximum() {
    return this.maximum;
  }
}
