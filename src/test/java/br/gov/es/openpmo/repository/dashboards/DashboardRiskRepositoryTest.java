package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.model.risk.Importance;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.model.risk.StatusOfRisk;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.RiskRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
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

import static br.gov.es.openpmo.model.risk.Importance.*;
import static br.gov.es.openpmo.model.risk.StatusOfRisk.*;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("database")
@DataNeo4jTest
@DisplayName("Test if find all Risks of Workpack")
@Testcontainers
class DashboardRiskRepositoryTest extends BaseRepositoryTest {

  @Autowired
  RiskRepository riskRepository;

  @Autowired
  WorkpackRepository workpackRepository;

  private Workpack workpack;

  @BeforeEach
  void setUp() {
    this.workpack = new Workpack();
    this.workpackRepository.save(this.workpack);
  }

  @Test
  @DisplayName("Should count HIGH and OPENED Risks of Workpack")
  void test1() {
    this.givenWorkpackHasRelatedRisk();
    final long highRisks = this.riskRepository.countOpenedRiskOfWorkpackByImportance(
        this.workpack.getId(),
        HIGH.name()
    );
    assertThat(highRisks)
        .isEqualTo(2);
  }

  private void givenWorkpackHasRelatedRisk() {
    this.createRisk(this.workpack, HIGH, NOT_GONNA_HAPPEN);
    this.createRisk(this.workpack, LOW, OPEN);
    this.createRisk(this.workpack, MEDIUM, OPEN);
    this.createRisk(this.workpack, LOW, HAPPENED);
    this.createRisk(this.workpack, MEDIUM, NOT_GONNA_HAPPEN);
    this.createRisk(this.workpack, LOW, HAPPENED);
    this.createRisk(this.workpack, LOW, OPEN);
    this.createRisk(this.workpack, HIGH, OPEN);
    this.createRisk(this.workpack, HIGH, OPEN);
  }

  private void createRisk(final Workpack workpack, final Importance importance, final StatusOfRisk status) {
    final Risk risk = new Risk();
    risk.setWorkpack(workpack);
    risk.setImportance(importance);
    risk.setStatus(status);
    this.riskRepository.save(risk);
  }

  @Test
  @DisplayName("Should count 0 when Workpack not has Risk")
  void test2() {
    final long allRisksTotal = this.riskRepository.countAllRisksOfWorkpack(this.workpack.getId());
    assertThat(allRisksTotal).isEqualTo(0);
  }

  @Test
  @DisplayName("Should count total Risks of Workpack")
  void test3() {
    this.givenWorkpackHasRelatedRisk();
    final Long allRisksTotal = this.riskRepository.countAllRisksOfWorkpack(this.workpack.getId());
    assertThat(allRisksTotal).isEqualTo(9);
  }

  @Test
  @DisplayName("Should count total Risks Closed of Workpack")
  void test4() {
    this.givenWorkpackHasRelatedRisk();
    final Long closedRisks = this.riskRepository.countClosedRisksOfWorkpack(this.workpack.getId());
    assertThat(closedRisks)
        .isEqualTo(4);
  }

  @Test
  @DisplayName("Should count LOW and OPENED Risks of Workpack")
  void test5() {
    this.givenWorkpackHasRelatedRisk();
    final long lowRisks = this.riskRepository.countOpenedRiskOfWorkpackByImportance(
        this.workpack.getId(),
        LOW.name()
    );
    assertThat(lowRisks)
        .isEqualTo(2);
  }

  @Test
  @DisplayName("Should count MEDIUM and OPENED Risks of Workpack")
  void test6() {
    this.givenWorkpackHasRelatedRisk();
    final long mediumRisks = this.riskRepository.countOpenedRiskOfWorkpackByImportance(
        this.workpack.getId(),
        MEDIUM.name()
    );
    assertThat(mediumRisks)
        .isEqualTo(1);
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
