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
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Optional;

import static br.gov.es.openpmo.utils.ApplicationMessage.*;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class ProcessServiceTest {

  ProcessService service;

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

  Workpack workpack() {
    return new Workpack();
  }

  ProcessCreateDto processCreateDto(final Long idWorkpack) {
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

  Process process() {
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

  ProcessUpdateDto processUpdateDto() {
    return new ProcessUpdateDto(
        1L,
        "name",
        "note"
    );
  }

  ProcessResponse processResponse() {
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
        "abbr"
    ));
    return processResponse;
  }

  @Nested
  class Create {

    @Test
    void shouldCreateProcess() {
      when(ProcessServiceTest.this.repository.save(isA(Process.class))).thenReturn(ProcessServiceTest.this.process());
      when(ProcessServiceTest.this.workpackService.findById(anyLong())).thenReturn(ProcessServiceTest.this.workpack());
      final Process process = ProcessServiceTest.this.service.create(ProcessServiceTest.this.processCreateDto(1L));

      verify(ProcessServiceTest.this.repository, times(1)).save(isA(Process.class));
      verify(ProcessServiceTest.this.workpackService, times(1)).findById(anyLong());
      assertNotNull(process);
    }

    @Test
    void shouldThrowExceptionIfIdWorkpackIsNull() {
      final NegocioException exception = assertThrows(
          NegocioException.class,
          () -> ProcessServiceTest.this.service.create(ProcessServiceTest.this.processCreateDto(null))
      );
      assertEquals(ID_WORKPACK_NOT_NULL, exception.getMessage());
      verify(ProcessServiceTest.this.repository, never()).save(isA(Process.class));
      verify(ProcessServiceTest.this.workpackService, never()).findById(anyLong());
    }

  }

  @Nested
  class Update {

    @Test
    void shouldUpdateProcess() {
      when(ProcessServiceTest.this.repository.save(isA(Process.class), anyInt())).thenReturn(ProcessServiceTest.this.process());
      when(ProcessServiceTest.this.repository.findById(anyLong())).thenReturn(Optional.of(ProcessServiceTest.this.process()));
      when(ProcessServiceTest.this.eDocsApi.findProcessByProtocol(anyString(), eq(1L))).thenReturn(ProcessServiceTest.this.processResponse());

      ProcessServiceTest.this.service.update(ProcessServiceTest.this.processUpdateDto(), 1L);

      verify(ProcessServiceTest.this.repository, times(1)).save(isA(Process.class), anyInt());
      verify(ProcessServiceTest.this.repository, times(1)).findById(anyLong());
      verify(ProcessServiceTest.this.eDocsApi, times(1)).findProcessByProtocol(anyString(), eq(1L));
    }

    @Test
    void shouldThrowExceptionIfProcessNotFound() {

      final RegistroNaoEncontradoException exception = assertThrows(
          RegistroNaoEncontradoException.class,
          () -> ProcessServiceTest.this.service.update(ProcessServiceTest.this.processUpdateDto(), 1L)
      );

      verify(ProcessServiceTest.this.repository, never()).save(isA(Process.class), anyInt());
      verify(ProcessServiceTest.this.eDocsApi, never()).findProcessByProtocol(anyString(), eq(1L));
      verify(ProcessServiceTest.this.repository, times(1)).findById(anyLong());
      assertEquals(PROCESS_NOT_FOUND, exception.getMessage());
    }

  }

  @Nested
  class Delete {

    @Test
    void shouldDeleteProcessById() {
      doNothing().when(ProcessServiceTest.this.repository).deleteById(anyLong());

      ProcessServiceTest.this.service.deleteById(1L);

      verify(ProcessServiceTest.this.repository, times(1)).deleteById(anyLong());
    }

    @Test
    void shouldThrowExceptionIfIdIsNull() {

      final IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class,
          () -> ProcessServiceTest.this.service.deleteById(null)
      );

      assertEquals(PROCESS_ID_NOT_NULL, exception.getMessage());
    }

  }

  @Nested
  class FindById {

    @Test
    void shouldFindById() {
      when(ProcessServiceTest.this.repository.findById(anyLong())).thenReturn(Optional.of(ProcessServiceTest.this.process()));
      when(ProcessServiceTest.this.eDocsApi.findProcessByProtocol(anyString(), eq(1L))).thenReturn(ProcessServiceTest.this.processResponse());
      when(ProcessServiceTest.this.repository.save(isA(Process.class), anyInt())).thenReturn(ProcessServiceTest.this.process());

      final ProcessDetailDto processFromEDocsDto = ProcessServiceTest.this.service.findById(1L, 1L);

      verify(ProcessServiceTest.this.repository, times(1)).findById(anyLong());
      verify(ProcessServiceTest.this.repository, times(1)).save(isA(Process.class), anyInt());
      verify(ProcessServiceTest.this.eDocsApi, times(1)).findProcessByProtocol(anyString(), eq(1L));
    }

    @Test
    void shouldThrowExceptionIfProcessNotFound() {
      when(ProcessServiceTest.this.repository.findById(anyLong())).thenReturn(Optional.empty());

      Assertions.assertThatThrownBy(() -> ProcessServiceTest.this.service.findById(1L, 1L))
          .hasMessage(PROCESS_NOT_FOUND)
          .isInstanceOf(RegistroNaoEncontradoException.class);

      verify(ProcessServiceTest.this.repository, times(1)).findById(anyLong());
      verify(ProcessServiceTest.this.repository, never()).save(isA(Process.class));
      verify(ProcessServiceTest.this.eDocsApi, never()).findProcessByProtocol(anyString(), eq(1L));
    }

  }

  @Nested
  class FindAll {

    @Test
    void shouldFindAllProcessWithoutCustomFilter() {
      when(ProcessServiceTest.this.repository.findAllByWorkpack(anyLong())).thenReturn(asList(ProcessServiceTest.this.process()));

      ProcessServiceTest.this.service.findAllAsCardDto(1L, null);

      verify(ProcessServiceTest.this.repository, times(1)).findAllByWorkpack(anyLong());
      verify(ProcessServiceTest.this.customFilterService, never()).findById(anyLong());
      verify(ProcessServiceTest.this.findAllProcess, never()).execute(isA(CustomFilter.class), anyMap());
    }

    @Test
    void shouldFindAllProcessUsingCustomFilter() {
      when(ProcessServiceTest.this.customFilterService.findById(anyLong())).thenReturn(new CustomFilter());
      when(ProcessServiceTest.this.findAllProcess.execute(
          isA(CustomFilter.class),
          anyMap()
      )).thenReturn(asList(ProcessServiceTest.this.process()));

      ProcessServiceTest.this.service.findAllAsCardDto(1L, 1L);

      verify(ProcessServiceTest.this.repository, never()).findAllByWorkpack(anyLong());
      verify(ProcessServiceTest.this.customFilterService, times(1)).findById(anyLong());
      verify(ProcessServiceTest.this.findAllProcess, times(1)).execute(isA(CustomFilter.class), anyMap());
    }

    @Test
    void shouldThrowExceptionIfIdWorkpackIsNull() {
      Assertions.assertThatThrownBy(() -> ProcessServiceTest.this.service.findAllAsCardDto(null, 1L))
          .hasMessage(ID_WORKPACK_NOT_NULL)
          .isInstanceOf(IllegalArgumentException.class);
    }

  }

}
