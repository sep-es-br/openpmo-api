package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.PerformanceIndexesByStep;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

import static br.gov.es.openpmo.model.dashboards.DashboardUtils.apply;

public class PerformanceIndexesData {

  private Long idBaseline;

  private BigDecimal actualCost;

  private BigDecimal plannedValue;

  private BigDecimal earnedValue;

  private BigDecimal estimatesAtCompletion;

  private BigDecimal estimateToComplete;

  private CostPerformanceIndexData costPerformanceIndex;

  private SchedulePerformanceIndexData schedulePerformanceIndex;

  @JsonFormat(pattern = "yyyy-MM")
  private YearMonth date;

  public static PerformanceIndexesData of(final PerformanceIndexesByStep from) {
    if (from == null) {
      return null;
    }

    final PerformanceIndexesData to = new PerformanceIndexesData();

    to.setIdBaseline(from.getIdBaseline());
    to.setActualCost(from.getActualCost());
    to.setPlannedValue(from.getPlannedValue());
    to.setEarnedValue(from.getEarnedValue());
    to.setEstimatesAtCompletion(from.getEstimatesAtCompletion());
    to.setEstimateToComplete(from.getEstimateToComplete());
    to.setDate(from.getDate());

    apply(from.getCostPerformanceIndex(), CostPerformanceIndexData::of, to::setCostPerformanceIndex);
    apply(from.getSchedulePerformanceIndex(), SchedulePerformanceIndexData::of, to::setSchedulePerformanceIndex);

    return to;
  }

  public static PerformanceIndexesData of(final PerformanceIndexes from) {
    if (from == null) {
      return null;
    }

    final PerformanceIndexesData to = new PerformanceIndexesData();

    to.setIdBaseline(from.getIdBaseline());
    to.setActualCost(from.getActualCost());
    to.setPlannedValue(from.getPlannedValue());
    to.setEarnedValue(from.getEarnedValue());
    to.setEstimatesAtCompletion(from.getEstimatesAtCompletion());
    to.setEstimateToComplete(from.getEstimateToComplete());
    to.setDate(from.getMonth().toYearMonth());
    to.setCostPerformanceIndex(CostPerformanceIndexData.of(from));
    to.setSchedulePerformanceIndex(SchedulePerformanceIndexData.of(from));

    return to;
  }

  public Long getIdBaseline() {
    return idBaseline;
  }

  public void setIdBaseline(Long idBaseline) {
    this.idBaseline = idBaseline;
  }

  public BigDecimal getActualCost() {
    return this.actualCost;
  }

  public void setActualCost(final BigDecimal actualCost) {
    this.actualCost = actualCost;
  }

  public BigDecimal getPlannedValue() {
    return this.plannedValue;
  }

  public void setPlannedValue(final BigDecimal plannedValue) {
    this.plannedValue = plannedValue;
  }

  public BigDecimal getEarnedValue() {
    return this.earnedValue;
  }

  public void setEarnedValue(final BigDecimal earnedValue) {
    this.earnedValue = earnedValue;
  }

  public BigDecimal getEstimatesAtCompletion() {
    return this.estimatesAtCompletion;
  }

  public void setEstimatesAtCompletion(final BigDecimal estimatesAtCompletion) {
    this.estimatesAtCompletion = estimatesAtCompletion;
  }

  public BigDecimal getEstimateToComplete() {
    return this.estimateToComplete;
  }

  public void setEstimateToComplete(final BigDecimal estimateToComplete) {
    this.estimateToComplete = estimateToComplete;
  }

  public CostPerformanceIndexData getCostPerformanceIndex() {
    return this.costPerformanceIndex;
  }

  public void setCostPerformanceIndex(final CostPerformanceIndexData costPerformanceIndex) {
    this.costPerformanceIndex = costPerformanceIndex;
  }

  public SchedulePerformanceIndexData getSchedulePerformanceIndex() {
    return this.schedulePerformanceIndex;
  }

  public void setSchedulePerformanceIndex(final SchedulePerformanceIndexData schedulePerformanceIndex) {
    this.schedulePerformanceIndex = schedulePerformanceIndex;
  }

  public YearMonth getDate() {
    return this.date;
  }

  public void setDate(final YearMonth date) {
    this.date = date;
  }

  public PerformanceIndexesByStep getResponse() {
    return new PerformanceIndexesByStep(
      this.idBaseline,
      this.actualCost,
      this.plannedValue,
      this.earnedValue,
      this.estimatesAtCompletion,
      this.estimateToComplete,
      Optional.ofNullable(this.costPerformanceIndex).map(CostPerformanceIndexData::getResponse).orElse(null),
      Optional.ofNullable(this.schedulePerformanceIndex).map(SchedulePerformanceIndexData::getResponse).orElse(null),
      this.date
    );
  }

}
