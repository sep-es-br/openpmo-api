package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@NodeEntity
public class Dashboard extends Entity {

  @Relationship("BELONGS_TO")
  private Workpack workpack;

  @Relationship(value = "IS_PART_OF", direction = Relationship.INCOMING)
  private List<DashboardMonth> months;

  public List<TripleConstraint> getTripleConstraint() {
    if (this.months == null) {
      return Collections.emptyList();
    }
    return this.months.stream()
      .map(DashboardMonth::getTripleConstraint)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  public List<EarnedValue> getEarnedValue() {
    if (this.months == null) {
      return Collections.emptyList();
    }
    return this.months.stream()
      .map(DashboardMonth::getEarnedValue)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  public List<PerformanceIndexes> getPerformanceIndexes() {
    if (this.months == null) {
      return Collections.emptyList();
    }
    return this.months.stream()
      .map(DashboardMonth::getPerformanceIndexes)
      .collect(Collectors.toList());
  }

  public Workpack getWorkpack() {
    return this.workpack;
  }

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

  public List<DashboardMonth> getMonths() {
    return months;
  }

  public void setMonths(List<DashboardMonth> months) {
    this.months = months;
  }

  public void addMonth(DashboardMonth month) {
    Objects.requireNonNull(month, "Month cannot be null!");
    if (this.months == null) {
      this.months = new ArrayList<>();
    }
    final boolean containsMonth = this.months.stream().anyMatch(month::isSameMonth);
    if (!containsMonth) {
      month.setDashboard(this);
      this.months.add(month);
    }
  }

  public void addMonths(Iterable<DashboardMonth> months) {
    if (months == null) {
      return;
    }
    for (DashboardMonth month : months) {
      this.addMonth(month);
    }
  }

  @Transient
  public boolean hasMonths() {
    return this.months != null && !this.months.isEmpty();
  }

  @Transient
  public Optional<DashboardMonth> monthAt(LocalDate date) {
    if (this.months == null) {
      return Optional.empty();
    }
    return this.months.stream()
      .filter(month -> month.isAt(date))
      .findFirst();
  }

  public void setTripleConstraint(TripleConstraint tripleConstraint) {
    if (tripleConstraint == null) {
      return;
    }
    final DashboardMonth tripleConstraintMonth = tripleConstraint.getMonth();
    final LocalDate date = tripleConstraintMonth.getDate();
    final Optional<DashboardMonth> maybeMonth = this.monthAt(date);
    if (maybeMonth.isPresent()) {
      final DashboardMonth month = maybeMonth.get();
      month.setTripleConstraint(tripleConstraint);
    } else {
      tripleConstraintMonth.setDashboard(this);
      tripleConstraintMonth.setTripleConstraint(tripleConstraint);
      this.addMonth(tripleConstraintMonth);
    }
  }

  public void setTripleConstraint(Iterable<TripleConstraint> tripleConstraints) {
    for (TripleConstraint tripleConstraint : tripleConstraints) {
      this.setTripleConstraint(tripleConstraint);
    }
  }

  public void setEarnedValue(EarnedValue earnedValue) {
    final DashboardMonth earnedValueMonth = earnedValue.getMonth();
    final LocalDate date = earnedValueMonth.getDate();
    final Optional<DashboardMonth> maybeMonth = this.monthAt(date);
    if (maybeMonth.isPresent()) {
      final DashboardMonth month = maybeMonth.get();
      month.setEarnedValue(earnedValue);
    } else {
      earnedValueMonth.setDashboard(this);
      earnedValueMonth.setEarnedValue(earnedValue);
      this.addMonth(earnedValueMonth);
    }
  }

  public void setEarnedValue(Iterable<EarnedValue> earnedValues) {
    for (EarnedValue earnedValue : earnedValues) {
      this.setEarnedValue(earnedValue);
    }
  }

  public void setPerformanceIndexes(PerformanceIndexes performanceIndexes) {
    final DashboardMonth performanceIndexesMonth = performanceIndexes.getMonth();
    final LocalDate date = performanceIndexesMonth.getDate();
    final Optional<DashboardMonth> maybeMonth = this.monthAt(date);
    if (maybeMonth.isPresent()) {
      final DashboardMonth month = maybeMonth.get();
      month.setPerformanceIndexes(performanceIndexes);
    } else {
      performanceIndexesMonth.setDashboard(this);
      performanceIndexesMonth.setPerformanceIndexes(performanceIndexes);
      this.addMonth(performanceIndexesMonth);
    }
  }

  public void setPerformanceIndexes(Iterable<PerformanceIndexes> performanceIndexes) {
    for (PerformanceIndexes performanceIndex : performanceIndexes) {
      this.setPerformanceIndexes(performanceIndex);
    }
  }
}
