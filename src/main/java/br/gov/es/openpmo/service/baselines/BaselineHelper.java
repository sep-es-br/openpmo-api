package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.baselines.Snapshotable;
import br.gov.es.openpmo.model.relations.IsSnapshotOf;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.baselines.BaselineHelperScheduleRepository;
import br.gov.es.openpmo.repository.baselines.BaselineHelperWorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.BiFunction;

@Component
public class BaselineHelper {

  private final BaselineHelperWorkpackRepository baselineHelperWorkpackRepository;

  private final BaselineHelperScheduleRepository baselineHelperScheduleRepository;

  @Autowired
  public BaselineHelper(
    final BaselineHelperWorkpackRepository baselineHelperWorkpackRepository,
    final BaselineHelperScheduleRepository baselineHelperScheduleRepository
  ) {
    this.baselineHelperWorkpackRepository = baselineHelperWorkpackRepository;
    this.baselineHelperScheduleRepository = baselineHelperScheduleRepository;
  }

  private static <T> void ifIsNullThrowsException(
    final T obj,
    final String message
  ) {
    if (Objects.isNull(obj)) {
      throw new NegocioException(message);
    }
  }

  public <T extends Snapshotable<T>, R extends IsSnapshotOf<T>> void createMasterSnapshotRelationship(
    final T master,
    final T snapshot,
    final Neo4jRepository<? super R, Long> repository,
    final BiFunction<T, T, R> constructor
  ) {
    master.setCategory(CategoryEnum.MASTER);
    snapshot.setCategory(CategoryEnum.SNAPSHOT);

    final R apply = constructor.apply(master, snapshot);
    repository.save(apply, 0);
  }

  public <T extends Snapshotable<T>> T createSnapshot(
    final T snapshotable,
    final Neo4jRepository<? super T, Long> repository
  ) {
    final T snapshot = snapshotable.snapshot();
    return repository.save(snapshot);
  }

  public <T extends Snapshotable<T>> T createSnapshotWithBaselineRelationship(
    final T snapshotable,
    final Neo4jRepository<? super T, Long> repository,
    final Baseline baseline
  ) {
    final T snapshot = snapshotable.snapshot();
    snapshot.setBaseline(baseline);
    return repository.save(snapshot);
  }

  public <T extends Snapshotable<T>> void createBaselineSnapshotRelationship(
    final Baseline baseline,
    final T snapshot,
    final Neo4jRepository<T, Long> repository
  ) {
    snapshot.setBaseline(baseline);
    repository.save(snapshot);
  }

  public <T extends Snapshotable<T>> void createBaselineSnapshotRelationship(
    final Baseline baseline,
    final T snapshot
    ) {
    snapshot.setBaseline(baseline);
    this.baselineHelperWorkpackRepository.createBaselineComposesRelationship(baseline.getId(), snapshot.getId());
  }

  public void createIsInRelationship(
    final Workpack child,
    final Workpack parent
  ) {
    ifIsNullThrowsException(child, ApplicationMessage.WORKPACK_IS_NULL);
    ifIsNullThrowsException(parent, ApplicationMessage.COST_ACCOUNT_NOT_FOUND);
    final Long childId = child.getId();
    final Long parentId = parent.getId();
    this.baselineHelperWorkpackRepository.createIsInRelationship(childId, parentId);
  }

  public void createAppliesToRelationship(
    final Workpack workpack,
    final CostAccount costAccount
  ) {
    ifIsNullThrowsException(workpack, ApplicationMessage.WORKPACK_IS_NULL);
    ifIsNullThrowsException(costAccount, ApplicationMessage.COST_ACCOUNT_NOT_FOUND);
    final Long workpackId = workpack.getId();
    final Long costAccountId = costAccount.getId();
    this.baselineHelperWorkpackRepository.createAppliesToRelationship(workpackId, costAccountId);
  }

  public void createFeatureRelationship(
    final Workpack workpack,
    final Schedule schedule
  ) {
    ifIsNullThrowsException(workpack, ApplicationMessage.WORKPACK_IS_NULL);
    ifIsNullThrowsException(schedule, ApplicationMessage.SCHEDULE_IS_NULL);
    final Long workpackId = workpack.getId();
    final Long scheduleId = schedule.getId();
    this.baselineHelperWorkpackRepository.createFeaturesRelationship(workpackId, scheduleId);
  }

  public void createComposesRelationship(
    final Schedule schedule,
    final Step step
  ) {
    ifIsNullThrowsException(schedule, ApplicationMessage.SCHEDULE_IS_NULL);
    ifIsNullThrowsException(step, ApplicationMessage.STEP_NOT_FOUND);
    final Long scheduleId = schedule.getId();
    final Long stepId = step.getId();
    this.baselineHelperScheduleRepository.createComposesRelationship(scheduleId, stepId);
  }

  public void createCostAccountConsumesRelationship(
    final Long stepId,
    final Long costAccountId,
    final Long stepSnapshotId,
    final Long costAccountSnapshotId
  ) {
    this.baselineHelperWorkpackRepository.createCostAccountConsumesRelationship(
      stepId,
      costAccountId,
      stepSnapshotId,
      costAccountSnapshotId
    );
  }

}
