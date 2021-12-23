package br.gov.es.openpmo.dto.risk;


import br.gov.es.openpmo.model.risk.Importance;
import br.gov.es.openpmo.model.risk.NatureOfRisk;
import br.gov.es.openpmo.model.risk.StatusOfRisk;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class RiskUpdateDto {

  @NotNull
  private final Long id;
  @NotNull
  private final Long idWorkpack;
  @NotNull
  @NotEmpty
  private final String name;
  @NotNull
  @NotEmpty
  private final String description;
  @NotNull
  private final Importance importance;
  @NotNull
  private final NatureOfRisk nature;
  @NotNull
  private final StatusOfRisk status;
  @JsonFormat(pattern = "dd-MM-yyyy")
  private final LocalDate likelyToHappenFrom;
  @JsonFormat(pattern = "dd-MM-yyyy")
  private final LocalDate likelyToHappenTo;
  @JsonFormat(pattern = "dd-MM-yyyy")
  private final LocalDate happenedIn;

  public RiskUpdateDto(
    final Long id,
    final Long idWorkpack,
    final String name,
    final String description,
    final Importance importance,
    final NatureOfRisk nature,
    final StatusOfRisk status,
    final LocalDate likelyToHappenFrom,
    final LocalDate likelyToHappenTo,
    final LocalDate happenedIn
  ) {
    this.id = id;
    this.idWorkpack = idWorkpack;
    this.name = name;
    this.description = description;
    this.importance = importance;
    this.nature = nature;
    this.status = status;
    this.likelyToHappenFrom = likelyToHappenFrom;
    this.likelyToHappenTo = likelyToHappenTo;
    this.happenedIn = happenedIn;
  }

  public Long getId() {
    return this.id;
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public Importance getImportance() {
    return this.importance;
  }

  public NatureOfRisk getNature() {
    return this.nature;
  }

  public StatusOfRisk getStatus() {
    return this.status;
  }

  public LocalDate getLikelyToHappenFrom() {
    return this.likelyToHappenFrom;
  }

  public LocalDate getLikelyToHappenTo() {
    return this.likelyToHappenTo;
  }

  public LocalDate getHappenedIn() {
    return this.happenedIn;
  }
}
