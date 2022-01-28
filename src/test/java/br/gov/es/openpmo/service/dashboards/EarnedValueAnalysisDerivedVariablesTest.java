package br.gov.es.openpmo.service.dashboards;

import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueAnalysisDerivedVariables;
import br.gov.es.openpmo.dto.dashboards.earnevalueanalysis.EarnedValueAnalysisVariables;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

@Tag("unit")
@DisplayName("Test calculation variables derived of EVA (Earned Value Analysis)")
@ExtendWith(MockitoExtension.class)
class EarnedValueAnalysisDerivedVariablesTest {

  private static final BigDecimal PLANNED_VALUE = new BigDecimal(380);
  private static final BigDecimal ACTUAL_COST = new BigDecimal(369);
  private static final BigDecimal EARNED_VALUE = new BigDecimal(410);

  @BeforeEach
  void setUp() {
  }

  @Test
  @DisplayName("Should not calculate any derived variable when plannedValue is zero")
  void test1() {
    final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
      EARNED_VALUE,
      ACTUAL_COST,
      BigDecimal.ZERO
    );
    final EarnedValueAnalysisDerivedVariables systemUnderTest = createSystemUnderTest(
      earnedValueAnalysisVariables
    );
    assertAll(
      () -> assertNull(systemUnderTest.getCostVariance(), "Should not calculate CV"),
      () -> assertNull(systemUnderTest.getScheduleVariance(), "Should not calculate SV"),
      () -> assertNull(systemUnderTest.getCostPerformanceIndex(), "Should not calculate CPI"),
      () -> assertNull(systemUnderTest.getSchedulePerformanceIndex(), "Should not calculate SPI"),
      () -> assertNull(systemUnderTest.getEstimateAtComplete(), "Should not calculate ETC"),
      () -> assertNull(systemUnderTest.getEstimateAtCompletion(), "Should not calculate EAC")
    );
  }

  @NotNull
  private static EarnedValueAnalysisDerivedVariables createSystemUnderTest(final EarnedValueAnalysisVariables earnedValueAnalysisVariables) {
    return EarnedValueAnalysisDerivedVariables.create(
      earnedValueAnalysisVariables
    );
  }

  @Nested
  @DisplayName("Test calculation of SV (Schedule Variance)")
  class ScheduleVarianceTest {
    @Test
    @DisplayName("Should calculate SV (Schedule Variance)")
    void test1() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        EARNED_VALUE,
        ACTUAL_COST,
        PLANNED_VALUE
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);
      final BigDecimal EXPECTED = BigDecimal.valueOf(30);
      assertEquals(EXPECTED, derivedVariables.getScheduleVariance(), "Should has same schedule variance");
    }

    @Test
    @DisplayName("Should not calculate SV when Earned Value is null")
    void test2() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        null,
        ACTUAL_COST,
        PLANNED_VALUE
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);
      assertNull(derivedVariables.getScheduleVariance(), "Should not calculate schedule variance");
    }

  }

  @Nested
  @DisplayName("Test calculation of CV (Cost Variance)")
  class CostVarianceTest {
    @Test
    @DisplayName("Should calculate CV when has all variables")
    void test1() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        EARNED_VALUE,
        ACTUAL_COST,
        PLANNED_VALUE
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);
      final BigDecimal EXPECTED = BigDecimal.valueOf(41);
      assertEquals(EXPECTED, derivedVariables.getCostVariance(), "Should has same cost variance");
    }

    @Test
    @DisplayName("Should not calculate CV when Earned Value is null")
    void test2() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        null,
        ACTUAL_COST,
        PLANNED_VALUE
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);
      assertNull(derivedVariables.getCostVariance(), "Should not calculate cost variance");
    }

  }

  @Nested
  @DisplayName("Test calculation of CPI (Cost Performance Index)")
  class CostPerformanceIndexTest {
    @Test
    @DisplayName("Should calculate CPI when has all variables")
    void test1() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        EARNED_VALUE,
        ACTUAL_COST,
        PLANNED_VALUE
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);

      final BigDecimal EXPECTED = BigDecimal.valueOf(1.11);
      assertEquals(EXPECTED, derivedVariables.getCostPerformanceIndex(), "Should has same cost performance index");
    }

    @Test
    @DisplayName("Should not calculate CPI when Actual Cost is zero")
    void test2() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        EARNED_VALUE,
        BigDecimal.ZERO,
        PLANNED_VALUE
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);
      assertNull(derivedVariables.getCostPerformanceIndex(), "Should not calculate CPI");
    }

    @Test
    @DisplayName("Should not calculate CPI when Actual Cost is null")
    void test3() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        EARNED_VALUE,
        null,
        PLANNED_VALUE
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);
      assertNull(derivedVariables.getCostPerformanceIndex(), "Should not calculate CPI");
    }

    @Test
    @DisplayName("Should not calculate CPI when Earned Value is null")
    void test4() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        null,
        ACTUAL_COST,
        PLANNED_VALUE
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);
      assertNull(derivedVariables.getCostPerformanceIndex(), "Should not calculate CPI");
    }


  }

  @Nested
  @DisplayName("Test calculation of SPI (Schedule Performance Index)")
  class SchedulePerformanceIndexTest {

    @Test
    @DisplayName("Should calculate SPI when has all variables")
    void test1() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        EARNED_VALUE,
        ACTUAL_COST,
        PLANNED_VALUE
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);

      final BigDecimal EXPECTED = BigDecimal.valueOf(1.08);
      assertEquals(EXPECTED, derivedVariables.getSchedulePerformanceIndex(), "Should has same schedule performance index");
    }


    @Test
    @DisplayName("Should not calculate SPI when Planned Value is zero")
    void test2() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        EARNED_VALUE,
        ACTUAL_COST,
        BigDecimal.ZERO
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);
      assertNull(derivedVariables.getSchedulePerformanceIndex(), "Should not calculate SPI");
    }

    @Test
    @DisplayName("Should not calculate SPI when Planned Value is null")
    void test3() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        EARNED_VALUE,
        ACTUAL_COST,
        null
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);
      assertNull(derivedVariables.getSchedulePerformanceIndex(), "Should not calculate SPI");
    }

    @Test
    @DisplayName("Should not calculate SPI when Earned Value is null")
    void test4() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        null,
        ACTUAL_COST,
        PLANNED_VALUE
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);
      assertNull(derivedVariables.getSchedulePerformanceIndex(), "Should not calculate SPI");
    }


  }

  @Nested
  @DisplayName("Test calculation of EAC (Estimate At Completion)")
  class EstimateAtCompletionTest {
    @Test
    @DisplayName("Should calculate EAC when has all variables")
    void test1() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        EARNED_VALUE,
        ACTUAL_COST,
        PLANNED_VALUE
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);
      final BigDecimal EXPECTED = BigDecimal.valueOf(342.34);
      assertEquals(EXPECTED, derivedVariables.getEstimateAtCompletion(), "Should has same estimate at completion value");
    }

    @Test
    @DisplayName("Should not calculate EAC when Actual Cost is zero")
    void test2() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        EARNED_VALUE,
        BigDecimal.ZERO,
        PLANNED_VALUE
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);
      assertNull(derivedVariables.getEstimateAtCompletion(), "Should not calculate EAC");
    }

    @Test
    @DisplayName("Should not calculate EAC when Actual Cost is null")
    void test3() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        EARNED_VALUE,
        null,
        PLANNED_VALUE
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);
      assertNull(derivedVariables.getEstimateAtCompletion(), "Should not calculate EAC");
    }

    @Test
    @DisplayName("Should not calculate EAC when Earned Value is null")
    void test4() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        null,
        ACTUAL_COST,
        PLANNED_VALUE
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);
      assertNull(derivedVariables.getEstimateAtCompletion(), "Should not calculate EAC");
    }
  }

  @Nested
  @DisplayName("Test calculation of ETC (Estimate At Complete)")
  class EstimateAtCompleteTest {
    @Test
    @DisplayName("Should calculate ETC when has all variables")
    void test1() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        EARNED_VALUE,
        ACTUAL_COST,
        PLANNED_VALUE
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);

      final BigDecimal EXPECTED = BigDecimal.valueOf(-26.66);
      assertEquals(EXPECTED, derivedVariables.getEstimateAtComplete(), "Should has same estimate at complete value");
    }

    @Test
    @DisplayName("Should not calculate ETC when Actual Cost is null")
    void test2() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        EARNED_VALUE,
        null,
        PLANNED_VALUE
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);
      assertNull(derivedVariables.getEstimateAtComplete(), "Should not calculate ETC");
    }

    @Test
    @DisplayName("Should not calculate ETC when Earned Value is null")
    void test3() {
      final EarnedValueAnalysisVariables earnedValueAnalysisVariables = new EarnedValueAnalysisVariables(
        null,
        ACTUAL_COST,
        PLANNED_VALUE
      );
      final EarnedValueAnalysisDerivedVariables derivedVariables = createSystemUnderTest(earnedValueAnalysisVariables);
      assertNull(derivedVariables.getEstimateAtComplete(), "Should not calculate ETC");
    }
  }


}
