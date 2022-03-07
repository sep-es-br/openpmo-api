package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.util.DashboardDatasheetUtil;
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

import java.text.MessageFormat;

import static org.junit.jupiter.api.Assertions.assertSame;

@Tag("database")
@DataNeo4jTest
@DisplayName("Test if dashboard datasheet is being retrieved correctly")
@Testcontainers
class DashboardDatasheetRepositoryTest extends BaseRepositoryTest {

  @Autowired
  DashboardDatasheetRepository underTest;

  @Autowired
  WorkpackRepository workpackRepository;

  Workpack workpack;

  DashboardDatasheetUtil util;

  @BeforeEach
  void setUp() {
    this.util = new DashboardDatasheetUtil(this.workpackRepository);
    this.workpack = this.util.createWorkpack();
  }

  @Test
  @DisplayName("Should retrived the quantity of projects correctly")
  void quantityOfProjects() {
    //given
    this.util.createProjectRelationships(this.workpack);

    // when
    final Long actual = this.underTest.quantityOfProjects(this.workpack.getId());

    // then
    final Long expected = 10L;
    assertSame(expected, actual, getErrorMessage(expected, actual));
  }

  private static String getErrorMessage(final Long expected, final Long actual) {
    return MessageFormat.format("{0} was expected, but {1} was found!", expected, actual);
  }

  @Test
  @DisplayName("Should retrived the quantity of deliverables correctly")
  void quantityOfDeliverables() {
    //given
    this.util.createDeliverablesRelationships(this.workpack);

    // when
    final Long actual = this.underTest.quantityOfDeliverables(this.workpack.getId());

    // then
    final Long expected = 10L;
    assertSame(expected, actual, getErrorMessage(expected, actual));
  }

  @Test
  @DisplayName("Should retrived the quantity of milestone correctly")
  void quantityOfMilestones() {
    //given
    this.util.createMilestoneRelationships(this.workpack);

    // when
    final Long actual = this.underTest.quantityOfMilestones(this.workpack.getId());

    // then
    final Long expected = 10L;
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
