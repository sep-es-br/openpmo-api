package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.BaselineRepository;
import br.gov.es.openpmo.repository.IsPropertySnapshotOfRepository;
import br.gov.es.openpmo.repository.IsWorkpackSnapshotOfRepository;
import br.gov.es.openpmo.repository.PropertyModelRepository;
import br.gov.es.openpmo.repository.PropertyRepository;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.util.DashboardMilestoneUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.text.MessageFormat;
import java.time.LocalDateTime;

@Tag("database")
@DataNeo4jTest
@DisplayName("Test if milestone is being retrieved correctly")
@Testcontainers
class DashboardMilestoneRepositoryTest extends BaseRepositoryTest {

  @Autowired
  DashboardMilestoneRepository underTest;

  @Autowired
  BaselineRepository baselineRepository;

  @Autowired
  WorkpackRepository workpackRepository;

  @Autowired
  WorkpackModelRepository workpackModelRepository;

  @Autowired
  PropertyRepository propertyRepository;

  @Autowired
  PropertyModelRepository propertyModelRepository;

  @Autowired
  IsWorkpackSnapshotOfRepository workpackSnapshotOfRepository;

  @Autowired
  IsPropertySnapshotOfRepository propertySnapshotOfRepository;

  DashboardMilestoneUtil util;

  Workpack workpack;

  Baseline baseline;

  LocalDateTime firstDay;

  LocalDateTime secondDay;

  LocalDateTime thirdDay;

  LocalDateTime dateTime;

  @BeforeEach
  void setUp() {
    this.util = new DashboardMilestoneUtil(
        this.baselineRepository,
        this.workpackRepository,
        this.workpackModelRepository,
        this.propertyRepository,
        this.propertyModelRepository,
        this.workpackSnapshotOfRepository,
        this.propertySnapshotOfRepository
    );

    this.workpack = this.util.createWorkpack();
    this.baseline = this.util.createBaseline();
    this.util.link(this.workpack, this.baseline);

    final int year = 2022;
    final int month = 1;
    final int startDay = 1;

    this.firstDay = LocalDateTime.of(year, month, startDay, 0, 0, 0);
    this.secondDay = this.firstDay.plusDays(1);
    this.thirdDay = this.secondDay.plusDays(1);

    this.dateTime = LocalDateTime.now();
  }

