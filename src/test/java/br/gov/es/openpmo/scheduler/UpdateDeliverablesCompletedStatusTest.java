package br.gov.es.openpmo.scheduler;

import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.repository.StepRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.scheduler.updatestatus.UpdateDeliverablesCompletedStatus;
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
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Tag("unit")
@DisplayName("Test if update status 'completed' of Deliverables")
@ExtendWith(MockitoExtension.class)
class UpdateDeliverablesCompletedStatusTest {

  private static final Deliverable DELIVERABLE = mock(Deliverable.class);
  private UpdateDeliverablesCompletedStatus updateWorkpackCompletedStatus;
  @Mock
  private WorkpackRepository repository;
  @Mock
  private StepRepository stepRepository;
  @Captor
  private ArgumentCaptor<Collection<Deliverable>> captureCompletedDeliverable;


  @BeforeEach
  void setUp() {
    this.updateWorkpackCompletedStatus = new UpdateDeliverablesCompletedStatus(
      this.repository,
      this.stepRepository
    );
  }

  @Test
  @DisplayName("Should find all deliverables stored")
  void test1() {
    doReturn(new HashSet<>(asList(
      mock(Deliverable.class),
      mock(Deliverable.class),
      mock(Deliverable.class),
      mock(Deliverable.class)
    ))).when(this.repository).findAllDeliverables();

    this.updateWorkpackCompletedStatus.update();

    verify(this.repository, times(1)).findAllDeliverables();
  }

  @Test
  @DisplayName("Should not update status of deliverable when remain steps to complete")
  void test2() {
    doReturn(new HashSet<>(Collections.singletonList(
      DELIVERABLE
    ))).when(this.repository).findAllDeliverables();
    doReturn(1L)
      .when(DELIVERABLE)
      .getId();
    doReturn(true).when(this.repository)
      .hasScheduleRelated(1L);
    doReturn(true)
      .when(this.repository)
      .hasActiveBaseline(1L);
    doReturn(true)
      .when(this.stepRepository)
      .hasWorkToCompleteComparingWithActiveBaseline(1L);

    this.updateWorkpackCompletedStatus.update();

    verify(this.repository, times(1))
      .hasScheduleRelated(anyLong());
    verify(this.repository, times(1))
      .findAllDeliverables();
    verify(this.stepRepository, never())
      .hasWorkToCompleteComparingWithMaster(anyLong());
    verify(this.repository, times(1))
      .hasActiveBaseline(1L);
    verify(this.stepRepository, times(1))
      .hasWorkToCompleteComparingWithActiveBaseline(1L);

    verify(this.repository, never())
      .save(anyCollection(), anyInt());
  }

  @Test
  @DisplayName("Should update status of deliverable when all steps are completed")
  void test3() {
    doReturn(new HashSet<>(Collections.singletonList(
      DELIVERABLE
    ))).when(this.repository).findAllDeliverables();
    doReturn(true).when(this.repository)
      .hasScheduleRelated(1L);
    doReturn(1L)
      .when(DELIVERABLE)
      .getId();
    doReturn(true)
      .when(this.repository)
      .hasActiveBaseline(1L);
    doReturn(false)
      .when(this.stepRepository)
      .hasWorkToCompleteComparingWithActiveBaseline(1L);
    doReturn(null)
      .when(this.repository)
      .save(anyList(), eq(0));

    this.updateWorkpackCompletedStatus.update();

    verify(this.repository, times(1))
      .hasScheduleRelated(anyLong());
    verify(this.repository, times(1))
      .findAllDeliverables();
    verify(this.stepRepository, never())
      .hasWorkToCompleteComparingWithMaster(anyLong());
    verify(this.repository, times(1))
      .hasActiveBaseline(1L);
    verify(this.stepRepository, times(1))
      .hasWorkToCompleteComparingWithActiveBaseline(1L);

    verify(this.repository, times(1))
      .save(this.captureCompletedDeliverable.capture(), eq(0));
    final Collection<Deliverable> value = this.captureCompletedDeliverable.getValue();
    assertThat(value).hasSize(1);
  }

  @Test
  @DisplayName("Should skip deliverable when not has schedule related")
  void test4() {

    doReturn(new HashSet<>(Collections.singletonList(
      DELIVERABLE
    ))).when(this.repository).findAllDeliverables();
    doReturn(false).when(this.repository)
      .hasScheduleRelated(1L);

    this.updateWorkpackCompletedStatus.update();

    verify(this.repository, times(1))
      .hasScheduleRelated(anyLong());
    verify(this.repository, times(1))
      .findAllDeliverables();
    verify(this.stepRepository, never())
      .hasWorkToCompleteComparingWithMaster(anyLong());
    verify(this.repository, never())
      .hasActiveBaseline(1L);
    verify(this.stepRepository, never())
      .hasWorkToCompleteComparingWithActiveBaseline(1L);
    verify(this.repository, never())
      .save(anyCollection(), anyInt());
  }


}
