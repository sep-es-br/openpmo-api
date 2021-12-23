package br.gov.es.openpmo.service.baselines.calculator;

import br.gov.es.openpmo.dto.baselines.ccbmemberview.ProposedAndCurrentValue;
import br.gov.es.openpmo.dto.workpack.WorkpackName;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.schedule.Schedule;
import br.gov.es.openpmo.model.schedule.Step;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.ConsumesRepository;
import br.gov.es.openpmo.repository.ScheduleRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CostVariationCalculatorTest {

  private static final long ID_PREVIOUS_BASELINE = 4L;
  private static final long ID_WORKPACK = 2L;
  private static final long ID_BASELINE = 1L;
  private static final long ID_STEP = 3L;
 // private CostVariationCalculator costVariationCalculator;

  @Mock
  private BaselineRepository repository;
  @Mock
  private ConsumesRepository consumesRepository;
  @Mock
  private ScheduleRepository scheduleRepository;
  @Mock
  private WorkpackRepository workpackRepository;

  @Mock
  private Workpack workpack;
  @Mock
  private Schedule schedule;
  @Mock
  private Step step;
  @Mock
  private WorkpackName workpackName;
  @Mock
  private Baseline previousBaseline;

  @BeforeEach
  void setUp() {
    //    this.costVariationCalculator = new CostVariationCalculator(
    //      this.repository,
    //      this.consumesRepository,
    //      this.workpackRepository,
    //      this.scheduleRepository
    //    );
  }

  @Test
  void shouldReturnOnlyProposedValueIfIsFirstBaseline() {
    //    this.givenIsFirstBaseline();
    //
    //    final BaselineCostDetail baselineCostDetail = this.costVariationCalculator.calculate(ID_BASELINE);
    //
    //    verify(this.repository, times(1)).findDeliverableWorkpacksOfProjectMaster(ID_WORKPACK);
    //    verify(this.repository, times(1)).findWorkpackByBaselineId(ID_BASELINE);
    //    verify(this.repository, times(1)).findPreviousBaseline(ID_BASELINE, ID_WORKPACK);
    //    verify(this.scheduleRepository, times(1)).findScheduleByWorkpackId(ID_WORKPACK);
    //    verify(this.workpackName, times(1)).getName();
    //    verify(this.workpackRepository, times(1)).findWorkpackNameAndFullname(ID_WORKPACK);
    //    verify(this.consumesRepository, times(1)).findAllSnapshotConsumesOfStepMaster(
    //      ID_BASELINE,
    //      ID_STEP
    //    );
  }

  private void givenIsFirstBaseline() {
    when(this.workpack.getId()).thenReturn(ID_WORKPACK);
    when(this.repository.findWorkpackByBaselineId(ID_BASELINE)).thenReturn(Optional.of(this.workpack));
    when(this.repository.findPreviousBaseline(ID_BASELINE, ID_WORKPACK)).thenReturn(Optional.empty());
    when(this.repository.findDeliverableWorkpacksOfProjectMaster(this.workpack.getId())).thenReturn(
      new HashSet<>(singletonList(this.workpack))
    );
    when(this.scheduleRepository.findScheduleByWorkpackId(ID_WORKPACK)).thenReturn(Optional.of(this.schedule));
    when(this.step.getId()).thenReturn(ID_STEP);
    when(this.schedule.getSteps()).thenReturn(new HashSet<>(singletonList(
      this.step
    )));
    when(this.workpackName.getName()).thenReturn("");
    when(this.workpackRepository.findWorkpackNameAndFullname(ID_WORKPACK)).thenReturn(Optional.of(this.workpackName));
  }


  @Test
  void shouldReturnCostAndDetailsOfBaseline() {
    //    this.givenHasPreviousBaseline();
    //
    //    final BaselineCostDetail baselineCostDetail = this.costVariationCalculator.calculate(ID_BASELINE);
    //
    //    verify(this.repository, times(1)).findDeliverableWorkpacksOfProjectMaster(ID_WORKPACK);
    //    verify(this.repository, times(1)).findWorkpackByBaselineId(ID_BASELINE);
    //    verify(this.repository, times(1)).findPreviousBaseline(ID_BASELINE, ID_WORKPACK);
    //    verify(this.scheduleRepository, times(1)).findScheduleByWorkpackId(ID_WORKPACK);
    //    verify(this.workpackName, times(1)).getName();
    //    verify(this.workpackRepository, times(1)).findWorkpackNameAndFullname(ID_WORKPACK);
    //    verify(this.consumesRepository, times(2)).findAllSnapshotConsumesOfStepMaster(
    //      anyLong(),
    //      eq(ID_STEP)
    //    );
  }

  private void givenHasPreviousBaseline() {
    when(this.workpack.getId()).thenReturn(ID_WORKPACK);
    when(this.repository.findWorkpackByBaselineId(ID_BASELINE)).thenReturn(Optional.of(this.workpack));
    when(this.previousBaseline.getId()).thenReturn(ID_PREVIOUS_BASELINE);
    when(this.repository.findPreviousBaseline(ID_BASELINE, ID_WORKPACK)).thenReturn(Optional.of(this.previousBaseline));
    when(this.repository.findDeliverableWorkpacksOfProjectMaster(this.workpack.getId())).thenReturn(
      new HashSet<>(singletonList(this.workpack))
    );
    when(this.scheduleRepository.findScheduleByWorkpackId(ID_WORKPACK)).thenReturn(Optional.of(this.schedule));
    when(this.step.getId()).thenReturn(ID_STEP);
    when(this.schedule.getSteps()).thenReturn(new HashSet<>(singletonList(
      this.step
    )));
    when(this.workpackName.getName()).thenReturn("");
    when(this.workpackRepository.findWorkpackNameAndFullname(ID_WORKPACK)).thenReturn(Optional.of(this.workpackName));
    when(this.consumesRepository.findAllSnapshotConsumesOfStepMaster(anyLong(), eq(ID_STEP)))
      .thenReturn(Collections.emptyList());
  }


  @Nested class ProposedAndCurrentValueTest {

    private ProposedAndCurrentValue proposedAndCurrentValue;

    @BeforeEach
    void setUp() {
      this.proposedAndCurrentValue = new ProposedAndCurrentValue();
    }

    @Test
    void shouldNotAddNullCurrentValue() {
      this.proposedAndCurrentValue.addCurrentValue(new BigDecimal(10));
      this.proposedAndCurrentValue.addCurrentValue(null);

      final BigDecimal EXPECTED = new BigDecimal(10);

      assertEquals(
        EXPECTED,
        this.proposedAndCurrentValue.getCurrentValue(),
        "Current value should be 10"
      );
    }

    @Test
    void shouldNotAddNullProposedValue() {
      this.proposedAndCurrentValue.addProposedValue(new BigDecimal(10));
      this.proposedAndCurrentValue.addProposedValue(null);

      final BigDecimal EXPECTED = new BigDecimal(10);

      assertEquals(
        EXPECTED,
        this.proposedAndCurrentValue.getProposedValue(),
        "Current value should be 10"
      );
    }

    @Test
    void shouldAddProposedValueAndCurrentValue() {
      this.proposedAndCurrentValue.addProposedValue(new BigDecimal(10));
      this.proposedAndCurrentValue.addCurrentValue(new BigDecimal(15));
      this.proposedAndCurrentValue.addProposedValue(new BigDecimal(5));
      this.proposedAndCurrentValue.addProposedValue(null);
      this.proposedAndCurrentValue.addCurrentValue(null);

      final BigDecimal PROPOSED_EXPECTED = new BigDecimal(15);
      final BigDecimal CURRENT_EXPECTED = new BigDecimal(15);

      assertEquals(
        PROPOSED_EXPECTED,
        this.proposedAndCurrentValue.getProposedValue(),
        "Proposed value should be 15"
      );

      assertEquals(
        CURRENT_EXPECTED,
        this.proposedAndCurrentValue.getCurrentValue(),
        "Current value should be 5"
      );
    }


  }

}
