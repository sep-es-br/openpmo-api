package br.gov.es.openpmo.dto.baselines.ccbmemberview;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.ONE_HUNDRED;
import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.daysBetween;

public class ScheduleDetailItem {

  private final String icon;
  private final String description;
  @JsonIgnore
  private final ScheduleInterval proposedIntervalDate;
  @JsonIgnore
  private final ScheduleInterval currentIntervalDate;
  private final LocalDate currentDate;
  private final LocalDate proposedDate;
  private BigDecimal variation;

  public ScheduleDetailItem(
    final String icon,
    final String description,
    final ScheduleInterval proposedIntervalDate,
    final ScheduleInterval currentIntervalDate
  ) {
    this.icon = icon;
    this.description = description;
    this.proposedIntervalDate = proposedIntervalDate;
    this.currentIntervalDate = currentIntervalDate;
    this.currentDate = getEndDate(currentIntervalDate);
    this.proposedDate = getEndDate(proposedIntervalDate);
    this.calculateVariation();
  }

  private void calculateVariation() {
    if(this.currentDate == null || this.proposedDate == null || this.currentDate.isEqual(this.proposedDate)) {
      this.variation = null;
      return;
    }

    final LocalDate proposedEndDate = this.proposedIntervalDate.getEndDate();
    final LocalDate currentEndDate = this.currentIntervalDate.getEndDate();
    final LocalDate currentInitialDate = this.currentIntervalDate.getInitialDate();

    final BigDecimal daysBetweenProposedAndInitialCurrent = daysBetween(
      currentInitialDate,
      proposedEndDate
    );

    final BigDecimal daysBetweenCurrentAndInitialCurrent = daysBetween(
      currentInitialDate,
      currentEndDate
    );

    if(BigDecimal.ZERO.compareTo(daysBetweenCurrentAndInitialCurrent) == 0
       || BigDecimal.ZERO.compareTo(daysBetweenProposedAndInitialCurrent) == 0) {
      this.variation = null;
      return;
    }
    this.variation = daysBetweenProposedAndInitialCurrent
      .divide(daysBetweenCurrentAndInitialCurrent, 6, RoundingMode.HALF_EVEN)
      .subtract(BigDecimal.ONE)
      .multiply(ONE_HUNDRED);

  }


  private static LocalDate getEndDate(final ScheduleInterval currentIntervalDate) {
    return Optional.ofNullable(currentIntervalDate)
      .map(ScheduleInterval::getEndDate)
      .orElse(null);
  }

  public String getIcon() {
    return this.icon;
  }

  public String getDescription() {
    return this.description;
  }

  public LocalDate getCurrentDate() {
    return this.currentDate;
  }

  public LocalDate getProposedDate() {
    return this.proposedDate;
  }

  public BigDecimal getVariation() {
    return this.variation;
  }

  public void roundData() {
    this.variation = TripleConstraintUtils.roundOneDecimal(this.variation);
  }

}
