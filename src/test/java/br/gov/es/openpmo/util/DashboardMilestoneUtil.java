package br.gov.es.openpmo.util;

import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.properties.Date;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.Toggle;
import br.gov.es.openpmo.model.properties.models.DateModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.ToggleModel;
import br.gov.es.openpmo.model.relations.IsPropertySnapshotOf;
import br.gov.es.openpmo.model.relations.IsWorkpackSnapshotOf;
import br.gov.es.openpmo.model.workpacks.Milestone;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.MilestoneModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DashboardMilestoneUtil {

  BaselineRepository baselineRepository;

  WorkpackRepository workpackRepository;

  WorkpackModelRepository workpackModelRepository;

  PropertyRepository propertyRepository;

  PropertyModelRepository propertyModelRepository;

  IsWorkpackSnapshotOfRepository workpackSnapshotOfRepository;

  IsPropertySnapshotOfRepository propertySnapshotOfRepository;

  public DashboardMilestoneUtil(
      final BaselineRepository baselineRepository,
      final WorkpackRepository workpackRepository,
      final WorkpackModelRepository workpackModelRepository,
      final PropertyRepository propertyRepository,
      final PropertyModelRepository propertyModelRepository,
      final IsWorkpackSnapshotOfRepository workpackSnapshotOfRepository,
      final IsPropertySnapshotOfRepository propertySnapshotOfRepository
  ) {
    this.baselineRepository = baselineRepository;
    this.workpackRepository = workpackRepository;
    this.workpackModelRepository = workpackModelRepository;
    this.propertyRepository = propertyRepository;
    this.propertyModelRepository = propertyModelRepository;
    this.workpackSnapshotOfRepository = workpackSnapshotOfRepository;
    this.propertySnapshotOfRepository = propertySnapshotOfRepository;
  }

  public void link(final Workpack workpack, final Baseline baseline) {
    this.baselineRepository.createIsBaselinedByRelationship(baseline.getId(), workpack.getId());
  }

  public Workpack createWorkpack() {
    final Workpack workpack = new Workpack();
    return this.workpackRepository.save(workpack);
  }

  public Baseline createBaseline() {
    final Baseline baseline = new Baseline();
    return this.baselineRepository.save(baseline);
  }

  public void createRelationshipsWithoutBaseline(
      final Workpack workpack,
      final LocalDateTime dateValue,
      final Boolean toggleValue,
      final String name
  ) {
    final Milestone milestone = this.createMilestone();
    final MilestoneModel milestoneModel = this.createMilestoneModel();
    final Date date = this.createDate(dateValue);
    final Toggle toggle = this.createToggle(toggleValue);
    final DateModel dateModel = this.createDateModel();
    final ToggleModel toggleModel = this.createToggleModel(name);

    this.link(milestone, workpack);
    this.link(milestone, milestoneModel);
    this.link(milestone, date);
    this.link(milestone, toggle);
    this.link(milestoneModel, dateModel);
    this.link(milestoneModel, toggleModel);
    this.link(date, dateModel);
    this.link(toggle, toggleModel);
  }

  public Milestone createMilestone() {
    final Milestone milestone = new Milestone();
    return this.workpackRepository.save(milestone);
  }

  public MilestoneModel createMilestoneModel() {
    final MilestoneModel milestoneModel = new MilestoneModel();
    return this.workpackModelRepository.save(milestoneModel);
  }

  public Date createDate(final LocalDateTime value) {
    final Date date = new Date();
    date.setValue(value);
    return this.propertyRepository.save(date);
  }

  public Toggle createToggle(final Boolean value) {
    final Toggle toggle = new Toggle();
    toggle.setValue(value);
    return this.propertyRepository.save(toggle);
  }

  public DateModel createDateModel() {
    final DateModel dateModel = new DateModel();
    return this.propertyModelRepository.save(dateModel);
  }

  public ToggleModel createToggleModel(final String name) {
    final ToggleModel toggleModel = new ToggleModel();
    toggleModel.setName(name);
    return this.propertyModelRepository.save(toggleModel);
  }

  public void link(final Workpack child, final Workpack parent) {
    this.workpackRepository.createIsInRelationship(child.getId(), parent.getId());
  }

  public void link(final Workpack workpack, final WorkpackModel workpackModel) {
    this.workpackRepository.createIsInstanceByRelationship(workpack.getId(), workpackModel.getId());
  }

  public void link(final Workpack workpack, final Property property) {
    this.workpackRepository.createFeaturesRelationship(workpack.getId(), property.getId());
  }

  public void link(final WorkpackModel workpack, final PropertyModel property) {
    this.workpackModelRepository.createFeaturesRelationship(workpack.getId(), property.getId());
  }

  public void link(final Property property, final PropertyModel propertyModel) {
    this.propertyRepository.createIsDrivenByRelationship(property.getId(), propertyModel.getId());
  }

  public void createRelationshipsWithBaseline(
      final Workpack workpack,
      final Baseline baseline,
      final LocalDateTime dateValue,
      final LocalDateTime dateSnapshotValue,
      final boolean toggleValue,
      final String name
  ) {
    final Milestone milestone = this.createMilestone();
    final MilestoneModel milestoneModel = this.createMilestoneModel();
    final Date date = this.createDate(dateValue);
    final Toggle toggle = this.createToggle(toggleValue);
    final DateModel dateModel = this.createDateModel();
    final ToggleModel toggleModel = this.createToggleModel(name);

    this.link(milestone, workpack);
    this.link(milestone, milestoneModel);
    this.link(milestone, date);
    this.link(milestone, toggle);
    this.link(milestoneModel, dateModel);
    this.link(milestoneModel, toggleModel);
    this.link(date, dateModel);
    this.link(toggle, toggleModel);

    final Milestone milestoneSnapshot = this.createMilestoneSnapshot(milestone, baseline);
    final Date dateSnapshot = this.createDateSnapshot(date, baseline, dateSnapshotValue);
    this.link(milestoneSnapshot, dateSnapshot);
  }

  public Milestone createMilestoneSnapshot(final Milestone master, final Baseline baseline) {
    final Milestone snapshot = this.createMilestone();
    this.createIsSnapshotOfRelationship(master, snapshot);
    this.createComposesRelationship(baseline, snapshot);
    return snapshot;
  }

  public Date createDateSnapshot(final Date master, final Baseline baseline, final LocalDateTime value) {
    final Date snapshot = this.createDate(value);
    this.createIsSnapshotOfRelationship(master, snapshot);
    this.createComposesRelationship(baseline, snapshot);
    return snapshot;
  }

  private void createIsSnapshotOfRelationship(final Workpack master, final Workpack snapshot) {
    this.workpackSnapshotOfRepository.save(new IsWorkpackSnapshotOf(master, snapshot));
  }

  private void createComposesRelationship(final Baseline baseline, final Workpack snapshot) {
    this.baselineRepository.createComposesRelationshipWithWorkpack(baseline.getId(), snapshot.getId());
  }

  private void createIsSnapshotOfRelationship(final Property master, final Property snapshot) {
    this.propertySnapshotOfRepository.save(new IsPropertySnapshotOf(master, snapshot));
  }

  private void createComposesRelationship(final Baseline baseline, final Property snapshot) {
    this.baselineRepository.createComposesRelationshipWithProperty(baseline.getId(), snapshot.getId());
  }

}
