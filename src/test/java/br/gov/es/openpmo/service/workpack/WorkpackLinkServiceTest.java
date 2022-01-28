package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.relations.BelongsTo;
import br.gov.es.openpmo.model.relations.IsLinkedTo;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.BelongsToRepository;
import br.gov.es.openpmo.repository.WorkpackLinkRepository;
import br.gov.es.openpmo.service.office.plan.PlanService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@DisplayName("Test Workpack link with Workpack Model rules")
@ExtendWith(MockitoExtension.class)
class WorkpackLinkServiceTest {

  static final long ID_WORKPACK = 1L;
  static final long ID_WORKPACK_MODEL = 2L;
  static final long ID_PLAN = 3L;
  static final Long ID_WORKPACK_PARENT = 4L;

  @Mock
  PlanService planService;
  @Mock
  WorkpackLinkRepository repository;
  @Mock
  WorkpackService workpackService;
  @Mock
  WorkpackModelService workpackModelService;
  @Mock
  BelongsToRepository belongsToRepository;

  @InjectMocks
  WorkpackLinkService service;


  @Mock
  Workpack target;
  @Mock
  Workpack parent;
  @Mock
  WorkpackModel model;
  @Mock
  Plan plan;

  @Test
  @DisplayName("Should link Workpack to WorkpackModel")
  void shouldLinkWorkpackToWorkpackModel() {
    this.givenAllDataIsValidAndNotHasParent();
    this.service.linkWorkpackToWorkpackModel(ID_WORKPACK, ID_WORKPACK_MODEL, ID_PLAN, null);

    verify(this.workpackService, times(1)).findById(ID_WORKPACK);
    verify(this.planService, times(1)).findById(ID_PLAN);
    verify(this.workpackModelService, times(1)).findById(ID_WORKPACK_MODEL);
    verify(this.repository, times(1)).save(isA(IsLinkedTo.class));
  }

  private void givenAllDataIsValidAndNotHasParent() {
    when(this.workpackService.findById(ID_WORKPACK)).thenReturn(this.target);
    when(this.workpackModelService.findById(ID_WORKPACK_MODEL)).thenReturn(this.model);
    when(this.planService.findById(ID_PLAN)).thenReturn(this.plan);
    when(this.target.hasSameModelType(isA(WorkpackModel.class))).thenReturn(true);
  }

  @Test
  @DisplayName("Should make Workpack belongs to a plan")
  void shouldMakeWorkpackBelongsToPlan() {
    this.givenAllDataIsValidAndNotHasParent();
    this.service.linkWorkpackToWorkpackModel(ID_WORKPACK, ID_WORKPACK_MODEL, ID_PLAN, null);
    verify(this.planService, times(1)).findById(ID_PLAN);
    verify(this.belongsToRepository, times(1)).save(isA(BelongsTo.class));
  }

  @Test
  @DisplayName("Should add Workpack parent")
  void shouldAddParent() {
    this.givenAllDataIsValidAndHasParent();
    this.service.linkWorkpackToWorkpackModel(ID_WORKPACK, ID_WORKPACK_MODEL, ID_PLAN, ID_WORKPACK_PARENT);
    verify(this.workpackService, times(2)).findById(anyLong());
    verify(this.workpackService, times(1)).saveDefault(this.target);
    verify(this.parent, times(1)).addChildren(this.target);
  }

  private void givenAllDataIsValidAndHasParent() {
    when(this.workpackService.findById(ID_WORKPACK)).thenReturn(this.target);
    when(this.workpackModelService.findById(ID_WORKPACK_MODEL)).thenReturn(this.model);
    when(this.planService.findById(ID_PLAN)).thenReturn(this.plan);
    when(this.workpackService.findById(ID_WORKPACK_PARENT)).thenReturn(this.parent);
    when(this.target.hasSameModelType(isA(WorkpackModel.class))).thenReturn(true);
  }

  @Test
  @DisplayName("Should throw exception if Workpack not has same type of WorkpackModel")
  void shouldThrowExceptionIfWorkpackNotHasSameTypeOfWorkpackModel() {
    this.givenWorkpackNotHasSameTypeOfWorkpackModel();
    assertThatThrownBy(() -> this.service.linkWorkpackToWorkpackModel(
      ID_WORKPACK,
      ID_WORKPACK_MODEL,
      ID_PLAN,
      null
    ))
      .isInstanceOf(NegocioException.class)
      .hasMessage(ApplicationMessage.WORKPACK_MODEL_TYPE_MISMATCH);

    verify(this.workpackService, times(1)).findById(ID_WORKPACK);
    verify(this.planService, never()).findById(ID_PLAN);
    verify(this.repository, never()).save(isA(IsLinkedTo.class));
  }

  private void givenWorkpackNotHasSameTypeOfWorkpackModel() {
    when(this.workpackService.findById(ID_WORKPACK)).thenReturn(this.target);
    when(this.workpackModelService.findById(ID_WORKPACK_MODEL)).thenReturn(this.model);
    when(this.target.hasSameModelType(isA(WorkpackModel.class))).thenReturn(false);
  }


}
