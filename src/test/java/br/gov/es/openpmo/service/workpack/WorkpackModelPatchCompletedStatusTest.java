package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelCompletedUpdateRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.models.DeliverableModel;
import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static br.gov.es.openpmo.utils.ApplicationMessage.COMPLETED_STATUS_MUST_BE_NOT_NULL;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_MODEL_INVALID_TYPE;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Tag("unit")
@DisplayName("Test if Deliverable Model property 'showCompletedManagement' was patched")
@ExtendWith(MockitoExtension.class)
class WorkpackModelPatchCompletedStatusTest {

  private static final Long ID_WORKPACK_MODEL = 1L;
  @Mock
  private WorkpackModelCompletedUpdateRequest REQUEST;
  @Mock
  private DeliverableModel DELIVERABLE_MODEL;
  private WorkpackModelPatchCompletedStatus workpackModelPatchCompletedStatus;
  @Mock
  private WorkpackModelRepository repository;

  @BeforeEach
  void setUp() {
    this.workpackModelPatchCompletedStatus = new WorkpackModelPatchCompletedStatus(
      this.repository
    );
  }

  @Test
  @DisplayName("Should not update 'showCompletedManagement' when not found Deliverable Model")
  void test1() {
    doReturn(Optional.empty())
      .when(this.repository)
      .findById(ID_WORKPACK_MODEL);

    assertThatThrownBy(() -> this.workpackModelPatchCompletedStatus.patch(this.REQUEST, ID_WORKPACK_MODEL))
      .isInstanceOf(NegocioException.class)
      .hasMessage(WORKPACK_NOT_FOUND);

    verify(this.REQUEST, never()).getCompleted();
    verify(this.DELIVERABLE_MODEL, never()).setShowCompletedManagement(anyBoolean());
    verify(this.repository, never()).save(any(WorkpackModel.class), eq(1));
  }

  @Test
  @DisplayName("Should update 'showCompletedManagement' status to true when found Deliverable Model")
  void test2() {
    final boolean COMPLETED_VALUE = true;
    doReturn(Optional.of(this.DELIVERABLE_MODEL))
      .when(this.repository)
      .findById(ID_WORKPACK_MODEL);
    doReturn(this.DELIVERABLE_MODEL)
      .when(this.repository)
      .save(eq(this.DELIVERABLE_MODEL), eq(1));
    doReturn(COMPLETED_VALUE)
      .when(this.REQUEST)
      .getCompleted();
    doNothing()
      .when(this.DELIVERABLE_MODEL)
      .setShowCompletedManagement(COMPLETED_VALUE);

    this.workpackModelPatchCompletedStatus.patch(this.REQUEST, ID_WORKPACK_MODEL);

    verify(this.REQUEST, times(1)).getCompleted();
    verify(this.DELIVERABLE_MODEL, times(1)).setShowCompletedManagement(COMPLETED_VALUE);
    verify(this.repository, times(1)).findById(1L);
    verify(this.repository, times(1)).save(eq(this.DELIVERABLE_MODEL), eq(1));

  }

  @Test
  @DisplayName("Should update 'showCompletedManagement' status to false when found Deliverable Model")
  void test3() {
    final boolean COMPLETED_VALUE = false;

    doReturn(Optional.of(this.DELIVERABLE_MODEL))
      .when(this.repository)
      .findById(ID_WORKPACK_MODEL);
    doReturn(this.DELIVERABLE_MODEL)
      .when(this.repository)
      .save(eq(this.DELIVERABLE_MODEL), eq(1));
    doReturn(COMPLETED_VALUE)
      .when(this.REQUEST)
      .getCompleted();
    doNothing()
      .when(this.DELIVERABLE_MODEL)
      .setShowCompletedManagement(COMPLETED_VALUE);

    this.workpackModelPatchCompletedStatus.patch(this.REQUEST, ID_WORKPACK_MODEL);

    verify(this.REQUEST, times(1)).getCompleted();
    verify(this.DELIVERABLE_MODEL, times(1)).setShowCompletedManagement(COMPLETED_VALUE);
    verify(this.repository, times(1)).findById(1L);
    verify(this.repository, times(1)).save(eq(this.DELIVERABLE_MODEL), eq(1));
  }

  @Test
  @DisplayName("Should throw exception when request is null")
  void test4() {
    assertThatThrownBy(() -> this.workpackModelPatchCompletedStatus.patch(null, ID_WORKPACK_MODEL))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(COMPLETED_STATUS_MUST_BE_NOT_NULL);

    verify(this.REQUEST, never()).getCompleted();
    verify(this.DELIVERABLE_MODEL, never()).setShowCompletedManagement(anyBoolean());
    verify(this.repository, never()).findById(1L);
    verify(this.repository, never()).save(any(WorkpackModel.class), anyInt());
  }

  @Test
  @DisplayName("Should not update 'showCompletedManagement' when Workpack Model is not Deliverable Model")
  void test5() {

    final ProjectModel PROJECT_MODEL = mock(ProjectModel.class);

    doReturn(Optional.of(PROJECT_MODEL))
      .when(this.repository)
      .findById(ID_WORKPACK_MODEL);

    assertThatThrownBy(() -> this.workpackModelPatchCompletedStatus.patch(this.REQUEST, ID_WORKPACK_MODEL))
      .isInstanceOf(NegocioException.class)
      .hasMessage(WORKPACK_MODEL_INVALID_TYPE);

    verify(this.REQUEST, never()).getCompleted();
    verify(this.DELIVERABLE_MODEL, never()).setShowCompletedManagement(anyBoolean());
    verify(this.repository, times(1)).findById(1L);
    verify(this.repository, never()).save(eq(this.DELIVERABLE_MODEL), eq(1));
  }


}
