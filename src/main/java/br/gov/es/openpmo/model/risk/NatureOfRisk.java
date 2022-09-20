package br.gov.es.openpmo.model.risk;

import br.gov.es.openpmo.model.issue.NatureOfIssue;

public enum NatureOfRisk {

  THREAT("threat", NatureOfIssue.PROBLEM),
  OPPORTUNITY("opportunity", NatureOfIssue.BENEFIT);

  private final String nature;
  private final NatureOfIssue equivalentNatureIssue;

  NatureOfRisk(
    final String nature,
    final NatureOfIssue equivalentNatureIssue
  ) {
    this.nature = nature;
    this.equivalentNatureIssue = equivalentNatureIssue;
  }

  public String getNature() {
    return this.nature;
  }

  public NatureOfIssue issue() {
    return this.equivalentNatureIssue;
  }
}

