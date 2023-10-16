package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.properties.Date;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.relations.IsPropertySnapshotOf;
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

  @Transient
  private Date date;

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
  public Date getDate() {
    if (this.date != null) {
      return this.date;
    }
    final Set<Property> properties = this.getProperties();
    if (properties != null) {
      for (Property property : properties) {
        if (property instanceof Date) {
          this.date = (Date) property;
          return this.date;
        }
      }
    }
    throw new NegocioException("Milestone deve conter uma data!");
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
    final Set<IsPropertySnapshotOf> snapshots = this.getDate().getSnapshots();
    if (snapshots == null) {
      return null;
    }
    for (IsPropertySnapshotOf snapshot : snapshots) {
      Property property = snapshot.getSnapshot();
      if (property instanceof Date) {
        this.snapshotDate = ((Date) property).toLocalDate();
        return this.snapshotDate;
      }
    }
    return null;
  }

  @Transient
  public LocalDate getSnapshotDateActiveOrProposedBaseline() {
    if (this.snapshotDate != null) {
      return this.snapshotDate;
    }
    final Set<IsPropertySnapshotOf> snapshots = this.getDate().getSnapshots();
    if (snapshots == null) {
      return null;
    }
    LocalDate snapshotDateActive = null;
    LocalDate snapshotDateProposed = null;
    LocalDateTime baselineProposalDate = null;
    for (IsPropertySnapshotOf snapshot : snapshots) {
      Property property = snapshot.getSnapshot();
      Baseline baseline = property.getBaseline();
      if (baseline.isActive()) {
        snapshotDateActive = ((Date) property).toLocalDate();
        break;
      }
      if (baseline.getStatus() == PROPOSED) {
        if (baselineProposalDate == null) {
          baselineProposalDate = baseline.getProposalDate();
          snapshotDateProposed = ((Date) property).toLocalDate();
        } else if (baseline.getProposalDate().isAfter(baselineProposalDate)) {
          baselineProposalDate = baseline.getProposalDate();
          snapshotDateProposed = ((Date) property).toLocalDate();
        }
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
