package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.service.properties.PropertyModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_MODEL_INVALID_STATE_DELETE_RELATIONSHIP_ERROR;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class WorkpackModelDeleteServiceServiceTest {

  private static final Class<WorkpackModel> WORKPACK_MODEL_CLASS = WorkpackModel.class;
  @InjectMocks
  WorkpackModelDeleteService service;
  @Mock WorkpackModelRepository workpackModelRepository;
  @Mock PropertyModelService propertyModelService;


  private WorkpackModel target;
  private WorkpackModel parent;

  @BeforeEach
  void setUp() {
    this.target = new WorkpackModel();
    this.target.setId(1L);
    this.parent = new WorkpackModel();
    this.parent.setId(2L);
  }

  @Test
  void shouldDeleteWorkpackWithoutParent() {
    this.service.delete(this.target, null);
    verify(this.workpackModelRepository, times(1)).deleteCascadeAllNodesRelated(anyLong());
    verify(this.workpackModelRepository, never()).deleteRelationshipBetween(this.target.getId(), this.parent.getId());
  }

  @Test
  void shouldDeleteWorkpackWithOneParent() {
    this.givenWorkpackHasOneParent();
    this.service.delete(this.target, this.parent);
    verify(this.workpackModelRepository, times(1)).deleteCascadeAllNodesRelated(anyLong());
    verify(this.workpackModelRepository, never()).deleteRelationshipBetween(this.target.getId(), this.parent.getId());
  }

  private void givenWorkpackHasOneParent() {
    this.target.addParent(this.parent);
  }

  @Test
  void shouldDeleteWorkpackWithMoreThanOneParent() {
    this.givenWorkpackHasMoreThanOneParent();
    this.service.delete(this.target, this.parent);
    verify(this.workpackModelRepository, never()).delete(isA(WORKPACK_MODEL_CLASS));
    verify(this.workpackModelRepository, times(1)).deleteRelationshipBetween(this.target.getId(), this.parent.getId());
  }

  private void givenWorkpackHasMoreThanOneParent() {
    final WorkpackModel parent2 = new WorkpackModel();
    parent2.setId(3L);
    final WorkpackModel parent3 = new WorkpackModel();
    parent3.setId(4L);
    this.target.addParent(asList(this.parent, parent2, parent3));
  }

  @Test
  void shouldThrowExceptionIfWorkpackHasParentAndParentInstanceIsNull() {
    this.givenWorkpackHasOneParent();
    assertThatThrownBy(() -> this.service.delete(this.target, null))
      .hasMessage(WORKPACK_MODEL_INVALID_STATE_DELETE_RELATIONSHIP_ERROR)
      .isInstanceOf(NegocioException.class);
    verify(this.workpackModelRepository, never()).deleteCascadeAllNodesRelated(anyLong());
    verify(this.workpackModelRepository, never()).deleteRelationshipBetween(this.target.getId(), this.parent.getId());
  }

  @Test
  void shouldThrowExceptionIfWorkpackNotHasParentAndParentInstanceIsNonNull() {
    this.givenWorkpackHasOneParent();
    assertThatThrownBy(() -> this.service.delete(this.target, null))
      .hasMessage(WORKPACK_MODEL_INVALID_STATE_DELETE_RELATIONSHIP_ERROR)
      .isInstanceOf(NegocioException.class);
    verify(this.workpackModelRepository, never()).deleteCascadeAllNodesRelated(anyLong());
    verify(this.workpackModelRepository, never()).deleteRelationshipBetween(this.target.getId(), this.parent.getId());
  }
}
