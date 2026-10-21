package br.gov.es.openpmo.dto.preprojects;

public class PreProjectEvaluationItemDto {

  private final Long idPropertyModel;

  private final String name;

  private final String label;

  private final Double note;

  private final Double weight;

  private final Double weightedNote;

  private final Double maximumNote;

  public PreProjectEvaluationItemDto(
    final Long idPropertyModel,
    final String name,
    final String label,
    final Double note,
    final Double weight,
    final Double maximumNote
  ) {
    this.idPropertyModel = idPropertyModel;
    this.name = name;
    this.label = label;
    this.note = note == null ? 0D : note;
    this.weight = weight == null ? 1D : weight;
    this.weightedNote = this.note * this.weight;
    this.maximumNote = maximumNote == null ? 0D : maximumNote;
  }

  public Long getIdPropertyModel() {
    return this.idPropertyModel;
  }

  public String getName() {
    return this.name;
  }

  public String getLabel() {
    return this.label;
  }

  public Double getNote() {
    return this.note;
  }

  public Double getWeight() {
    return this.weight;
  }

  public Double getWeightedNote() {
    return this.weightedNote;
  }

  public Double getMaximumNote() {
    return this.maximumNote;
  }
}
