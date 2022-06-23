package br.gov.es.openpmo.service.process;

import br.gov.es.openpmo.apis.edocs.EDocsApi;
import br.gov.es.openpmo.apis.edocs.response.ProcessHistoryResponse;
import br.gov.es.openpmo.apis.edocs.response.ProcessResponse;
import br.gov.es.openpmo.dto.process.ProcessCreateDto;
import br.gov.es.openpmo.dto.process.ProcessDetailDto;
import br.gov.es.openpmo.dto.process.ProcessUpdateDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.process.Process;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.ProcessRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllProcessUsingCustomFilter;
import br.gov.es.openpmo.service.filters.CustomFilterService;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static br.gov.es.openpmo.utils.ApplicationMessage.ID_WORKPACK_NOT_NULL;
import static br.gov.es.openpmo.utils.ApplicationMessage.PROCESS_ID_NOT_NULL;
import static br.gov.es.openpmo.utils.ApplicationMessage.PROCESS_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@DisplayName("Test Process rules")
@ExtendWith(MockitoExtension.class)
class ProcessServiceTest {

  @Mock
  ProcessRepository repository;
  @Mock
  WorkpackService workpackService;
  @Mock
  EDocsApi eDocsApi;
  @Mock
  FindAllProcessUsingCustomFilter findAllProcess;
  @Mock
  CustomFilterService customFilterService;
  private ProcessService service;

  static Workpack workpack() {
    return new Workpack();
  }

  static ProcessCreateDto processCreateDto(final Long idWorkpack) {
    return new ProcessCreateDto(
      idWorkpack,
      "Processo 2021-J8BS5",
      "Note 2021-J8BS5",
      "2021-J8BS5",
      "EmAndamento",
      "Subject 2021-J8BS5",
      "J8BS5",
      3L,
      false
    );
  }

  static Process process() {
    return new Process(
      "",
      "",
      "",
      "",
      1L,
      "",
      false,
      "",
      new Workpack()
    );
  }

  static ProcessUpdateDto processUpdateDto() {
    return new ProcessUpdateDto(
      1L,
      "name",
      "note"
    );
  }

  static ProcessResponse processResponse() {
    final ProcessResponse processResponse = new ProcessResponse(
      "id",
      "processNumber",
      "subject",
      "status",
      true
    );
    processResponse.addHistory(new ProcessHistoryResponse(
      LocalDateTime.now(),
      "name",
      "abbr",
      "",
      null
    ));
    return processResponse;
  }

  @BeforeEach
  void setUp() {
    this.service = new ProcessService(
      this.repository,
      this.eDocsApi,
      this.workpackService,
      this.findAllProcess,
      this.customFilterService
    );
  }

  @Nested
  @DisplayName("Test Process creation")
  class CreateTest {

    @Test
    @DisplayName("Should create process")
    void shouldCreateProcess() {
      when(ProcessServiceTest.this.repository.save(isA(Process.class))).thenReturn(ProcessServiceTest.process());
      when(ProcessServiceTest.this.workpackService.findById(anyLong())).thenReturn(ProcessServiceTest.workpack());
      final Process process = ProcessServiceTest.this.service.create(ProcessServiceTest.processCreateDto(1L));

      verify(ProcessServiceTest.this.repository, times(1)).save(isA(Process.class));
      verify(ProcessServiceTest.this.workpackService, times(1)).findById(anyLong());
      assertNotNull(process, "Process should be not null");
    }

    @Test
    @DisplayName("Should throw exception if workpack id is null")
    void shouldThrowExceptionIfIdWorkpackIsNull() {
      final NegocioException exception = assertThrows(
        NegocioException.class,
        () -> ProcessServiceTest.this.service.create(ProcessServiceTest.processCreateDto(null))
      );
      assertEquals(ID_WORKPACK_NOT_NULL, exception.getMessage(), "Should have same exception message");
      verify(ProcessServiceTest.this.repository, never()).save(isA(Process.class));
      verify(ProcessServiceTest.this.workpackService, never()).findById(anyLong());
    }

  }

  @Nested
  @DisplayName("Test Process update")
  class UpdateTest {

    @Test
    @DisplayName("Should update process")
    void shouldUpdateProcess() {
      when(ProcessServiceTest.this.repository.save(isA(Process.class), anyInt())).thenReturn(ProcessServiceTest.process());
      when(ProcessServiceTest.this.repository.findById(anyLong())).thenReturn(Optional.of(ProcessServiceTest.process()));
      when(ProcessServiceTest.this.eDocsApi.findProcessByProtocol(
        anyString(),
        eq(1L)
      )).thenReturn(ProcessServiceTest.processResponse());

      ProcessServiceTest.this.service.update(ProcessServiceTest.processUpdateDto(), 1L);

      verify(ProcessServiceTest.this.repository, times(1)).save(isA(Process.class), anyInt());
      verify(ProcessServiceTest.this.repository, times(1)).findById(anyLong());
      verify(ProcessServiceTest.this.eDocsApi, times(1)).findProcessByProtocol(anyString(), eq(1L));
    }

