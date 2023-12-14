package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
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

  @Property("s1")
  private List<String> s1;
  @Property("s2")
  private List<String> s2;
  @Property("s3")
  private List<String> s3;

  @Property("n1")
  private List<Float> n1;
  @Property("n2")  
  private List<Float> n2; 
  @Property("n3")
  private List<Float> n3;
  
  /**
   * @return the n1
   */
  public List<Float> getN1() {
    return n1;
  }

  /**
   * @param n1 the n1 to set
   */
  public void setN1(List<Float> n1) {
    this.n1 = n1;
  }


  /**
   * @return the n2
   */
  public List<Float> getN2() {
    return n2;
  }

  /**
   * @param n2 the n2 to set
   */
  public void setN2(List<Float> n2) {
    this.n2 = n2;
  }

  /**
   * @return the n3
   */
  public List<Float> getN3() {
    return n3;
  }

  /**
   * @param n3 the n3 to set
   */
  public void setN3(List<Float> n3) {
    this.n3 = n3;
  }

  /**
   * @return the s1
   */
  public List<String> getS1() {
    return s1;
  }

  /**
   * @param s1 the s1 to set
   */
  public void setS1(List<String> s1) {
    this.s1 = s1;
  }


  /**
   * @return the s2
   */
  public List<String> getS2() {
    return s2;
  }

  /**
   * @param s2 the s2 to set
   */
  public void setS2(List<String> s2) {
    this.s2 = s2;
  }
  
  /**
   * @return the s3
   */
  public List<String> getS3() {
    return s3;
  }

  /**
   * @param s3 the s3 to set
   */
  public void setS3(List<String> s3) {
    this.s3 = s3;
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
