package br.gov.es.openpmo.model.risk;

public enum Importance {

  HIGH("high"),
  MEDIUM("medium"),
  LOW("low");

  private final String importance;

  Importance(final String importance) {

    this.importance = importance;
  }

  public String getImportance() {
    return this.importance;
  }
}
