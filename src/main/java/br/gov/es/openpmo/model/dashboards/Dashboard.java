package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@NodeEntity
public class Dashboard extends Entity {

  @Relationship("BELONGS_TO")
  private Workpack workpack;

  @Relationship(value = "IS_PART_OF", direction = Relationship.INCOMING)
  private List<DashboardMonth> months;

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
    if (this.months == null) {
      this.months = new ArrayList<>();
    }
    boolean containsMonth = false;
    for (DashboardMonth dashboardMonth : this.months) {
      if (month.isSameMonth(dashboardMonth)) {
        containsMonth = true;
        break;
      }
    }
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
    for (DashboardMonth month : this.months) {
      if (month.isAt(date)) {
        return Optional.of(month);
      }
    }
    return Optional.empty();
  }

  public void addTripleConstraint(TripleConstraint tripleConstraint) {
    if (tripleConstraint == null) {
      return;
    }
    final DashboardMonth tripleConstraintMonth = tripleConstraint.getMonth();
    final LocalDate date = tripleConstraintMonth.getDate();
    final Optional<DashboardMonth> maybeMonth = this.monthAt(date);
    if (maybeMonth.isPresent()) {
      final DashboardMonth month = maybeMonth.get();
      month.addTripleConstraints(tripleConstraint);
    } else {
      tripleConstraintMonth.setDashboard(this);
      tripleConstraintMonth.addTripleConstraints(tripleConstraint);
      this.addMonth(tripleConstraintMonth);
    }
  }

  public void addTripleConstraints(Iterable<TripleConstraint> tripleConstraints) {
    for (TripleConstraint tripleConstraint : tripleConstraints) {
      this.addTripleConstraint(tripleConstraint);
    }
  }

  public void addEarnedValue(EarnedValue earnedValue) {
    final DashboardMonth earnedValueMonth = earnedValue.getMonth();
    final LocalDate date = earnedValueMonth.getDate();
    final Optional<DashboardMonth> maybeMonth = this.monthAt(date);
    if (maybeMonth.isPresent()) {
      final DashboardMonth month = maybeMonth.get();
      month.addEarnedValue(earnedValue);
    } else {
      earnedValueMonth.setDashboard(this);
      earnedValueMonth.addEarnedValue(earnedValue);
      this.addMonth(earnedValueMonth);
    }
  }

  public void addEarnedValues(Iterable<EarnedValue> earnedValues) {
    for (EarnedValue earnedValue : earnedValues) {
      this.addEarnedValue(earnedValue);
    }
  }

  public void addPerformanceIndexes(PerformanceIndexes performanceIndexes) {
    final DashboardMonth performanceIndexesMonth = performanceIndexes.getMonth();
    final LocalDate date = performanceIndexesMonth.getDate();
    final Optional<DashboardMonth> maybeMonth = this.monthAt(date);
    if (maybeMonth.isPresent()) {
      final DashboardMonth month = maybeMonth.get();
      month.addPerformanceIndexes(performanceIndexes);
    } else {
      performanceIndexesMonth.setDashboard(this);
      performanceIndexesMonth.addPerformanceIndexes(performanceIndexes);
      this.addMonth(performanceIndexesMonth);
    }
  }

  public void addPerformanceIndexes(Iterable<PerformanceIndexes> performanceIndexes) {
    for (PerformanceIndexes performanceIndex : performanceIndexes) {
      this.addPerformanceIndexes(performanceIndex);
    }
  }

  @Transient
  public List<EarnedValue> getEarnedValues(Long baselineId) {
    if (this.months == null) {
      return Collections.emptyList();
    }
    List<EarnedValue> earnedValues = new ArrayList<>();
    for (DashboardMonth month : this.months) {
      EarnedValue earnedValue = month.getEarnedValue(baselineId);
      if (earnedValue != null) {
        earnedValues.add(earnedValue);
      }
    }
    return earnedValues;
  }

  @Transient
  public List<PerformanceIndexes> getPerformanceIndexes(Long baselineId) {
    if (this.months == null) {
      return Collections.emptyList();
    }
    List<PerformanceIndexes> performanceIndexes = new ArrayList<>();
    for (DashboardMonth month : this.months) {
      PerformanceIndexes indexes = month.getPerformanceIndexes(baselineId);
      if (indexes != null) {
        performanceIndexes.add(indexes);
      }
    }
    return performanceIndexes;
  }

  @Transient
  public List<TripleConstraint> getTripleConstraint(Long baselineId) {
    if (this.months == null) {
      return Collections.emptyList();
    }
    List<TripleConstraint> tripleConstraints = new ArrayList<>();
    for (DashboardMonth month : this.months) {
      final TripleConstraint tripleConstraint = month.getTripleConstraint(baselineId);
      if (tripleConstraint != null) {
        tripleConstraints.add(tripleConstraint);
      }
    }
    return tripleConstraints;
  }

  @Transient
  public List<YearMonth> getYearMonths() {
    if (this.months == null) {
      return Collections.emptyList();
    }
    final List<YearMonth> dates = new ArrayList<>();
    for (DashboardMonth month : this.months) {
      dates.add(month.toYearMonth());
    }
    return dates;
  }

}
