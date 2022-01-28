package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.tripleconstraint.CostAndScopeData;
import br.gov.es.openpmo.model.relations.Consumes;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.repository.ConsumesRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Tag("unit")
@DisplayName("Test if calculate total cost of steps")
@ExtendWith(MockitoExtension.class)
class GetCostAndScopeTest {

  private static final Long ID_STEP_MASTER = 1L;
  private static final Long ID_BASELINE = 1L;
  @Mock
  private ConsumesRepository consumesRepository;
  private GetCostAndScope getCostAndScope;
  private List<Step> steps;

  @BeforeEach
  void setUp() {
    this.steps = asList(
      createStep(
        LocalDate.now().plusMonths(1),
        asList(
          createConsumes(100, 200, 1L),
          createConsumes(50, 100, 2L)
        ),
        5,
        15
      ),
      createStep(
        LocalDate.now().plusMonths(2),
        asList(
          createConsumes(100, 200, 3L),
          createConsumes(50, 100, 4L)
        ),
        15,
        10
      ),
      createStep(
        LocalDate.now().plusMonths(3),
        asList(
          createConsumes(100, 200, 5L),
          createConsumes(0, 100, 6L)
        ),
        5,
        10
      )
    );

    this.getCostAndScope = new GetCostAndScope(this.consumesRepository);
  }

  @NotNull private static Consumes createConsumes(final int actualCost, final int plannedCost, final long id) {
    final Consumes consumes = new Consumes();
    consumes.setId(id);
    consumes.setStep(createStep(
      null,
      emptyList(),
      5,
      10
    ));
    consumes.setActualCost(new BigDecimal(actualCost));
    consumes.setPlannedCost(new BigDecimal(plannedCost));
    return consumes;
  }

  @NotNull private static Step createStep(
    final LocalDate periodFromStart,
    final List<? extends Consumes> consumes,
    final int actualWork,
    final int plannedWork
  ) {
    final Step step = new Step();
    step.setId(ID_STEP_MASTER);
    step.setPlannedWork(new BigDecimal(plannedWork));
    step.setActualWork(new BigDecimal(actualWork));
    step.setPeriodFromStart(periodFromStart);
    step.setConsumes(new HashSet<>(consumes));
    return step;
  }

  @Test
  @DisplayName("Should calculate total 'Planned Value' Cost correctly")
  void test1() {

    final List<Consumes> snapshotConsumes = asList(
      createConsumes(100, 200, 1L),
      createConsumes(0, 100, 2L)
    );
    doReturn(snapshotConsumes)
      .when(this.consumesRepository)
      .findAllSnapshotConsumesOfStepMaster(ID_BASELINE, ID_STEP_MASTER);

    final CostAndScopeData costAndScopeData = this.getCostAndScope.get(
      ID_BASELINE,
      YearMonth.now().plusMonths(1),
      this.steps
    );

    assertEquals(
      new BigDecimal(900),
      costAndScopeData.getCostDataChart().getPlannedValue(),
      "Should have same Planned Value"
    );
    verify(this.consumesRepository, times(3))
      .findAllSnapshotConsumesOfStepMaster(ID_BASELINE, ID_STEP_MASTER);
  }

  @Test
  @DisplayName("Should calculate total 'Foreseen Value' Cost correctly")
  void test2() {

    final List<Consumes> snapshotConsumes = asList(
      createConsumes(100, 200, 1L),
      createConsumes(0, 100, 2L)
    );
    doReturn(snapshotConsumes)
      .when(this.consumesRepository)
      .findAllSnapshotConsumesOfStepMaster(ID_BASELINE, ID_STEP_MASTER);

    final CostAndScopeData costAndScopeData = this.getCostAndScope.get(
      ID_BASELINE,
      YearMonth.now().plusMonths(1),
      this.steps
    );

    assertEquals(
      new BigDecimal(900),
      costAndScopeData.getCostDataChart().getForeseenValue(),
      "Should have same Foreseen Value"
    );
    verify(this.consumesRepository, times(3))
      .findAllSnapshotConsumesOfStepMaster(ID_BASELINE, ID_STEP_MASTER);
  }

  @Test
  @DisplayName("Should calculate total 'Actual Value' Cost correctly at next month")
  void test3() {
    final List<Consumes> snapshotConsumes = asList(
      createConsumes(100, 200, 1L),
      createConsumes(0, 100, 2L)
    );

    doReturn(snapshotConsumes)
      .when(this.consumesRepository)
      .findAllSnapshotConsumesOfStepMaster(ID_BASELINE, ID_STEP_MASTER);

    final CostAndScopeData costAndScopeData = this.getCostAndScope.get(
      ID_BASELINE,
      YearMonth.now().plusMonths(1),
      this.steps
    );

    assertEquals(
      new BigDecimal(150),
      costAndScopeData.getCostDataChart().getActualValue(),
      "Should have same Actual Value"
    );
    verify(this.consumesRepository, times(3))
      .findAllSnapshotConsumesOfStepMaster(ID_BASELINE, ID_STEP_MASTER);
  }

  @Test
  @DisplayName("Should calculate total 'Actual Value' Cost correctly at 4 next months")
  void test4() {
    final List<Consumes> snapshotConsumes = asList(
      createConsumes(100, 200, 1L),
      createConsumes(0, 100, 2L)
    );

    doReturn(snapshotConsumes)
      .when(this.consumesRepository)
      .findAllSnapshotConsumesOfStepMaster(ID_BASELINE, ID_STEP_MASTER);

    final CostAndScopeData costAndScopeData = this.getCostAndScope.get(
      ID_BASELINE,
      YearMonth.now().plusMonths(4),
      this.steps
    );

    assertEquals(
      new BigDecimal(400),
      costAndScopeData.getCostDataChart().getActualValue(),
      "Should have same Actual Value"
    );
    verify(this.consumesRepository, times(3))
      .findAllSnapshotConsumesOfStepMaster(ID_BASELINE, ID_STEP_MASTER);
  }

  @Test
  @DisplayName("Should not calculate total 'Actual Value' Cost when year/month is before of start date")
  void test5() {
    final List<Consumes> snapshotConsumes = asList(
      createConsumes(100, 200, 1L),
      createConsumes(0, 100, 2L)
    );

    doReturn(snapshotConsumes)
      .when(this.consumesRepository)
      .findAllSnapshotConsumesOfStepMaster(ID_BASELINE, ID_STEP_MASTER);

    final CostAndScopeData costAndScopeData = this.getCostAndScope.get(
      ID_BASELINE,
      YearMonth.now().minusMonths(4),
      this.steps
    );

    assertNull(costAndScopeData.getCostDataChart().getActualValue(), "Should not has 'Actual Value'");

    verify(this.consumesRepository, times(3))
      .findAllSnapshotConsumesOfStepMaster(ID_BASELINE, ID_STEP_MASTER);
  }
}
