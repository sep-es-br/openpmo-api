package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.DashboardBaselineResponse;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.baselines.Status;
import br.gov.es.openpmo.model.relations.IsBaselinedBy;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.IsBaselinedByRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.assertj.core.api.Condition;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("database")
@DataNeo4jTest
@DisplayName("Test if baseline is filtering correctly for dashboard")
@Testcontainers
class DashboardBaselineRepositoryTest extends BaseRepositoryTest {

  @Autowired
  DashboardBaselineRepository underTest;

  @Autowired
  WorkpackRepository workpackRepository;

  @Autowired
  IsBaselinedByRepository isBaselinedByRepository;

  Workpack workpack;

  @BeforeEach
  void setUp() {
    this.workpack = new Workpack();
    this.workpackRepository.save(this.workpack);
  }

  @Test
  @DisplayName("Should find all approved or proposed baselines")
  void test1() {
    // given
    final Baseline draft = this.createBaseline("DRAFT", Status.DRAFT, LocalDateTime.now(), false);
    final Baseline proposed = this.createBaseline("PROPOSED", Status.PROPOSED, LocalDateTime.now(), false);
    final Baseline approved = this.createBaseline("APPROVED", Status.APPROVED, LocalDateTime.now(), false);
    final Baseline rejected = this.createBaseline("REJECTED", Status.REJECTED, LocalDateTime.now(), false);

    this.linkBaselineWithWorkpack(draft, this.workpack);
    this.linkBaselineWithWorkpack(proposed, this.workpack);
    this.linkBaselineWithWorkpack(approved, this.workpack);
    this.linkBaselineWithWorkpack(rejected, this.workpack);

    // when
    final List<DashboardBaselineResponse> baselineResponses =
        this.underTest.findAllByWorkpackId(this.workpack.getId());

    // then
    assertThat(baselineResponses)
        .hasSize(2)
        .allMatch(DashboardBaselineRepositoryTest::isApprovedOrProposed);
  }

  Baseline createBaseline(
      final String name,
      final Status status,
      final LocalDateTime proposalDate,
      final boolean active
  ) {
    final Baseline baseline = new Baseline();
    baseline.setName(name);
    baseline.setStatus(status);
    baseline.setProposalDate(proposalDate);
    baseline.setActive(active);
    return this.underTest.save(baseline);
  }

  void linkBaselineWithWorkpack(final Baseline baseline, final Workpack workpack) {
    final IsBaselinedBy isBaselinedBy = new IsBaselinedBy();
    isBaselinedBy.setBaseline(baseline);
    isBaselinedBy.setWorkpack(workpack);
    this.isBaselinedByRepository.save(isBaselinedBy);
  }

  static boolean isApprovedOrProposed(final DashboardBaselineResponse baselineResponse) {
    return baselineResponse.getStatus() == Status.APPROVED
        || baselineResponse.getStatus() == Status.PROPOSED;
  }

  @Test
  @DisplayName("Should not find any baseline if workpack does not have any")
  void test2() {
    // given
    // workpack does not have baslines

    //when
    final List<DashboardBaselineResponse> baselineResponses =
        this.underTest.findAllByWorkpackId(this.workpack.getId());

    // then
    assertThat(baselineResponses)
        .hasSize(0)
        .noneMatch(DashboardBaselineRepositoryTest::isApprovedOrProposed);
  }

  @Test
  @DisplayName("Should not find any baselines but approved or rejected")
  void test3() {
    // given
    final Baseline draft = this.createBaseline("DRAFT", Status.DRAFT, LocalDateTime.now(), false);
    final Baseline rejected = this.createBaseline("REJECTED", Status.REJECTED, LocalDateTime.now(), false);

    this.linkBaselineWithWorkpack(draft, this.workpack);
    this.linkBaselineWithWorkpack(rejected, this.workpack);

    // when
    final List<DashboardBaselineResponse> baselineResponses =
        this.underTest.findAllByWorkpackId(this.workpack.getId());

    // then
    assertThat(baselineResponses)
        .hasSize(0)
        .noneMatch(DashboardBaselineRepositoryTest::isApprovedOrProposed);
  }

  @Test
  @DisplayName("Should retrieve default value as workpack has an active baseline")
  void test4() {
    // given
    final Baseline draft = this.createBaseline("DRAFT", Status.DRAFT, LocalDateTime.now(), false);
    final Baseline proposed = this.createBaseline("PROPOSED", Status.PROPOSED, LocalDateTime.now(), false);
    final Baseline approved = this.createBaseline("APPROVED", Status.APPROVED, LocalDateTime.now(), true);
    final Baseline rejected = this.createBaseline("REJECTED", Status.REJECTED, LocalDateTime.now(), false);

    this.linkBaselineWithWorkpack(draft, this.workpack);
    this.linkBaselineWithWorkpack(proposed, this.workpack);
    this.linkBaselineWithWorkpack(approved, this.workpack);
    this.linkBaselineWithWorkpack(rejected, this.workpack);

    // when
    final List<DashboardBaselineResponse> baselineResponses =
        this.underTest.findAllByWorkpackId(this.workpack.getId());

    // then
    final Condition<DashboardBaselineResponse> defaultBaseline =
        new Condition<>(response -> isDefaultBaseline(response, approved), "Default Baseline");

    assertThat(baselineResponses)
        .hasSize(2)
        .haveExactly(1, defaultBaseline);
  }

  private static boolean isDefaultBaseline(final DashboardBaselineResponse baselineResponse, final Baseline baseline) {
    return baselineResponse.getDefaultBaseline() == Boolean.TRUE
        && Objects.equals(baselineResponse.getId(), baseline.getId());
  }

  @Test
  @DisplayName("Should retrieve default value as workpack have proposed baselines")
  void test5() {
    // given
    final LocalDateTime date1 = LocalDateTime.of(2006, 10, 10, 10, 10, 10);
    final LocalDateTime date2 = LocalDateTime.of(2004, 10, 10, 10, 10, 10);
    final LocalDateTime date3 = LocalDateTime.of(2002, 10, 10, 10, 10, 10);
    final LocalDateTime date4 = LocalDateTime.of(2000, 10, 10, 10, 10, 10);

    final Baseline draft = this.createBaseline("DRAFT", Status.DRAFT, date1, false);
    final Baseline proposed = this.createBaseline("PROPOSED", Status.PROPOSED, date2, false);
    final Baseline approved = this.createBaseline("APPROVED", Status.APPROVED, date3, false);
    final Baseline rejected = this.createBaseline("REJECTED", Status.REJECTED, date4, false);

    this.linkBaselineWithWorkpack(draft, this.workpack);
    this.linkBaselineWithWorkpack(proposed, this.workpack);
    this.linkBaselineWithWorkpack(approved, this.workpack);
    this.linkBaselineWithWorkpack(rejected, this.workpack);

    // when
    final List<DashboardBaselineResponse> baselineResponses =
        this.underTest.findAllByWorkpackId(this.workpack.getId());

    // then
    final Condition<DashboardBaselineResponse> defaultBaseline =
        new Condition<>(response -> isDefaultBaseline(response, proposed), "Default Baseline");

    assertThat(baselineResponses)
        .hasSize(2)
        .haveExactly(1, defaultBaseline);
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
