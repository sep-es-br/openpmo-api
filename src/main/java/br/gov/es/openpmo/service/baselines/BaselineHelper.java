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
import br.gov.es.openpmo.repository.CostAccountRepository;
import br.gov.es.openpmo.repository.ScheduleRepository;
import br.gov.es.openpmo.repository.StepRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class BaselineHelper {

  private final WorkpackRepository workpackRepository;

  private final CostAccountRepository costAccountRepository;

  private final StepRepository stepRepository;

  private final ScheduleRepository scheduleRepository;

  @Autowired
  public BaselineHelper(
    final WorkpackRepository workpackRepository,
    final CostAccountRepository costAccountRepository,
    final StepRepository stepRepository,
    final ScheduleRepository scheduleRepository
  ) {
    this.workpackRepository = workpackRepository;
    this.costAccountRepository = costAccountRepository;
    this.stepRepository = stepRepository;
    this.scheduleRepository = scheduleRepository;
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

  public <T extends Snapshotable<T>> void createBaselineSnapshotRelationship(
    final Baseline baseline,
    final T snapshot,
    final Neo4jRepository<T, Long> repository
  ) {
    snapshot.setBaseline(baseline);
    repository.save(snapshot);
  }

  public void createIsInRelationship(
    final Workpack child,
    final Workpack parent
  ) {
    child.addParent(parent);
    this.workpackRepository.save(child);
  }

  public void createAppliesToRelationship(
    final Workpack workpack,
    final CostAccount costAccount
  ) {
    if(workpack == null) {
      throw new NegocioException(ApplicationMessage.WORKPACK_IS_NULL);
    }

    workpack.addCost(costAccount);
    this.workpackRepository.save(workpack);
  }

  public void createFeatureRelationship(
    final Workpack workpack,
    final Schedule schedule
  ) {
    schedule.setWorkpack(workpack);
    this.scheduleRepository.save(schedule);
  }

  public void createComposesRelationship(
    final Schedule schedule,
    final Step step
  ) {
    if(schedule == null) {
      throw new NegocioException(ApplicationMessage.SCHEDULE_IS_NULL);
    }

    schedule.addStep(step);
    this.scheduleRepository.save(schedule);
  }

}
