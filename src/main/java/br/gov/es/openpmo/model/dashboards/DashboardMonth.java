package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;
import java.time.YearMonth;

@NodeEntity
public class DashboardMonth extends Entity {

  @Relationship("IS_PART_OF")
  private Dashboard dashboard;

  @Relationship(value = "IS_AT", direction = Relationship.INCOMING)
  private EarnedValue earnedValue;

  @Relationship(value = "IS_AT", direction = Relationship.INCOMING)
  private PerformanceIndexes performanceIndexes;

  @Relationship(value = "IS_AT", direction = Relationship.INCOMING)
  private TripleConstraint tripleConstraint;

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

  public EarnedValue getEarnedValue() {
    return earnedValue;
  }

  public void setEarnedValue(EarnedValue earnedValue) {
    if (this.earnedValue == null) {
      earnedValue.setMonth(this);
      this.earnedValue = earnedValue;
    } else {
      this.earnedValue.retain(earnedValue);
    }
  }

  public PerformanceIndexes getPerformanceIndexes() {
    return performanceIndexes;
  }

  public void setPerformanceIndexes(PerformanceIndexes performanceIndexes) {
    if (this.performanceIndexes == null) {
      performanceIndexes.setMonth(this);
      this.performanceIndexes = performanceIndexes;
    } else {
      this.performanceIndexes.retain(performanceIndexes);
    }
  }

  public TripleConstraint getTripleConstraint() {
    return tripleConstraint;
  }

  public void setTripleConstraint(TripleConstraint tripleConstraint) {
    if (this.tripleConstraint == null) {
      tripleConstraint.setMonth(this);
      this.tripleConstraint = tripleConstraint;
    } else {
      this.tripleConstraint.retain(tripleConstraint);
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

}
