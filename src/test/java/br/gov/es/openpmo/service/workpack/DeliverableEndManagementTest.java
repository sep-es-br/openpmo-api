package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpack.EndDeliverableManagementRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_IS_NOT_DELIVERABLE_INVALID_STATE_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Tag("unit")
@DisplayName("Test if Deliverable managed was ended")
@ExtendWith(MockitoExtension.class)
class DeliverableEndManagementTest {

  static final Long ID_DELIVERABLE = 1L;
  static final Long ID_PROJECT = 1L;
  DeliverableEndManagement deliverableEndManagement;
  @Mock
  Deliverable DELIVERABLE;
  @Mock
  WorkpackRepository repository;
  @Mock
  Project PROJECT;
  @Mock
  EndDeliverableManagementRequest REQUEST;

  @BeforeEach
  void setUp() {
    this.deliverableEndManagement = new DeliverableEndManagement(
      this.repository
    );
  }

  @Test
  @DisplayName("Should throw exception when not found Deliverable")
  void test1() {

    doReturn(Optional.empty())
      .when(this.repository)
      .findById(ID_DELIVERABLE);

    assertThatThrownBy(() -> this.deliverableEndManagement.execute(ID_DELIVERABLE, this.REQUEST))
      .isInstanceOf(NegocioException.class)
      .hasMessage(WORKPACK_NOT_FOUND);

    verify(this.repository, times(1)).findById(ID_DELIVERABLE);
    verify(this.repository, never()).save(any(Deliverable.class), eq(1));
  }

  @Test
  @DisplayName("Should not end management of non Deliverable")
  void test2() {

    final Project NON_DELIVERABLE = mock(Project.class);

    doReturn(Optional.of(NON_DELIVERABLE))
      .when(this.repository)
      .findById(ID_DELIVERABLE);

    assertThatThrownBy(() -> this.deliverableEndManagement.execute(ID_DELIVERABLE, this.REQUEST))
      .isInstanceOf(NegocioException.class)
      .hasMessage(WORKPACK_IS_NOT_DELIVERABLE_INVALID_STATE_ERROR);

    verify(this.repository, times(1)).findById(ID_DELIVERABLE);
    verify(this.repository, never()).save(any(Deliverable.class), eq(1));
  }


  @Nested
  @DisplayName("Test if project parent must be ended when Deliverable management end")
  class ProjectEndManagementTest {
    @Test
    @DisplayName("Should not end management of Project when remain Deliverables to manage")
    void test1() {
      final LocalDate aManagementEndDate = LocalDate.now();
      doReturn(ID_DELIVERABLE)
        .when(DeliverableEndManagementTest.this.DELIVERABLE)
        .getId();
      doReturn(Optional.of(DeliverableEndManagementTest.this.DELIVERABLE))
        .when(DeliverableEndManagementTest.this.repository)
        .findById(ID_DELIVERABLE);
      doReturn(aManagementEndDate)
        .when(DeliverableEndManagementTest.this.REQUEST)
        .getEndManagementDate();
      doNothing()
        .when(DeliverableEndManagementTest.this.DELIVERABLE)
        .setEndManagementDate(aManagementEndDate);
      doReturn(DeliverableEndManagementTest.this.DELIVERABLE)
        .when(DeliverableEndManagementTest.this.repository)
        .save(DeliverableEndManagementTest.this.DELIVERABLE, 1);
      doReturn(ID_PROJECT)
        .when(DeliverableEndManagementTest.this.PROJECT)
        .getId();
      doReturn(Optional.of(DeliverableEndManagementTest.this.PROJECT))
        .when(DeliverableEndManagementTest.this.repository)
        .findProjectParentOf(ID_DELIVERABLE);
      doReturn(true)
        .when(DeliverableEndManagementTest.this.repository)
        .hasRemainDeliveriesToManage(ID_PROJECT);

      DeliverableEndManagementTest.this.deliverableEndManagement.execute(ID_DELIVERABLE, DeliverableEndManagementTest.this.REQUEST);

      verify(DeliverableEndManagementTest.this.repository, times(1)).findById(ID_DELIVERABLE);
      verify(DeliverableEndManagementTest.this.repository, times(1)).save(DeliverableEndManagementTest.this.DELIVERABLE, 1);
      verify(DeliverableEndManagementTest.this.REQUEST, times(1)).getEndManagementDate();
      verify(DeliverableEndManagementTest.this.DELIVERABLE, times(1)).setEndManagementDate(aManagementEndDate);
    }

