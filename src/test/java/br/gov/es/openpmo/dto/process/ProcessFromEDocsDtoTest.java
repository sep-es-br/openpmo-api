package br.gov.es.openpmo.dto.process;

import br.gov.es.openpmo.apis.edocs.response.ProcessHistoryResponse;
import br.gov.es.openpmo.apis.edocs.response.ProcessResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;

@Tag("unit")
@DisplayName("Test creation of ProcessFromEDocsDto")
@ExtendWith(MockitoExtension.class)
class ProcessFromEDocsDtoTest {

  private static List<ProcessHistoryResponse> buildHistoryProcess() {
    return asList(
      new ProcessHistoryResponse(LocalDateTime.now().minusDays(10), "name 1", "NM 1"),
      new ProcessHistoryResponse(LocalDateTime.now().minusDays(20), "name 2", "NM 2"),
      new ProcessHistoryResponse(LocalDateTime.now().minusDays(24), "name 3", "NM 3"),
      new ProcessHistoryResponse(LocalDateTime.now().minusDays(36), "name 1", "NM 1")
    );
  }

  @Nested
  @DisplayName("Test creation of ProcessFromEDocsDto from ProcessResponse")
  class CreateFromProcessResponseTest {

    private ProcessResponse process;

    @BeforeEach
    void createProcess() {
      this.process = new ProcessResponse(
        "570085f2-141e-4e79-8ed0-070ee89b9614",
        "2021-J8BS5",
        "Celebração de parceria com Organizações da Sociedade Civil para implementação de 01",
        "EmAndamento",
        true
      );
      this.process.addHistory(buildHistoryProcess());
    }

    @Test
    @DisplayName("Should set process number")
    void shouldSetProcessNumber() {
      final ProcessFromEDocsDto dto = ProcessFromEDocsDto.of(this.process);
      assertEquals(
        this.process.getProcessNumber(),
        dto.getProcessNumber(),
        "processNumber should be equals"
      );

    }

    @Test
    @DisplayName("Should set subject")
    void shouldSetSubject() {
      final ProcessFromEDocsDto dto = ProcessFromEDocsDto.of(this.process);
      assertEquals(
        this.process.getSubject(),
        dto.getSubject(),
        "subject should be equals"
      );
    }

    @Test
    @DisplayName("Should set status")
    void shouldSetStatus() {
      final ProcessFromEDocsDto dto = ProcessFromEDocsDto.of(this.process);
      assertEquals(
        this.process.getStatus(),
        dto.getStatus(),
        "status should be equals"
      );
    }

    @Test
    @DisplayName("Should set current organization")
    void shouldSetCurrentOrganization() {
      final ProcessFromEDocsDto dto = ProcessFromEDocsDto.of(this.process);
      assertEquals(
        this.process.getCurrentOrganizationAbbreviation(),
        dto.getCurrentOrganization(),
        "currentOrganization should be equals"
      );
    }

    @Test
    @DisplayName("Should set length of stay on")
    void shouldSetLengthOfStayOn() {
      final ProcessFromEDocsDto dto = ProcessFromEDocsDto.of(this.process);
      assertEquals(
        this.process.lengthOfStayOn(),
        dto.getLengthOfStayOn(),
        "lengthOfStayOn should be equals"
      );
    }

    @Test
    @DisplayName("Should set priority")
    void shouldSetPriority() {
      final ProcessFromEDocsDto dto = ProcessFromEDocsDto.of(this.process);
      assertEquals(this.process.getPriority(), dto.getPriority(), "priority should be equals");
    }

  }

}
