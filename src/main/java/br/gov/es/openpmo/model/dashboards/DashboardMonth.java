package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NodeEntity
public class DashboardMonth extends Entity {

  @Relationship("IS_PART_OF")
  private Dashboard dashboard;

  @Relationship(value = "IS_AT", direction = Relationship.INCOMING)
  private List<EarnedValue> earnedValues;

  @Relationship(value = "IS_AT", direction = Relationship.INCOMING)
  private List<PerformanceIndexes> performanceIndexes;

  @Relationship(value = "IS_AT", direction = Relationship.INCOMING)
  private List<TripleConstraint> tripleConstraints;

  private LocalDate date;

  public DashboardMonth() {
  }

  public DashboardMonth(LocalDate date) {
    this.date = date;
  }

  public Dashboard getDashboard() {
    return dashboard;
  }

  public void setDashboard(Dashboard dashboard) {
    this.dashboard = dashboard;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public List<EarnedValue> getEarnedValues() {
    return earnedValues;
  }

  public void addEarnedValue(EarnedValue earnedValue) {
    if (this.earnedValues == null) {
      earnedValue.setMonth(this);
      this.earnedValues = new ArrayList<>();
      this.earnedValues.add(earnedValue);
    } else {
      for (EarnedValue value : earnedValues) {
        if (Objects.equals(value.getIdBaseline(), earnedValue.getIdBaseline())) {
          value.retain(earnedValue);
          return;
        }
      }
      earnedValue.setMonth(this);
      this.earnedValues.add(earnedValue);
    }
  }

  public List<PerformanceIndexes> getPerformanceIndexes() {
    return performanceIndexes;
  }

  public void addPerformanceIndexes(PerformanceIndexes performanceIndexes) {
    if (this.performanceIndexes == null) {
      performanceIndexes.setMonth(this);
      this.performanceIndexes = new ArrayList<>();
      this.performanceIndexes.add(performanceIndexes);
    } else {
      for (PerformanceIndexes index : this.performanceIndexes) {
        if (Objects.equals(index.getIdBaseline(), performanceIndexes.getIdBaseline())) {
          index.retain(performanceIndexes);
          return;
        }
      }
      performanceIndexes.setMonth(this);
      this.performanceIndexes.add(performanceIndexes);
    }
  }

  public List<TripleConstraint> getTripleConstraints() {
    return tripleConstraints;
  }

  public void addTripleConstraints(TripleConstraint tripleConstraint) {
    if (this.tripleConstraints == null) {
      tripleConstraint.setMonth(this);
      this.tripleConstraints = new ArrayList<>();
      this.tripleConstraints.add(tripleConstraint);
    } else {
      for (TripleConstraint constraint : this.tripleConstraints) {
        if (Objects.equals(constraint.getIdBaseline(), tripleConstraint.getIdBaseline())) {
          constraint.retain(tripleConstraint);
          return;
        }
      }
      tripleConstraint.setMonth(this);
      this.tripleConstraints.add(tripleConstraint);
    }
  }

  @Transient
  public YearMonth toYearMonth() {
    return YearMonth.from(this.date);
  }

  @Transient
  public boolean isAt(LocalDate date) {
    return this.date.getYear() == date.getYear()
      && this.date.getMonth() == date.getMonth();
  }

  @Transient
  public boolean isSameMonth(DashboardMonth month) {
    return isAt(month.date);
  }

  @Transient
  public EarnedValue getEarnedValue(Long baselineId) {
    if (this.earnedValues == null) {
      return null;
    }
    for (EarnedValue earnedValue : this.earnedValues) {
      if (Objects.equals(earnedValue.getIdBaseline(), baselineId)) {
        return earnedValue;
      }
    }
    return null;
  }

  @Transient
  public PerformanceIndexes getPerformanceIndexes(Long baselineId) {
    if (this.performanceIndexes == null) {
      return null;
    }
    for (PerformanceIndexes indexes : this.performanceIndexes) {
      if (Objects.equals(indexes.getIdBaseline(), baselineId)) {
        return indexes;
      }
    }
    return null;
  }

  @Transient
  public TripleConstraint getTripleConstraint(Long baselineId) {
    if (this.tripleConstraints == null) {
      return null;
    }
    for (TripleConstraint tripleConstraint : this.tripleConstraints) {
      if (Objects.equals(tripleConstraint.getIdBaseline(), baselineId)) {
        return tripleConstraint;
      }
    }
    return null;
  }

}