    @Test
    @DisplayName("Should end management of Project when all Deliveries has ended")
    void test2() {
      final LocalDate aManagementEndDate = LocalDate.now();

      doReturn(Optional.of(DeliverableEndManagementTest.this.DELIVERABLE))
        .when(DeliverableEndManagementTest.this.repository)
        .findById(ID_DELIVERABLE);
      doReturn(aManagementEndDate)
        .when(DeliverableEndManagementTest.this.REQUEST)
        .getEndManagementDate();
      doNothing()
        .when(DeliverableEndManagementTest.this.DELIVERABLE)
        .setEndManagementDate(aManagementEndDate);
      doReturn(DeliverableEndManagementTest.this.DELIVERABLE)
        .when(DeliverableEndManagementTest.this.repository)
        .save(DeliverableEndManagementTest.this.DELIVERABLE, 1);
      doReturn(ID_DELIVERABLE)
        .when(DeliverableEndManagementTest.this.DELIVERABLE)
        .getId();
      doReturn(ID_PROJECT)
        .when(DeliverableEndManagementTest.this.PROJECT)
        .getId();
      doReturn(Optional.of(DeliverableEndManagementTest.this.PROJECT))
        .when(DeliverableEndManagementTest.this.repository)
        .findProjectParentOf(ID_DELIVERABLE);
      doReturn(false)
        .when(DeliverableEndManagementTest.this.repository)
        .hasRemainDeliveriesToManage(ID_PROJECT);
      doNothing()
        .when(DeliverableEndManagementTest.this.PROJECT)
        .setEndManagementDate(any(LocalDate.class));

      DeliverableEndManagementTest.this.deliverableEndManagement.execute(
        ID_DELIVERABLE,
        DeliverableEndManagementTest.this.REQUEST
      );

      verify(DeliverableEndManagementTest.this.repository, times(1)).findById(ID_DELIVERABLE);
      verify(DeliverableEndManagementTest.this.repository, times(1)).save(DeliverableEndManagementTest.this.DELIVERABLE, 1);
      verify(DeliverableEndManagementTest.this.REQUEST, times(1)).getEndManagementDate();
      verify(DeliverableEndManagementTest.this.DELIVERABLE, times(1)).setEndManagementDate(aManagementEndDate);
      verify(DeliverableEndManagementTest.this.repository, times(1)).findProjectParentOf(ID_DELIVERABLE);
      verify(DeliverableEndManagementTest.this.PROJECT, times(1)).setEndManagementDate(any(LocalDate.class));
      verify(DeliverableEndManagementTest.this.repository, times(1)).save(DeliverableEndManagementTest.this.PROJECT, 1);
    }

    @Test
    @DisplayName("Should throw exception when not found Project related to Deliverable")
    void test3() {
      final LocalDate aManagementEndDate = LocalDate.now();

      doReturn(Optional.of(DeliverableEndManagementTest.this.DELIVERABLE))
        .when(DeliverableEndManagementTest.this.repository)
        .findById(ID_DELIVERABLE);
      doReturn(aManagementEndDate)
        .when(DeliverableEndManagementTest.this.REQUEST)
        .getEndManagementDate();
      doNothing()
        .when(DeliverableEndManagementTest.this.DELIVERABLE)
        .setEndManagementDate(aManagementEndDate);
      doReturn(DeliverableEndManagementTest.this.DELIVERABLE)
        .when(DeliverableEndManagementTest.this.repository)
        .save(DeliverableEndManagementTest.this.DELIVERABLE, 1);
      doReturn(ID_DELIVERABLE)
        .when(DeliverableEndManagementTest.this.DELIVERABLE)
        .getId();
      doReturn(Optional.empty())
        .when(DeliverableEndManagementTest.this.repository)
        .findProjectParentOf(ID_DELIVERABLE);


      assertThatThrownBy(() -> DeliverableEndManagementTest.this.deliverableEndManagement.execute(
        ID_DELIVERABLE,
        DeliverableEndManagementTest.this.REQUEST
      ))
        .isInstanceOf(NegocioException.class)
        .hasMessage(WORKPACK_NOT_FOUND);

      verify(DeliverableEndManagementTest.this.repository, times(1)).findById(ID_DELIVERABLE);
      verify(DeliverableEndManagementTest.this.repository, times(1)).save(DeliverableEndManagementTest.this.DELIVERABLE, 1);
      verify(DeliverableEndManagementTest.this.REQUEST, times(1)).getEndManagementDate();
      verify(DeliverableEndManagementTest.this.DELIVERABLE, times(1)).setEndManagementDate(aManagementEndDate);
      verify(DeliverableEndManagementTest.this.repository, times(1)).findProjectParentOf(ID_DELIVERABLE);
      verify(DeliverableEndManagementTest.this.PROJECT, never()).setEndManagementDate(any(LocalDate.class));
      verify(DeliverableEndManagementTest.this.repository, never()).save(DeliverableEndManagementTest.this.PROJECT, 1);
    }


  }


}
