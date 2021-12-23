package br.gov.es.openpmo.dto.risk;


import br.gov.es.openpmo.model.risk.Importance;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.model.risk.StatusOfRisk;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RiskCardDto {

  private final Long id;
  @NotNull
  @NotEmpty
  private final String name;
  @NotNull
  private final Importance importance;
  @NotNull
  private final StatusOfRisk status;

  public RiskCardDto(
    final Long id,
    final String name,
    final Importance importance,
    final StatusOfRisk status
  ) {
    this.id = id;
    this.name = name;
    this.importance = importance;
    this.status = status;
  }

  public static RiskCardDto of(final Risk risk) {
    return new RiskCardDto(
      risk.getId(),
      risk.getName(),
      risk.getImportance(),
      risk.getStatus()
    );
  }

  public String getName() {
    return this.name;
  }

  public Importance getImportance() {
    return this.importance;
  }

  public StatusOfRisk getStatus() {
    return this.status;
  }

  public Long getId() {
    return this.id;
  }
}
