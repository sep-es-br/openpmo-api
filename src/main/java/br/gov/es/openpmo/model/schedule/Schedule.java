package br.gov.es.openpmo.model.schedule;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.baselines.Snapshotable;
import br.gov.es.openpmo.model.relations.IsScheduleSnapshotOf;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@NodeEntity
public class Schedule extends Entity implements Snapshotable<Schedule> {

  private LocalDate end;

  private LocalDate start;

  @Relationship(type = "COMPOSES")
  private Baseline baseline;

  @Relationship(type = "FEATURES")
  private Workpack workpack;

  @Relationship(type = "IS_SNAPSHOT_OF")
  private IsScheduleSnapshotOf master;

  @Relationship(type = "IS_SNAPSHOT_OF", direction = INCOMING)
  private Set<IsScheduleSnapshotOf> snapshots;

  @Relationship(type = "COMPOSES", direction = Relationship.INCOMING)
  private Set<Step> steps;

  private CategoryEnum category;

  public Schedule() {
  }

  public Workpack getWorkpack() {
    return this.workpack;
  }

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

  public Set<Step> getSteps() {
    return this.steps;
  }

  public void setSteps(final Set<Step> steps) {
    this.steps = steps;
  }

  @Override
  public Schedule snapshot() {
    final Schedule snapshot = new Schedule();

    snapshot.start = this.start;
    snapshot.end = this.end;

    return snapshot;
  }

  @Override
  public Baseline getBaseline() {
    return this.baseline;
  }

  @Override
  public void setBaseline(final Baseline baseline) {
    this.baseline = baseline;
  }

  @Override
  public CategoryEnum getCategory() {
    return this.category;
  }

  @Override
  public void setCategory(final CategoryEnum category) {
    this.category = category;
  }

  @Override
  public boolean hasChanges(final Schedule other) {
    final boolean hasStartChanges = (this.start != null || other.start != null)
                                    && (this.start != null && other.start == null || this.start == null || !this.start.equals(other.start));

    final boolean hasEndChanges = (this.end != null || other.end != null)
                                  && (this.end != null && other.end == null || this.end == null || !this.end.equals(other.end));

    return hasStartChanges || hasEndChanges;
  }

  public LocalDate getStart() {
    return this.start;
  }

  public void setStart(final LocalDate start) {
    this.start = start;
  }

  public LocalDate getEnd() {
    return this.end;
  }

  public void setEnd(final LocalDate end) {
    this.end = end;
  }

  public IsScheduleSnapshotOf getMaster() {
    return this.master;
  }

  public void setMaster(final IsScheduleSnapshotOf master) {
    this.master = master;
  }

  public Set<IsScheduleSnapshotOf> getSnapshots() {
    return this.snapshots;
  }

  public void setSnapshots(final Set<IsScheduleSnapshotOf> snapshots) {
    this.snapshots = snapshots;
  }

  @Transient
  public void addStep(final Step step) {
    if (this.steps == null) {
      this.steps = new HashSet<>();
    }
    this.steps.add(step);
    step.setSchedule(this);
  }

  @Transient
  public boolean isStartStep(final Step step) {
    final boolean isSameStartYear = step.getPeriodFromStartDate().getYear() == this.start.getYear();
    final boolean isSameStartMonth = step.getPeriodFromStartDate().getMonthValue() == this.start.getMonthValue();
    return isSameStartYear && isSameStartMonth;
  }

  @Transient
  public boolean isEndStep(final Step step) {
    final boolean isSameEndYear = step.getPeriodFromStartDate().getYear() == this.end.getYear();
    final boolean isSameEndMonth = step.getPeriodFromStartDate().getMonthValue() == this.end.getMonthValue();
    return isSameEndYear && isSameEndMonth;
  }

  public boolean isEdgeStep(final Step step) {
    return this.isStartStep(step) || this.isEndStep(step);
  }

  @Transient
  public Long getIdWorkpack() {
    return this.workpack.getId();
  }

  @Transient
  public Long getIdMaster() {
    if (master == null || master.getMaster() == null) return null;
    return master.getMaster().getId();
  }
}
