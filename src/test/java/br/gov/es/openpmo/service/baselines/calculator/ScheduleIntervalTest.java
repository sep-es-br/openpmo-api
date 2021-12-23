package br.gov.es.openpmo.service.baselines.calculator;

import br.gov.es.openpmo.dto.baselines.ccbmemberview.ScheduleInterval;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.stream.Stream;

import static java.time.LocalDate.now;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class ScheduleIntervalTest {

  @Test
  void shouldReturnInitialDateCurrentIfChange() {
    final LocalDate currentInitialDate = now().minusMonths(4);
    final LocalDate endDate = now().minusMonths(2);
    final LocalDate proposedInitialDate = now().minusMonths(3);

    final ScheduleInterval currentDate = new ScheduleInterval(
      currentInitialDate,
      endDate
    );

    final ScheduleInterval proposedDate = new ScheduleInterval(
      proposedInitialDate,
      endDate
    );

    final LocalDate changedInitialDate = currentDate.getChangedInitialDate(proposedDate);

    assertEquals(currentInitialDate, changedInitialDate, "Initial date should be changed");
  }

  @Test
  void shouldReturnEndDateCurrentIfChange() {
    final LocalDate currentEndDate = now().minusMonths(1);
    final LocalDate initialDate = now().minusMonths(5);
    final LocalDate proposedEndDate = now().minusMonths(2);

    final ScheduleInterval currentDate = new ScheduleInterval(
      initialDate,
      currentEndDate
    );

    final ScheduleInterval proposedDate = new ScheduleInterval(
      initialDate,
      proposedEndDate
    );

    assertEquals(
      currentEndDate,
      currentDate.getChangedEndDate(proposedDate),
      "End date should be changed"
    );
  }

  @Test
  void shouldReturnNullIfEndDateNotChange() {

    final LocalDate currentEndDate = now().minusMonths(1);
    final LocalDate initialDate = now().minusMonths(5);

    final ScheduleInterval currentDate = new ScheduleInterval(
      initialDate,
      currentEndDate
    );

    final ScheduleInterval proposedDate = new ScheduleInterval(
      initialDate,
      currentEndDate
    );

    assertNull(
      currentDate.getChangedEndDate(proposedDate),
      "End date should be null"
    );
  }


  @Test
  void shouldReturnNullIfInitialDateNotChange() {
    final LocalDate currentInitialDate = now().minusMonths(4);
    final LocalDate endDate = now().minusMonths(2);

    final ScheduleInterval currentDate = new ScheduleInterval(
      currentInitialDate,
      endDate
    );

    final ScheduleInterval proposedDate = new ScheduleInterval(
      currentInitialDate,
      endDate
    );

    assertNull(
      currentDate.getChangedEndDate(proposedDate),
      "End date should be null"
    );
  }

  @ParameterizedTest
  @ArgumentsSource(IntervalArgumentProvider.class)
  void shouldCreateNewIntervalWithChangedDate(
    final LocalDate currentInitialDate,
    final LocalDate proposedInitialDate,
    final LocalDate currentEndDate,
    final LocalDate proposedEndDate,
    final LocalDate expectedInitialDate,
    final LocalDate expectedEndDate
  ) {
    final ScheduleInterval currentDate = new ScheduleInterval(
      currentInitialDate,
      currentEndDate
    );

    final ScheduleInterval proposedDate = new ScheduleInterval(
      proposedInitialDate,
      proposedEndDate
    );

    final ScheduleInterval changedInterval = currentDate.newChangedInterval(proposedDate);

    assertEquals(
      expectedInitialDate,
      changedInterval.getInitialDate(),
      "Should have initial date changed"
    );
    assertEquals(
      expectedEndDate,
      changedInterval.getEndDate(),
      "Should have end date changed"
    );
  }

  static class IntervalArgumentProvider implements ArgumentsProvider {

    @Override public Stream<? extends Arguments> provideArguments(final ExtensionContext context) throws Exception {
      return Stream.of(
        Arguments.of(
          now().minusMonths(4),
          now().minusMonths(3),
          now().minusMonths(1),
          now().minusMonths(2),
          now().minusMonths(4),
          now().minusMonths(1)
        ),
        Arguments.of(
          now().minusMonths(4),
          now().minusMonths(4),
          now().minusMonths(1),
          now().minusMonths(2),
          null,
          now().minusMonths(1)
        ),
        Arguments.of(
          now().minusMonths(4),
          now().minusMonths(3),
          now().minusMonths(1),
          now().minusMonths(1),
          now().minusMonths(4),
          null

        )
      );
    }
  }

}
