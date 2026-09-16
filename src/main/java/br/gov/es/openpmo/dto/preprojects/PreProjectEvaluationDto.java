package br.gov.es.openpmo.dto.preprojects;

import java.util.List;

public class PreProjectEvaluationDto {

  private final Long idPreProject;

  private final List<PreProjectEvaluationCriterionDto> criteria;

  private final String operation;

  private final Double finalNote;

  public PreProjectEvaluationDto(
    final Long idPreProject,
    final String operation,
    final List<PreProjectEvaluationCriterionDto> criteria
  ) {
    this.idPreProject = idPreProject;
    this.criteria = criteria;
    this.operation = operation == null ? "AVERAGE" : operation;
    final double total = criteria.stream()
      .mapToDouble(PreProjectEvaluationCriterionDto::getTotal)
      .sum();
    final double totalWeight = criteria.stream()
      .mapToDouble(PreProjectEvaluationCriterionDto::getWeight)
      .sum();
    this.finalNote = "AVERAGE".equalsIgnoreCase(this.operation) && totalWeight > 0
      ? total / totalWeight
      : total;
  }

  public Long getIdPreProject() {
    return this.idPreProject;
  }

  public List<PreProjectEvaluationCriterionDto> getCriteria() {
    return this.criteria;
  }

  public String getOperation() {
    return this.operation;
  }

  public Double getFinalNote() {
    return this.finalNote;
  }
}
