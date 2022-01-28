package br.gov.es.openpmo.scheduler;

import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.scheduler.updatestatus.UpdateProjectsCompletedStatus;
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
@DisplayName("Test update status 'completed' of Projects")
@ExtendWith(MockitoExtension.class)
class UpdateProjectCompletedStatusTest {

  private static final Project PROJECT = mock(Project.class);
  private UpdateProjectsCompletedStatus updateProjectsCompletedStatus;
  @Mock
  private WorkpackRepository repository;
  @Captor
  private ArgumentCaptor<Collection<Project>> captureCompletedProjects;


  @BeforeEach
  void setUp() {
    this.updateProjectsCompletedStatus = new UpdateProjectsCompletedStatus(
      this.repository
    );
  }

  @Test
  @DisplayName("Should find all projects stored")
  void test1() {
    doReturn(new HashSet<>(asList(
      mock(Project.class),
      mock(Project.class),
      mock(Project.class),
      mock(Project.class)
    ))).when(this.repository).findAllProjects();

    this.updateProjectsCompletedStatus.update();

    verify(this.repository, times(1)).findAllProjects();
  }

  @Test
  @DisplayName("Should not update 'completed' status when remain deliverable to complete")
  void test2() {
    doReturn(new HashSet<>(Collections.singletonList(
      PROJECT
    ))).when(this.repository).findAllProjects();
    doReturn(1L)
      .when(PROJECT)
      .getId();
    doReturn(true)
      .when(this.repository)
      .hasDeliverableToComplete(1L);
    doReturn(null)
      .when(this.repository)
      .save(anyList(), eq(0));

    this.updateProjectsCompletedStatus.update();

    verify(this.repository, times(1))
      .findAllProjects();
    verify(this.repository, times(1))
      .hasDeliverableToComplete(eq(1L));
    verify(this.repository, times(1))
      .save(this.captureCompletedProjects.capture(), eq(0));
    final Collection<Project> value = this.captureCompletedProjects.getValue();
    assertThat(value).hasSize(0);
  }

  @Test
  @DisplayName("Should not change 'completed' status of project when remain deliverables to complete")
  void test3() {
    doReturn(new HashSet<>(Collections.singletonList(
      PROJECT
    ))).when(this.repository).findAllProjects();
    doReturn(1L)
      .when(PROJECT)
      .getId();
    doReturn(true)
      .when(this.repository)
      .hasDeliverableToComplete(1L);
    doReturn(null)
      .when(this.repository)
      .save(anyList(), eq(0));

    this.updateProjectsCompletedStatus.update();

    verify(PROJECT, never()).setCompleted(anyBoolean());
  }

  @Test
  @DisplayName("Should update 'completed' status when all deliverables are completed")
  void test4() {
    doReturn(new HashSet<>(Collections.singletonList(
      PROJECT
    ))).when(this.repository).findAllProjects();
    doReturn(1L)
      .when(PROJECT)
      .getId();
    doReturn(false)
      .when(this.repository)
      .hasDeliverableToComplete(1L);
    doReturn(null)
      .when(this.repository)
      .save(anyList(), eq(0));

    this.updateProjectsCompletedStatus.update();

    verify(this.repository, times(1))
      .findAllProjects();
    verify(this.repository, times(1))
      .hasDeliverableToComplete(eq(1L));

    verify(this.repository, times(1))
      .save(this.captureCompletedProjects.capture(), eq(0));
    final Collection<Project> value = this.captureCompletedProjects.getValue();
    assertThat(value).hasSize(1);
  }
}
