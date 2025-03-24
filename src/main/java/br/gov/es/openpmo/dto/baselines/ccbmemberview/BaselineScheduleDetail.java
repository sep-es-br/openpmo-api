package br.gov.es.openpmo.dto.baselines.ccbmemberview;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.ONE_MONTH;
import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.daysBetween;
import static br.gov.es.openpmo.dto.baselines.ccbmemberview.TripleConstraintUtils.roundOneDecimal;

public class BaselineScheduleDetail {


  private final List<ScheduleDetailItem> scheduleDetails = new ArrayList<>();
  private BigDecimal variation;
  private LocalDate currentStartDate;
  private LocalDate currentEndDate;
  private LocalDate proposedStartDate;
  private LocalDate proposedEndDate;
  private BigDecimal currentValue;
  private BigDecimal proposedValue;

  private static BigDecimal calculateValue(
    final Temporal startDate,
    final Temporal endDate
  ) {
    final BigDecimal intervalDateInDays = daysBetween(startDate, endDate);

    if(intervalDateInDays == null) {
      return null;
    }
    return intervalDateInDays
      .divide(ONE_MONTH, 1, RoundingMode.HALF_UP);
  }

  public void addScheduleItem(final ScheduleDetailItem item) {
    this.scheduleDetails.add(item);
    this.updateValues();
  }

  private void updateValues() {
    this.updateCurrentDate();
    this.updateProposedDate();
    this.updateVariation();
  }

  private void updateCurrentDate() {
    this.currentStartDate = this.scheduleDetails.stream()
      .map(ScheduleDetailItem::getCurrentIntervalDate)
      .filter(Objects::nonNull)
      .map(ScheduleInterval::getInitialDate)
      .filter(Objects::nonNull)
      .min(Comparator.comparing(LocalDate::toEpochDay))
      .orElse(null);

    this.currentEndDate = this.scheduleDetails.stream()
      .map(ScheduleDetailItem::getCurrentIntervalDate)
      .filter(Objects::nonNull)
      .map(ScheduleInterval::getEndDate)
      .filter(Objects::nonNull)
      .max(Comparator.comparing(LocalDate::toEpochDay))
      .orElse(null);

    this.currentValue = calculateValue(this.currentStartDate, this.currentEndDate);
  }

  private void updateVariation() {
    if(this.currentEndDate == null || this.proposedEndDate == null) {
      return;
    }

    this.variation = daysBetween(this.currentEndDate, this.proposedEndDate);
  }

  private void updateProposedDate() {
    this.proposedStartDate = this.scheduleDetails.stream()
      .map(ScheduleDetailItem::getProposedIntervalDate)
      .filter(Objects::nonNull)
      .map(ScheduleInterval::getInitialDate)
      .filter(Objects::nonNull)
      .min(Comparator.comparing(LocalDate::toEpochDay))
      .orElse(null);

    this.proposedEndDate = this.scheduleDetails.stream()
      .map(ScheduleDetailItem::getProposedIntervalDate)
      .filter(Objects::nonNull)
      .map(ScheduleInterval::getEndDate)
      .filter(Objects::nonNull)
      .max(Comparator.comparing(LocalDate::toEpochDay))
      .orElse(null);

    this.proposedValue = calculateValue(this.proposedStartDate, this.proposedEndDate);
  }

  public void addScheduleItem(final Collection<? extends ScheduleDetailItem> itens) {
    this.scheduleDetails.addAll(itens);
  }

  public BigDecimal getVariation() {
    return this.variation;
  }

  public LocalDate getCurrentStartDate() {
    return this.currentStartDate;
  }

  public LocalDate getCurrentEndDate() {
    return this.currentEndDate;
  }

  public LocalDate getProposedStartDate() {
    return this.proposedStartDate;
  }

  public LocalDate getProposedEndDate() {
    return this.proposedEndDate;
  }

  public BigDecimal getCurrentValue() {
    return this.currentValue;
  }

  public BigDecimal getProposedValue() {
    return this.proposedValue;
  }

  public List<ScheduleDetailItem> getScheduleDetails() {
    return Collections.unmodifiableList(this.scheduleDetails);
  }

  public void roundData() {
    this.variation = roundOneDecimal(this.variation);
    this.scheduleDetails.forEach(ScheduleDetailItem::roundData);
  }

}
