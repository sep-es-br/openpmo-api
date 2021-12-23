package br.gov.es.openpmo.dto.risk;


import br.gov.es.openpmo.model.risk.Importance;
import br.gov.es.openpmo.model.risk.NatureOfRisk;
import br.gov.es.openpmo.model.risk.StatusOfRisk;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class RiskCreateDto {

  @NotNull
  private Long idWorkpack;
  @NotNull
  @NotEmpty
  private String name;
  @NotNull
  @NotEmpty
  private String description;
  @NotNull
  private Importance importance;
  @NotNull
  private NatureOfRisk nature;
  @NotNull
  private StatusOfRisk status;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate likelyToHappenFrom;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate likelyToHappenTo;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate happenedIn;

  public RiskCreateDto() {
  }

  public RiskCreateDto(
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

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public void setIdWorkpack(final Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public Importance getImportance() {
    return this.importance;
  }

  public void setImportance(final Importance importance) {
    this.importance = importance;
  }

  public NatureOfRisk getNature() {
    return this.nature;
  }

  public void setNature(final NatureOfRisk nature) {
    this.nature = nature;
  }

  public StatusOfRisk getStatus() {
    return this.status;
  }

  public void setStatus(final StatusOfRisk status) {
    this.status = status;
  }

  public LocalDate getLikelyToHappenFrom() {
    return this.likelyToHappenFrom;
  }

  public void setLikelyToHappenFrom(final LocalDate likelyToHappenFrom) {
    this.likelyToHappenFrom = likelyToHappenFrom;
  }

  public LocalDate getLikelyToHappenTo() {
    return this.likelyToHappenTo;
  }

  public void setLikelyToHappenTo(final LocalDate likelyToHappenTo) {
    this.likelyToHappenTo = likelyToHappenTo;
  }

  public LocalDate getHappenedIn() {
    return this.happenedIn;
  }

  public void setHappenedIn(final LocalDate happenedIn) {
    this.happenedIn = happenedIn;
  }
}