    @Test
    @DisplayName("Should throw excpetion if process not found")
    void shouldThrowExceptionIfProcessNotFound() {

      final RegistroNaoEncontradoException exception = assertThrows(
        RegistroNaoEncontradoException.class,
        () -> ProcessServiceTest.this.service.update(ProcessServiceTest.processUpdateDto(), 1L)
      );

      verify(ProcessServiceTest.this.repository, never()).save(isA(Process.class), anyInt());
      verify(ProcessServiceTest.this.eDocsApi, never()).findProcessByProtocol(anyString(), eq(1L));
      verify(ProcessServiceTest.this.repository, times(1)).findById(anyLong());
      assertEquals(PROCESS_NOT_FOUND, exception.getMessage(), "Should have same exception message");
    }

  }

  @Nested
  @DisplayName("Test Process Delete")
  class DeleteTest {

    @Test
    @DisplayName("Should delete process by id")
    void shouldDeleteProcessById() {
      doNothing().when(ProcessServiceTest.this.repository).deleteById(anyLong());

      ProcessServiceTest.this.service.deleteById(1L);

      verify(ProcessServiceTest.this.repository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should throw exception if id is null")
    void shouldThrowExceptionIfIdIsNull() {

      final IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> ProcessServiceTest.this.service.deleteById(null)
      );

      assertEquals(PROCESS_ID_NOT_NULL, exception.getMessage(), "Should have same exception message");
    }

  }

  @Nested
  @DisplayName("Test Process find by id")
  class FindByIdTest {

    @Test
    @DisplayName("Should find process by id")
    void shouldFindById() {
      when(ProcessServiceTest.this.repository.findById(anyLong())).thenReturn(Optional.of(ProcessServiceTest.process()));
      when(ProcessServiceTest.this.eDocsApi.findProcessByProtocol(
        anyString(),
        eq(1L)
      )).thenReturn(ProcessServiceTest.processResponse());
      when(ProcessServiceTest.this.repository.save(isA(Process.class), anyInt())).thenReturn(ProcessServiceTest.process());

      final ProcessDetailDto processFromEDocsDto = ProcessServiceTest.this.service.findById(1L, 1L);

      verify(ProcessServiceTest.this.repository, times(1)).findById(anyLong());
      verify(ProcessServiceTest.this.repository, times(1)).save(isA(Process.class), anyInt());
      verify(ProcessServiceTest.this.eDocsApi, times(1)).findProcessByProtocol(anyString(), eq(1L));
    }

    @Test
    @DisplayName("Should throw exception if process not found")
    void shouldThrowExceptionIfProcessNotFound() {
      when(ProcessServiceTest.this.repository.findById(anyLong())).thenReturn(Optional.empty());

      assertThatThrownBy(() -> ProcessServiceTest.this.service.findById(1L, 1L))
        .hasMessage(PROCESS_NOT_FOUND)
        .isInstanceOf(RegistroNaoEncontradoException.class);

      verify(ProcessServiceTest.this.repository, times(1)).findById(anyLong());
      verify(ProcessServiceTest.this.repository, never()).save(isA(Process.class));
      verify(ProcessServiceTest.this.eDocsApi, never()).findProcessByProtocol(anyString(), eq(1L));
    }

  }

  @Nested
  @DisplayName("Test Process find all")
  class FindAllTest {

    @Test
    @DisplayName("Should find all process without custom filter")
    void shouldFindAllProcessWithoutCustomFilter() {
      when(ProcessServiceTest.this.repository.findAllByWorkpack(anyLong())).thenReturn(Collections.singletonList(ProcessServiceTest.process()));

      ProcessServiceTest.this.service.findAllAsCardDto(1L, null, 2L);

      verify(ProcessServiceTest.this.repository, times(1)).findAllByWorkpack(anyLong());
      verify(ProcessServiceTest.this.customFilterService, never()).findById(anyLong(), anyLong());
      verify(ProcessServiceTest.this.findAllProcess, never()).execute(isA(CustomFilter.class), anyMap());
    }

    @Test
    @DisplayName("Should find all process using custom filter")
    void shouldFindAllProcessUsingCustomFilter() {
      //      when(ProcessServiceTest.this.customFilterService.findById(anyLong())).thenReturn(new CustomFilter(
      //        customFilterCreateRequest.getRequest().getName(),
      //        customFilterCreateRequest.getCustomFilterEnum(),
      //        customFilterCreateRequest.getRequest().getFavorite(),
      //        customFilterCreateRequest.getRequest().getSortByDirection(),
      //        customFilterCreateRequest.getRequest().getSortBy(),
      //        workpackModel,
      //        null
      //      ));
      //      when(ProcessServiceTest.this.findAllProcess.execute(
      //        isA(CustomFilter.class),
      //        anyMap()
      //      )).thenReturn(Collections.singletonList(ProcessServiceTest.process()));
      //
      //      ProcessServiceTest.this.service.findAllAsCardDto(1L, 1L);
      //
      //      verify(ProcessServiceTest.this.repository, never()).findAllByWorkpack(anyLong());
      //      verify(ProcessServiceTest.this.customFilterService, times(1)).findById(anyLong());
      //      verify(ProcessServiceTest.this.findAllProcess, times(1)).execute(isA(CustomFilter.class), anyMap());
    }

    @Test
    @DisplayName("Should throw exception if id workpack is null")
    void shouldThrowExceptionIfIdWorkpackIsNull() {
      assertThatThrownBy(() -> ProcessServiceTest.this.service.findAllAsCardDto(null, 1L, 2L))
        .hasMessage(ID_WORKPACK_NOT_NULL)
        .isInstanceOf(IllegalArgumentException.class);
    }

  }

}
