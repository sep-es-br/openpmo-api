package br.gov.es.openpmo.scheduler;

import br.gov.es.openpmo.model.workpacks.Program;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.scheduler.updatestatus.UpdateProgramCompletedStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Tag("unit")
@DisplayName("Test update status 'completed' of Programs")
@ExtendWith(MockitoExtension.class)
class UpdateProgramCompletedStatusTest {

  private static final Program PROGRAM = mock(Program.class);
  private UpdateProgramCompletedStatus updateProgramCompletedStatus;
  @Mock
  private WorkpackRepository repository;
  @Captor
  private ArgumentCaptor<Collection<Program>> captureCompletedProgram;


  @BeforeEach
  void setUp() {
    this.updateProgramCompletedStatus = new UpdateProgramCompletedStatus(
      this.repository
    );
  }

  @Test
  @DisplayName("Should find all programs stored")
  void test1() {
    doReturn(new HashSet<>(asList(
      mock(Program.class),
      mock(Program.class),
      mock(Program.class),
      mock(Program.class)
    ))).when(this.repository).findAllPrograms();

    this.updateProgramCompletedStatus.update();

    verify(this.repository, times(1)).findAllPrograms();
  }

  @Test
  @DisplayName("Should not update 'completed' status when remain projects to complete")
  void test2() {
    doReturn(new HashSet<>(Collections.singletonList(
      PROGRAM
    ))).when(this.repository).findAllPrograms();
    doReturn(1L)
      .when(PROGRAM)
      .getId();
    doReturn(true)
      .when(this.repository)
      .hasRemainProjectsToComplete(1L);
    doReturn(null)
      .when(this.repository)
      .save(anyList(), eq(0));

    this.updateProgramCompletedStatus.update();

    verify(this.repository, times(1))
      .findAllPrograms();
    verify(this.repository, times(1))
      .hasRemainProjectsToComplete(eq(1L));
    verify(this.repository, times(1))
      .save(this.captureCompletedProgram.capture(), eq(0));
    final Collection<Program> value = this.captureCompletedProgram.getValue();
    assertThat(value).hasSize(0);
  }

  @Test
  @DisplayName("Should not change 'completed' status of project when remain projects to complete")
  void test3() {
    doReturn(new HashSet<>(Collections.singletonList(
      PROGRAM
    ))).when(this.repository).findAllPrograms();
    doReturn(1L)
      .when(PROGRAM)
      .getId();
    doReturn(true)
      .when(this.repository)
      .hasRemainProjectsToComplete(1L);
    doReturn(null)
      .when(this.repository)
      .save(anyList(), eq(0));

    this.updateProgramCompletedStatus.update();

    verify(PROGRAM, never()).setCompleted(anyBoolean());
  }

  @Test
  @DisplayName("Should update 'completed' status when all projects are completed")
  void test4() {
    doReturn(new HashSet<>(Collections.singletonList(
      PROGRAM
    ))).when(this.repository).findAllPrograms();
    doReturn(1L)
      .when(PROGRAM)
      .getId();
    doReturn(false)
      .when(this.repository)
      .hasRemainProjectsToComplete(1L);
    doReturn(null)
      .when(this.repository)
      .save(anyList(), eq(0));

    this.updateProgramCompletedStatus.update();

    verify(this.repository, times(1))
      .findAllPrograms();
    verify(this.repository, times(1))
      .hasRemainProjectsToComplete(eq(1L));

    verify(this.repository, times(1))
      .save(this.captureCompletedProgram.capture(), eq(0));
    final Collection<Program> value = this.captureCompletedProgram.getValue();
    assertThat(value).hasSize(1);
  }
}
