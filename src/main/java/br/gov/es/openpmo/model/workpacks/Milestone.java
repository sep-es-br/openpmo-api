package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
//import br.gov.es.openpmo.model.properties.Date;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.relations.IsPropertySnapshotOf;
import br.gov.es.openpmo.model.relations.IsWorkpackSnapshotOf;
import br.gov.es.openpmo.model.workpacks.models.MilestoneModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static br.gov.es.openpmo.model.baselines.Status.PROPOSED;

@NodeEntity
public class Milestone extends Workpack {

  @Relationship("IS_INSTANCE_BY")
  private MilestoneModel instance;

  private LocalDateTime date;

  @Transient
  private LocalDate milestoneDate;

  @Transient
  private LocalDate snapshotDate;

  public MilestoneModel getInstance() {
    return this.instance;
  }

  public void setInstance(final MilestoneModel instance) {
    this.instance = instance;
  }

  @Override
  public Workpack snapshot() {
    return new Milestone();
  }

  @Override
  public String getType() {
    return "Milestone";
  }

  @Transient
  public LocalDateTime getDate() {
    return this.date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  @Transient
  public LocalDate getMilestoneDate() {
    if (this.milestoneDate != null) {
      return this.milestoneDate;
    }
    this.milestoneDate = this.getDate().toLocalDate();
    return this.milestoneDate;
  }

  @Transient
  public LocalDate getSnapshotDate() {
    if (this.snapshotDate != null) {
      return this.snapshotDate;
    }
    final Set<IsWorkpackSnapshotOf> snapshots = this.getSnapshots();
    if (snapshots == null) {
      return null;
    }
    for (IsWorkpackSnapshotOf snapshot : snapshots) {
      this.snapshotDate = snapshot.getDate().toLocalDate();
      return this.snapshotDate;
    }
    return null;
  }

  @Transient
  public LocalDate getSnapshotDateActiveOrProposedBaseline() {
    if (this.snapshotDate != null) {
      return this.snapshotDate;
    }
    final Set<IsWorkpackSnapshotOf> snapshots = this.getSnapshots();
    if (snapshots == null) {
      return null;
    }
    LocalDate snapshotDateActive = null;
    LocalDate snapshotDateProposed = null;
    LocalDateTime baselineProposalDate = null;
    for (IsWorkpackSnapshotOf snapshot : snapshots) {
//      Property property = snapshot.getSnapshot();
      Baseline baseline = snapshot.getSnapshot().getBaseline();
      if (baseline.isActive()) {
        snapshotDateActive = snapshot.getDate().toLocalDate();
        break;
      }
      if (baseline.getStatus() == PROPOSED) {
        baselineProposalDate = baseline.getProposalDate();
        snapshotDateProposed = snapshot.getDate().toLocalDate();
      }
    }
    if (snapshotDateActive != null) {
      this.snapshotDate = snapshotDateActive;
    } else {
      this.snapshotDate = snapshotDateProposed;
    }
    return this.snapshotDate;
  }

  @Transient
  public boolean isOnTime(LocalDate refDate) {
    final Boolean completed = this.getCompleted();
    if (completed == null || Boolean.FALSE.equals(completed)) {
      final LocalDate milestoneDate = this.getMilestoneDate();
      if (!refDate.isAfter(milestoneDate)) {
        return true;
      }
      final LocalDate snapshotDate = this.getSnapshotDate();
      if (snapshotDate == null) {
        return false;
      }
      return !refDate.isAfter(snapshotDate);
    }
    return false;
  }

  @Transient
  public boolean isLate(LocalDate refDate) {
    final Boolean completed = this.getCompleted();
    if (completed == null || Boolean.FALSE.equals(completed)) {
      final LocalDate milestoneDate = this.getMilestoneDate();
      if (refDate.isAfter(milestoneDate)) {
        return true;
      }
      final LocalDate snapshotDate = this.getSnapshotDate();
      if (snapshotDate == null) {
        return false;
      }
      return !milestoneDate.isAfter(refDate) && refDate.isAfter(snapshotDate)
        || milestoneDate.isAfter(refDate) && milestoneDate.isAfter(snapshotDate);
    }
    return false;
  }

  @Transient
  public boolean isConcluded() {
    if (Boolean.TRUE.equals(this.getCompleted())) {
      final LocalDate snapshotDate = this.getSnapshotDate();
      if (snapshotDate == null) {
        return true;
      }
      return !this.getMilestoneDate().isAfter(snapshotDate);
    }
    return false;
  }

  @Transient
  public boolean isLateConcluded() {
    final Boolean completed = this.getCompleted();
    if (Boolean.TRUE.equals(completed)) {
      final LocalDate snapshotDate = this.getSnapshotDate();
      if (snapshotDate == null) {
        return false;
      }
      return this.getMilestoneDate().isAfter(snapshotDate);
    }
    return false;
  }
}
