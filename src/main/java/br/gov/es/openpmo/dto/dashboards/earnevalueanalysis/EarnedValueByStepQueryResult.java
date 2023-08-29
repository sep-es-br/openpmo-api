package br.gov.es.openpmo.dto.dashboards.earnevalueanalysis;

import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.model.schedule.Step;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

@QueryResult
public class EarnedValueByStepQueryResult {

  private Set<Consumes> snapshotConsumesList;

  private Set<Step> snapshotStepList;

  private Set<Consumes> masterConsumesList;

  private Set<Step> masterStepList;

  private LocalDate date;

  public void setSnapshotConsumesList(final Set<Consumes> snapshotConsumesList) {
    this.snapshotConsumesList = snapshotConsumesList;
  }

  public void setSnapshotStepList(final Set<Step> snapshotStepList) {
    this.snapshotStepList = snapshotStepList;
  }

  public void setMasterConsumesList(final Set<Consumes> masterConsumesList) {
    this.masterConsumesList = masterConsumesList;
  }

  public void setMasterStepList(final Set<Step> masterStepList) {
    this.masterStepList = masterStepList;
  }

  public EarnedValueByStep toEarnedValueByStep() {
    final EarnedValueByStep result = new EarnedValueByStep();

    final BigDecimal plannedValue = sum(this.snapshotConsumesList, Consumes::getPlannedCost);
    final BigDecimal plannedWork = sum(this.snapshotStepList, Step::getPlannedWork);
    final BigDecimal actualCost = sum(this.masterConsumesList, Consumes::getActualCost);
    final BigDecimal estimatedCost = sum(this.masterConsumesList, Consumes::getPlannedCost);
    final BigDecimal actualWork = sum(this.masterStepList, Step::getActualWork);

    result.setPlannedValue(plannedValue);
    result.setActualCost(actualCost);
    result.setEstimatedCost(estimatedCost);
    result.setPlannedWork(plannedWork);
    result.setActualWork(actualWork);
    result.setDate(this.getDate());
    return result;
  }

  private YearMonth getDate() {
    return YearMonth.from(this.date);
  }

  public void setDate(final LocalDate date) {
    this.date = date;
  }

  private static <T> BigDecimal sum(final Collection<? extends T> data, final Function<T, BigDecimal> keyExtractor) {
    if (data == null) return BigDecimal.ZERO;
    return data.stream()
      .map(keyExtractor)
      .filter(Objects::nonNull)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

}
