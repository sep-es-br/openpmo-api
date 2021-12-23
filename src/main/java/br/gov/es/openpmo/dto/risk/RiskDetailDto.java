package br.gov.es.openpmo.dto.risk;


import br.gov.es.openpmo.dto.risk.response.RiskResponseDetailDto;
import br.gov.es.openpmo.model.risk.Importance;
import br.gov.es.openpmo.model.risk.NatureOfRisk;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.model.risk.StatusOfRisk;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static br.gov.es.openpmo.utils.ApplicationMessage.RISK_NOT_NULL;

public class RiskDetailDto {

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
  @JsonFormat(pattern = "yyyy-MM-dd")
  private final LocalDate likelyToHappenFrom;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private final LocalDate likelyToHappenTo;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private final LocalDate happenedIn;

  private final Set<RiskResponseDetailDto> responsePlans;

  public RiskDetailDto(
    final Long id,
    final Long idWorkpack,
    final String name,
    final String description,
    final Importance importance,
    final NatureOfRisk nature,
    final StatusOfRisk status,
    final LocalDate likelyToHappenFrom,
    final LocalDate likelyToHappenTo,
    final LocalDate happenedIn,
    final Set<RiskResponseDetailDto> responsePlans
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
    this.responsePlans = Objects.isNull(responsePlans)
      ? Collections.emptySet()
      : Collections.unmodifiableSet(responsePlans);
  }

  public static RiskDetailDto of(final Risk risk) {
    Objects.requireNonNull(risk, RISK_NOT_NULL);
    return new RiskDetailDto(
      risk.getId(),
      risk.getIdWorkpack(),
      risk.getName(),
      risk.getDescription(),
      risk.getImportance(),
      risk.getNature(),
      risk.getStatus(),
      risk.getLikelyToHappenFrom(),
      risk.getLikelyToHappenTo(),
      risk.getHappenedIn(),
      risk.getResponsesAsDetailDto()
    );
  }

  public Set<RiskResponseDetailDto> getResponsePlans() {
    return this.responsePlans;
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

  public Long getId() {
    return this.id;
  }
}
