package br.gov.es.openpmo.service.baselines.calculator;

import br.gov.es.openpmo.dto.baselines.ccbmemberview.BaselineCostDetail;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.CostDetailItem;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.ScheduleDetailItem;
import br.gov.es.openpmo.dto.baselines.ccbmemberview.ScheduleInterval;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class BaselineVariationCalculusTest {


  @NotNull private static CostDetailItem createCostItem(final BigDecimal currentValue, final BigDecimal proposedValue) {
    return new CostDetailItem(
      null,
      null,
      currentValue,
      proposedValue
    );
  }

  @NotNull private static ScheduleDetailItem createScheduleItem(
    final ScheduleInterval proposedIntervalDate,
    final ScheduleInterval currentIntervalDate
  ) {
    return new ScheduleDetailItem(
      null,
      null,
      proposedIntervalDate,
      currentIntervalDate
    );
  }

  @Nested class CostItem {
    @Test
    void shouldHave20PercentVariation() {
      final CostDetailItem item = createCostItem(new BigDecimal(100_000), new BigDecimal(80_000));

      final double EXPECTED = 20.0;

      final BigDecimal variation = item.getVariation();
      assertEquals(EXPECTED, variation.doubleValue(), "Should have 20% variation");
    }

    @Test
    void shouldHaveMinus25PercentVariation() {
      final CostDetailItem item = createCostItem(new BigDecimal(80_000), new BigDecimal(100_000));

      final double EXPECTED = -25.0;

      final double variation = item.getVariation().doubleValue();
      assertEquals(EXPECTED, variation, "Should have -25% variation");
    }

    @Test
    void shouldNotCalculateVariationIfNotHasProposedValue() {
      final CostDetailItem item = createCostItem(new BigDecimal(100_000), null);

      final BigDecimal variation = item.getVariation();

      assertNull(variation, "Should not calculate variation");
    }

    @Test
    void shouldNotCalculateVariationIfNotHasCurrentValue() {
      final CostDetailItem item = createCostItem(null, new BigDecimal(100_000));

      final BigDecimal variation = item.getVariation();
      assertNull(variation, "Should not calculate variation");
    }
  }

  @Nested class CostDetail {


    private BaselineCostDetail costDetail;

    @BeforeEach
    void setUp() {

    }

    @Test
    void shouldSumAllProposedValue() {
      this.costDetail = new BaselineCostDetail();
      this.costDetail.addDetail(asList(
        createCostItem(new BigDecimal(100_000), new BigDecimal(110_000)),
        createCostItem(new BigDecimal(100_000), new BigDecimal(120_000)),
        createCostItem(new BigDecimal(120_000), new BigDecimal(100_000)),
        createCostItem(new BigDecimal(100_000), new BigDecimal(170_000))
      ));
      final BigDecimal EXPECTED = new BigDecimal(500_000);

      assertEquals(
        EXPECTED,
        this.costDetail.getProposedValue(),
        "Should have 500.000,00 of proposed value"
      );
    }

    @Test
    void shouldSumAllCurrentValue() {
      this.costDetail = new BaselineCostDetail();
      this.costDetail.addDetail(asList(
        createCostItem(new BigDecimal(100_000), new BigDecimal(110_000)),
        createCostItem(new BigDecimal(100_000), new BigDecimal(120_000)),
        createCostItem(new BigDecimal(120_000), new BigDecimal(100_000)),
        createCostItem(new BigDecimal(100_000), new BigDecimal(170_000))
      ));

      final BigDecimal EXPECTED = new BigDecimal(420_000);
      assertEquals(
        EXPECTED,
        this.costDetail.getCurrentValue(),
        "Should have 420.000,00 of proposed value"
      );
    }

    @Test
    void shouldHaveMinus19_05Variation() {
      //      this.costDetail = new BaselineCostDetail();
      //      this.costDetail.addDetail(asList(
      //        createCostItem(new BigDecimal(100_000), new BigDecimal(110_000)),
      //        createCostItem(new BigDecimal(100_000), new BigDecimal(120_000)),
      //        createCostItem(new BigDecimal(120_000), new BigDecimal(100_000)),
      //        createCostItem(new BigDecimal(100_000), new BigDecimal(170_000))
      //      ));
      //
      //      final double EXPECTED = -19.05;
      //
      //      assertEquals(
      //        EXPECTED,
      //        this.costDetail.getVariation(),
      //        "Should have -19.04 of variation"
      //      );
    }

    @Test
    void shouldIgnoreNullValuesOfCurrentValue() {
      this.costDetail = new BaselineCostDetail();
      this.costDetail.addDetail(asList(
        createCostItem(new BigDecimal(100_000), new BigDecimal(110_000)),
        createCostItem(new BigDecimal(100_000), new BigDecimal(120_000)),
        createCostItem(null, new BigDecimal(100_000)),
        createCostItem(new BigDecimal(100_000), new BigDecimal(170_000))
      ));

      final BigDecimal EXPECTED = new BigDecimal(300_000);

      assertEquals(
        EXPECTED,
        this.costDetail.getCurrentValue(),
        "Should have 300.000,00 of proposed value"
      );
    }

    @Test
    void shouldIgnoreNullValuesOfProposedValue() {
      this.costDetail = new BaselineCostDetail();
      this.costDetail.addDetail(asList(
        createCostItem(new BigDecimal(100_000), new BigDecimal(110_000)),
        createCostItem(new BigDecimal(100_000), new BigDecimal(120_000)),
        createCostItem(new BigDecimal(120_000), null),
        createCostItem(new BigDecimal(100_000), new BigDecimal(170_000))
      ));

      final BigDecimal EXPECTED = new BigDecimal(400_000);

      assertEquals(
        EXPECTED,
        this.costDetail.getProposedValue(),
        "Should have 400.000,00 of proposed value"
      );
    }

    @Test
    void shouldNotCalculateProposedValueWhenItensIsEmpty() {
      this.costDetail = new BaselineCostDetail();
      this.costDetail.addDetail(Collections.emptyList());
      assertThat(this.costDetail)
        .hasAllNullFieldsOrPropertiesExcept("costDetails");
    }

    @Test
    void shouldNotCalculateVariationWhenItensIsNull() {
      this.costDetail = new BaselineCostDetail();
      assertNull(this.costDetail.getVariation(), "Should not calculate variation");
    }

  }

  @Nested
  class ScheduleItem {

    @Test
    void shouldNotCalculateVariationWhenProposedIntervalIsNull() {
      final ScheduleDetailItem scheduleItem = createScheduleItem(
        null,
        new ScheduleInterval(
          LocalDate.now().plusDays(5),
          LocalDate.now().plusDays(25)
        )
      );

      assertNull(scheduleItem.getVariation(), "Variation should be null");
    }

    @Test
    void shouldNotSetProposedDateWhenProposedIntervalIsNull() {
      final ScheduleDetailItem scheduleItem = createScheduleItem(
        null,
        new ScheduleInterval(
          LocalDate.now().plusDays(5),
          LocalDate.now().plusDays(25)
        )
      );

      assertNull(scheduleItem.getProposedDate(), "Proposed date should be null");
    }

    @Test
    void shouldNotCalculateVariationWhenCurrentIntervalIsNull() {
      final ScheduleDetailItem scheduleDetailItem = createScheduleItem(
        new ScheduleInterval(
          LocalDate.now().minusDays(15),
          LocalDate.now().minusDays(5)
        ),
        null
      );

      assertNull(scheduleDetailItem.getVariation(), "Variation should be null");
    }


    @Test
    void shouldNotSetProposedDateWhenCurrentIntervalIsNull() {
      final ScheduleDetailItem scheduleDetailItem = createScheduleItem(
        new ScheduleInterval(
          LocalDate.now().minusDays(15),
          LocalDate.now().minusDays(5)
        ),
        null
      );

      assertNull(scheduleDetailItem.getCurrentDate(), "Current date should be null");
    }

    @Test
    void shouldHave50PercentVariation() {
      final ScheduleDetailItem scheduleDetailItem = createScheduleItem(
        new ScheduleInterval(
          LocalDate.now().minusDays(15),
          LocalDate.now().minusDays(5)
        ), new ScheduleInterval(
          LocalDate.now().minusDays(20),
          LocalDate.now().minusDays(10)
        )
      );

      final BigDecimal variation = scheduleDetailItem.getVariation();

      final Double EXPECTED = 50.0;

      assertEquals(EXPECTED, variation.doubleValue(), "Variation should be 50%");
    }

  }

  @Nested class ScheduleDetail {

  }

  @Nested
  class Scope {

  }
}
