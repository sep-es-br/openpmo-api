package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.models.DeliverableModel;
import br.gov.es.openpmo.model.workpacks.models.MilestoneModel;
import br.gov.es.openpmo.model.workpacks.models.PortfolioModel;
import br.gov.es.openpmo.model.workpacks.models.ProgramModel;
import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import static br.gov.es.openpmo.util.WorkpackHierarchyUtil.createModel;
import static br.gov.es.openpmo.utils.WorkpackModelInstanceType.TYPE_NAME_MODEL_PROGRAM;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;


@Tag("unit")
@ExtendWith(MockitoExtension.class)
class ParentWorkpackTypeVerifierTest {

  @InjectMocks
  private ParentWorkpackTypeVerifier verifier;
  @Mock
  private WorkpackModelRepository repository;

  private WorkpackModel root;
  private WorkpackModel deepTarget;
  private WorkpackModel targetWithoutParentMatch;

  @BeforeEach
  void setUp() {
    /*
      - 10 (PortfolioModel 4)
        - 1 (DeliverableModel 1)
          - 2 (ProjectModel 1)
            - 5 (MilestoneModel 2)
              - 6 (PortfolioModel 1)
          - 8 (ProgramModel 2)
            - 7 (PortfolioModel 2)
              - 3 (ProgramModel 2)
            - 9 (PortfolioModel 3)
        - 4 (ProjectModel 1)
          - 8 (ProgramModel 2)
            - 7 (PortfolioModel 2)
              - 3 (ProgramModel 2)
            - 9 (PortfolioModel 3)
     */
    final WorkpackModel model1 = createModel(new DeliverableModel(), 1, "DeliverableModel 1");
    final WorkpackModel model4 = createModel(new ProjectModel(), 4, "ProjectModel 1");
    final WorkpackModel model5 = createModel(new MilestoneModel(), 5, "MilestoneModel 2");
    final WorkpackModel model7 = createModel(new PortfolioModel(), 7, "PortfolioModel 2");
    final WorkpackModel model9 = createModel(new PortfolioModel(), 9, "PortfolioModel 3");
    final WorkpackModel model2 = createModel(new ProjectModel(), 2, "ProjectModel 1");
    final WorkpackModel model8 = createModel(new ProgramModel(), 8, "ProgramModel 1");

    this.targetWithoutParentMatch = createModel(new PortfolioModel(), 6, "PortfolioModel 1");
    this.deepTarget = createModel(new ProgramModel(), 3, "ProgramModel 2");
    this.root = createModel(new PortfolioModel(), 10, "PortfolioModel 4");
    this.root.addChildren(model4, model1);

    model2.addChildren(model5);
    model5.addChildren(this.targetWithoutParentMatch);
    model7.addChildren(this.deepTarget);
    model4.addChildren(model8);
    model1.addChildren(model2, model8);
    model8.addChildren(model7, model9);
  }

  @Test
  void shouldReturnTrueWhenHasParentTypeOfProject() {
    when(this.repository.findByIdWithParents(anyLong())).thenReturn(Optional.of(this.deepTarget));
    final boolean hasParentOfTypeProject = this.verifier.verify(3L, TYPE_NAME_MODEL_PROGRAM::isTypeOf);
    assertTrue(hasParentOfTypeProject);
  }

  @Test
  void shouldReturnFalseWhenHasNoParent() {
    when(this.repository.findByIdWithParents(anyLong())).thenReturn(Optional.of(this.root));
    final boolean hasParentOfTypeProject = this.verifier.verify(10L, TYPE_NAME_MODEL_PROGRAM::isTypeOf);
    assertFalse(hasParentOfTypeProject);
  }

  @Test
  void shouldReturnFalseWhenParentTypeNotMatch() {
    when(this.repository.findByIdWithParents(anyLong())).thenReturn(Optional.of(this.targetWithoutParentMatch));
    final boolean hasParentOfTypeProject = this.verifier.verify(6L, TYPE_NAME_MODEL_PROGRAM::isTypeOf);
    assertFalse(hasParentOfTypeProject);
  }

  @Test
  void shouldThrowExceptionWhenWorkpackIdIsNull() {
    assertThatThrownBy(() -> this.verifier.verify(null, TYPE_NAME_MODEL_PROGRAM::isTypeOf))
      .hasMessage(ApplicationMessage.WORKPACKMODEL_NOT_FOUND)
      .isInstanceOf(NegocioException.class);
  }
}