  @Test
  @DisplayName("Should retrive the quantity correcty without baseline")
  void testQuantityWithoutBaseline() {
    // given
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.dateTime, false, "Status Completed");
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.dateTime, true, "Status Completed");

    this.util.createRelationshipsWithoutBaseline(this.workpack, this.dateTime, false, null);
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.dateTime, true, null);

    // when
    final Long actual = this.underTest.quantity(this.workpack.getId());

    // then
    final long expected = 2L;
    assertSame(expected, actual, getErrorMessage(expected, actual));
  }

  private static String getErrorMessage(final long expected, final long actual) {
    return MessageFormat.format("{0} was expected, but {1} was found!", expected, actual);
  }

  @Test
  @DisplayName("Should retrive the quantity correcty with baseline")
  void testQuantityWithBaseline() {
    // given
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.dateTime, this.dateTime, false, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.dateTime, this.dateTime, true, "Status Completed");

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.dateTime, this.dateTime, false, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.dateTime, this.dateTime, true, null);

    // when
    final Long actual = this.underTest.quantity(this.baseline.getId(), this.workpack.getId());

    // then
    final long expected = 2L;
    assertSame(expected, actual, getErrorMessage(expected, actual));
  }

  @Test
  @DisplayName("Should retrive the on time correcty without baseline")
  void testOnTimeWithoutBaseline() {
    // given
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.firstDay, false, "Status Completed");
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.secondDay, false, "Status Completed");
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.thirdDay, false, "Status Completed");

    this.util.createRelationshipsWithoutBaseline(this.workpack, this.firstDay, false, "Status Completed");
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.secondDay, false, "Status Completed");
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.thirdDay, false, "Status Completed");

    this.util.createRelationshipsWithoutBaseline(this.workpack, this.firstDay, true, "Status Completed");
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.secondDay, true, "Status Completed");
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.thirdDay, true, "Status Completed");

    this.util.createRelationshipsWithoutBaseline(this.workpack, this.firstDay, false, null);
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.secondDay, false, null);
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.thirdDay, false, null);

    this.util.createRelationshipsWithoutBaseline(this.workpack, this.firstDay, false, null);
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.secondDay, false, null);
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.thirdDay, false, null);

    this.util.createRelationshipsWithoutBaseline(this.workpack, this.firstDay, true, null);
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.secondDay, true, null);
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.thirdDay, true, null);

    // when
    final Long actual = this.underTest.onTime(this.workpack.getId(), this.secondDay.toLocalDate());

    // then
    final long expected = 4L;
    assertSame(expected, actual, getErrorMessage(expected, actual));
  }

  @Test
  @DisplayName("Should retrive the on time correcty with baseline")
  void testOnTimeWithBaseline() {
    // given
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.firstDay, false, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, false, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.thirdDay, false, "Status Completed");

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.firstDay, false, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, false, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.thirdDay, false, "Status Completed");

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.firstDay, true, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, true, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.thirdDay, true, "Status Completed");

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.firstDay, false, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, false, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.thirdDay, false, null);

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.firstDay, false, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, false, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.thirdDay, false, null);

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.firstDay, true, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, true, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.thirdDay, true, null);

    // when
    final Long actual = this.underTest.onTime(this.baseline.getId(), this.workpack.getId(), this.secondDay.toLocalDate());

    // then
    final long expected = 4L;
    assertSame(expected, actual, getErrorMessage(expected, actual));
  }

  @Test
  @DisplayName("Should retrive the late correcty without baseline")
  void testLateWithoutBaseline() {
    // given
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.firstDay, false, "Status Completed");
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.secondDay, false, "Status Completed");
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.thirdDay, false, "Status Completed");

    this.util.createRelationshipsWithoutBaseline(this.workpack, this.firstDay, false, "Status Completed");
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.secondDay, false, "Status Completed");
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.thirdDay, false, "Status Completed");

    this.util.createRelationshipsWithoutBaseline(this.workpack, this.firstDay, true, "Status Completed");
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.secondDay, true, "Status Completed");
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.thirdDay, true, "Status Completed");

    this.util.createRelationshipsWithoutBaseline(this.workpack, this.firstDay, false, null);
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.secondDay, false, null);
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.thirdDay, false, null);

    this.util.createRelationshipsWithoutBaseline(this.workpack, this.firstDay, false, null);
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.secondDay, false, null);
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.thirdDay, false, null);

    this.util.createRelationshipsWithoutBaseline(this.workpack, this.firstDay, true, null);
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.secondDay, true, null);
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.thirdDay, true, null);

    // when
    final Long actual = this.underTest.late(this.workpack.getId(), this.secondDay.toLocalDate());

    // then
    final long expected = 2L;
    assertSame(expected, actual, getErrorMessage(expected, actual));
  }

  @Test
  @DisplayName("Should retrive the late correcty with baseline")
  void testLateWithBaseline() {
    // given
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.firstDay, false, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, false, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.thirdDay, false, "Status Completed");

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.firstDay, false, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, false, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.thirdDay, false, "Status Completed");

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.firstDay, true, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, true, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.thirdDay, true, "Status Completed");

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.firstDay, false, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, false, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.thirdDay, false, null);

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.firstDay, false, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, false, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.thirdDay, false, null);

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.firstDay, true, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, true, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.thirdDay, true, null);

    // when
    final Long actual = this.underTest.late(this.baseline.getId(), this.workpack.getId(), this.secondDay.toLocalDate());

    // then
    final long expected = 2L;
    assertSame(expected, actual, getErrorMessage(expected, actual));
  }

  @Test
  @DisplayName("Should retrive the concluded correcty without baseline")
  void testConcludedWithoutBaseline() {
    // given
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.dateTime, true, "Status Completed");
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.dateTime, true, "Status Completed");
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.dateTime, false, "Status Completed");

    this.util.createRelationshipsWithoutBaseline(this.workpack, this.dateTime, true, null);
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.dateTime, true, null);
    this.util.createRelationshipsWithoutBaseline(this.workpack, this.dateTime, false, null);

    // when
    final Long actual = this.underTest.concluded(this.workpack.getId());

    // then
    final long expected = 2L;
    assertSame(expected, actual, getErrorMessage(expected, actual));
  }

  @Test
  @DisplayName("Should retrive the concluded correcty with baseline")
  void testConcludedWithBaseline() {
    // given
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.secondDay, true, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, true, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.secondDay, true, "Status Completed");

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.secondDay, true, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, true, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.secondDay, true, "Status Completed");

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.secondDay, false, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, false, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.secondDay, false, "Status Completed");

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.secondDay, true, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, true, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.secondDay, true, null);

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.secondDay, true, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, true, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.secondDay, true, null);

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.secondDay, false, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, false, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.secondDay, false, null);

    // when
    final Long actual = this.underTest.concluded(this.baseline.getId(), this.workpack.getId());

    // then
    final long expected = 4L;
    assertSame(expected, actual, getErrorMessage(expected, actual));
  }

  @Test
  @DisplayName("Should retrive the late concluded correcty with baseline")
  void testLateConcludedWithBaseline() {
    // given
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.secondDay, true, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, true, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.secondDay, true, "Status Completed");

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.secondDay, true, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, true, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.secondDay, true, "Status Completed");

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.secondDay, false, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, false, "Status Completed");
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.secondDay, false, "Status Completed");

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.secondDay, true, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, true, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.secondDay, true, null);

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.secondDay, true, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, true, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.secondDay, true, null);

    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.firstDay, this.secondDay, false, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.secondDay, this.secondDay, false, null);
    this.util.createRelationshipsWithBaseline(this.workpack, this.baseline, this.thirdDay, this.secondDay, false, null);

    // when
    final Long actual = this.underTest.lateConcluded(this.baseline.getId(), this.workpack.getId());

    // then
    final long expected = 2L;
    assertSame(expected, actual, getErrorMessage(expected, actual));
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration getConfiguration() {
      return new Configuration.Builder()
          .uri(container.getBoltUrl())
          .build();
    }

  }

}
