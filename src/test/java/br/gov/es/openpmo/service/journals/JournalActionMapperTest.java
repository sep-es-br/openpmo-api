package br.gov.es.openpmo.service.journals;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.baselines.Status;
import br.gov.es.openpmo.model.issue.StatusOfIssue;
import br.gov.es.openpmo.model.issue.response.IssueResponseStatus;
import br.gov.es.openpmo.model.journals.JournalAction;
import br.gov.es.openpmo.model.risk.StatusOfRisk;
import br.gov.es.openpmo.model.risk.response.RiskResponseStatus;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static br.gov.es.openpmo.service.journals.JournalActionMapper.mapBaselineStatus;
import static br.gov.es.openpmo.service.journals.JournalActionMapper.mapIssueResponseStatus;
import static br.gov.es.openpmo.service.journals.JournalActionMapper.mapIssueStatus;
import static br.gov.es.openpmo.service.journals.JournalActionMapper.mapRiskResponseStatus;
import static br.gov.es.openpmo.service.journals.JournalActionMapper.mapRiskStatus;
import static java.text.MessageFormat.format;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Tag("unit")
@DisplayName("Test conversion of enums to journal actions")
class JournalActionMapperTest {

  @BeforeEach
  void setUp() {
  }

  @Nested
  @DisplayName("Test conversion of baseline status to journal action")
  class BaselineStatusTest {

    @ParameterizedTest(name = "Convert Status.{0} to JournalActon.{1}")
    @ArgumentsSource(BaselineStatusProvider.class)
    @DisplayName("Should convert valid baseline status to journal action")
    void test1(final Status status, final JournalAction expected) {
      assertEquals(
          expected,
          mapBaselineStatus(status),
          format("Status should be {0}", expected)
      );
    }

    @Test
    @DisplayName("Should throw exception when baseline status is null")
    void test2() {
      assertThatThrownBy(() -> mapBaselineStatus(null))
          .isInstanceOf(NegocioException.class)
          .hasMessage(ApplicationMessage.BASELINE_UNDEFINED_STATUS);
    }

  }

  @Nested
  @DisplayName("Test conversion of issue status to journal action")
  class IssueStatusTest {

    @ParameterizedTest(name = "Convert StatusOfIssue.{0} to JournalActon.{1}")
    @ArgumentsSource(IssueStatusProvider.class)
    @DisplayName("Should convert issue status to journal action")
    void test1(final StatusOfIssue status, final JournalAction expected) {
      assertEquals(
          expected,
          mapIssueStatus(status),
          format("Status should be {0}", expected)
      );
    }

    @Test
    @DisplayName("Should throw exception when issue status is null")
    void test2() {
      assertThatThrownBy(() -> mapIssueStatus(null))
          .isInstanceOf(NegocioException.class)
          .hasMessage(ApplicationMessage.ISSUE_UNDEFINED_STATUS);
    }

  }

  @Nested
  @DisplayName("Test conversion of issue response status to journal action")
  class IssueResponseStatusTest {

    @ParameterizedTest(name = "Convert IssueResponseStatus.{0} to JournalActon.{1}")
    @ArgumentsSource(IssueResponseStatusProvider.class)
    @DisplayName("Should convert issue response status to journal action")
    void test1(final IssueResponseStatus status, final JournalAction expected) {
      assertEquals(
          expected,
          mapIssueResponseStatus(status),
          format("Status should be {0}", expected)
      );
    }

    @Test
    @DisplayName("Should throw exception when issue response status is null")
    void test2() {
      assertThatThrownBy(() -> mapIssueResponseStatus(null))
          .isInstanceOf(NegocioException.class)
          .hasMessage(ApplicationMessage.ISSUE_RESPONSE_UNDEFINED_STATUS);
    }

  }

  @Nested
  @DisplayName("Test conversion of risk status to journal action")
  class RiskStatusTest {

    @ParameterizedTest(name = "Convert StatusOfRisk.{0} to JournalActon.{1}")
    @ArgumentsSource(RiskStatusProvider.class)
    @DisplayName("Should convert risk status to journal action")
    void test1(final StatusOfRisk status, final JournalAction expected) {
      assertEquals(
          expected,
          mapRiskStatus(status),
          format("Status should be {0}", expected)
      );
    }

    @Test
    @DisplayName("Should throw exception when risk status is null")
    void test2() {
      assertThatThrownBy(() -> mapRiskStatus(null))
          .isInstanceOf(NegocioException.class)
          .hasMessage(ApplicationMessage.RISK_UNDEFINED_STATUS);
    }

  }

  @Nested
  @DisplayName("Test conversion of risk response status to journal action")
  class RiskResponseStatusTest {

    @ParameterizedTest(name = "Convert RiskResponseStatus.{0} to JournalActon.{1}")
    @ArgumentsSource(RiskResponseStatusProvider.class)
    @DisplayName("Should convert risk response status to journal action")
    void test1(final RiskResponseStatus status, final JournalAction expected) {
      assertEquals(
          expected,
          mapRiskResponseStatus(status),
          format("Status should be {0}", expected)
      );
    }

    @Test
    @DisplayName("Should throw exception when risk response status is null")
    void test2() {
      assertThatThrownBy(() -> mapRiskResponseStatus(null))
          .isInstanceOf(NegocioException.class)
          .hasMessage(ApplicationMessage.RISK_RESPONSE_UNDEFINED_STATUS);
    }

  }

}
