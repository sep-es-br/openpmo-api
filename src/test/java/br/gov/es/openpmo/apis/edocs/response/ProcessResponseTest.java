package br.gov.es.openpmo.apis.edocs.response;

import br.gov.es.openpmo.apis.edocs.ProcessTimeline;
import br.gov.es.openpmo.exception.NegocioException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static br.gov.es.openpmo.utils.ApplicationMessage.PROCESS_HISTORY_EMPTY;
import static java.util.Arrays.asList;

@Tag("unit")
@DisplayName("Test Process History rules")
@ExtendWith(MockitoExtension.class)
class ProcessResponseTest {


  @Test
  @DisplayName("Should find current organization abbreviation on process history")
  void shouldFindCurrentOrganization() {
    final JSONObject json = buildProcessAsJson();
    final ProcessResponse process = new ProcessResponse(json);
    process.addHistory(buildHistoryProcess());

    final String abbreviation = process.getCurrentOrganizationAbbreviation();
    assertEquals("NM 1", abbreviation, "Should has same abbreviation");
  }

  static JSONObject buildProcessAsJson() {
    return new JSONObject()
      .put("id", "570085f2-141e-4e79-8ed0-070ee89b9614")
      .put("protocolo", "2021-J8BS5")
      .put("situacao", "EmAndamento")
      .put("classificacao", "PRIORIDADE")
      .put("resumo", "Celebração de parceria com Organizações da Sociedade Civil para implementação");
  }

  static List<ProcessHistoryResponse> buildHistoryProcess() {
    return asList(
      new ProcessHistoryResponse(LocalDateTime.now().minusDays(10), "name 1", "NM 1"),
      new ProcessHistoryResponse(LocalDateTime.now().minusDays(20), "name 2", "NM 2"),
      new ProcessHistoryResponse(LocalDateTime.now().minusDays(23), "name 2", "NM 2"),
      new ProcessHistoryResponse(LocalDateTime.now().minusDays(24), "name 3", "NM 3"),
      new ProcessHistoryResponse(LocalDateTime.now().minusDays(35), "name 3", "NM 3"),
      new ProcessHistoryResponse(LocalDateTime.now().minusDays(38), "name 1", "NM 1")
    );
  }


  @Test
  @DisplayName("Should throw exception if process history is empty")
  void shouldThrowExceptionIfProcessHistoryIsEmpty() {
    final JSONObject json = buildProcessAsJson();
    final ProcessResponse process = new ProcessResponse(json);
    final NegocioException negocioException = assertThrows(NegocioException.class, process::getCurrentOrganizationAbbreviation);
    assertEquals(
      PROCESS_HISTORY_EMPTY,
      negocioException.getMessage(),
      "Should has same error message"
    );
  }

  @Test
  @DisplayName("Should calculate length of stay on")
  void shouldCalculateLengthOfStayOn() {
    final JSONObject json = buildProcessAsJson();
    final ProcessResponse process = new ProcessResponse(json);
    process.addHistory(buildHistoryProcess());

    final long lengthOfStayOn = process.lengthOfStayOn();
    assertEquals(10, lengthOfStayOn, "Should have same lengthOfStayOn value");
  }

  @Nested
  @DisplayName("Test creation of timeline")
  class TimelineTest {
    private ProcessResponse process;
    private long historyLength;

    @BeforeEach
    void setUp() {
      final JSONObject json = buildProcessAsJson();
      this.process = new ProcessResponse(json);
      final List<ProcessHistoryResponse> processHistory = buildHistoryProcess();
      this.historyLength = processHistory.size();
      this.process.addHistory(processHistory);
    }

    @Test
    @DisplayName("Should return 'ProcessHistoryResponse' list as 'ProcessTimeline' list")
    void shouldReturnHistoryAsTimeline() {
      final List<ProcessTimeline> timeline = this.process.timeline();
      assertEquals(this.historyLength, timeline.size(), "Should has same size");
    }

    @Test
    @DisplayName("Should have three days in first process of timeline")
    void shouldHaveThreeDaysInFirstProcess() {
      final List<ProcessTimeline> timeline = this.process.timeline();
      assertEquals(3, timeline.get(0).daysDuration(), "Should has same days duration");
    }

    @Test
    @DisplayName("Should return same days duration if have one process in timeline")
    void shouldReturnSameDaysDurationIfHaveOneProcessInTimeline() {
      final JSONObject json = buildProcessAsJson();
      this.process = new ProcessResponse(json);
      this.process.addHistory(Collections.singletonList(
        new ProcessHistoryResponse(
          LocalDateTime.now().minusDays(10),
          "name 1",
          "NM 1"
        )));
      final List<ProcessTimeline> timeline = this.process.timeline();
      assertEquals(this.process.lengthOfStayOn(), timeline.get(0).daysDuration(), "Should has same days duration");
    }
  }

  @Nested
  @DisplayName("Test value of 'priority' field according 'classificacao' property")
  class PrioridadeFieldTest {
    @Test
    @DisplayName("Should set true if 'classificacao' is 'prioridade'")
    void shouldSetTrueIfClassificacaoIsPrioridade() {
      final JSONObject json = buildProcessAsJson()
        .put("classificao", "PRIORIDADE");

      final ProcessResponse process = new ProcessResponse(json);
      assertTrue(process.getPriority(), "Priority should be true");
    }

    @Test
    @DisplayName("Should set false if 'classificacao' is 'nao_prioridade'")
    void shouldSetFalseIfClassificaoIsNotPrioridade() {
      final JSONObject json = buildProcessAsJson()
        .put("classificacao", "NAO_PRIORIDADE");

      final ProcessResponse process = new ProcessResponse(json);
      assertFalse(process.getPriority(), "Priority should be false");
    }

    @Test
    @DisplayName("Should set false if 'classificacao' is null")
    void shouldSetFalseIfClassificacaoFieldIsNull() {
      final JSONObject json = buildProcessAsJson();
      json.remove("classificacao");
      final ProcessResponse process = new ProcessResponse(json);
      assertFalse(process.getPriority(), "Priority should be false");
    }
  }

  @Nested
  @DisplayName("Test creation of ProcessResponse from JSON")
  class CreateFromJsonTest {
    @Test
    @DisplayName("Should set id when create from JSON Object")
    void shouldSetIdFieldOnCreateWithJsonObject() {
      final JSONObject json = buildProcessAsJson();
      final ProcessResponse process = new ProcessResponse(json);
      assertEquals(json.get("id"), process.getId(), "Id should be equals");
    }

    @Test
    @DisplayName("Should set processNumber when create from JSON Object")
    void shouldSetProcessNumberFieldOnCreateWithJsonObject() {
      final JSONObject json = buildProcessAsJson();
      final ProcessResponse process = new ProcessResponse(json);
      assertEquals(json.get("protocolo"), process.getProcessNumber(), "ProcessNumber should be equals");
    }

    @Test
    @DisplayName("Should set status when create from JSON Object")
    void shouldSetStatusFieldOnCreateWithJsonObject() {
      final JSONObject json = buildProcessAsJson();
      final ProcessResponse process = new ProcessResponse(json);
      assertEquals(json.get("situacao"), process.getStatus(), "Status should be equals");
    }

    @Test
    @DisplayName("Should set subject when create from JSON Object")
    void shouldSetSubjectOnCreateWithJsonObject() {
      final JSONObject json = buildProcessAsJson();
      final ProcessResponse process = new ProcessResponse(json);
      assertEquals(json.get("resumo"), process.getSubject(), "Subject should be equals");
    }
  }


}
