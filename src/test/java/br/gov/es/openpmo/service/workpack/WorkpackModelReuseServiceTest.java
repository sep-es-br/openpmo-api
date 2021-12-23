package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpackreuse.ReusableWorkpackModelHierarchyDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static br.gov.es.openpmo.util.WorkpackHierarchyUtil.createModel;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class WorkpackModelReuseServiceTest {

  private static final long ID_PLAN_MODEL = 1L;
  private static final long TIER_3_MODEL_ID = 10L;
  private static final long TIER_1_MODEL_ID = 1L;
  private static final long TIER_2_MODEL_ID = 4L;
  @Mock
  WorkpackModelRepository repository;
  @InjectMocks
  WorkpackModelReuseService service;
  Set<WorkpackModel> workpackModels;
  WorkpackModel deepParent;
  WorkpackModel childOfAnyTier;
  WorkpackModel targetParent;

  @BeforeEach
  void setUp() {
    final WorkpackModel tier3Model1 = this.tier3();
    final WorkpackModel tier2Model1 = this.tier2();
    this.targetParent = this.tier1();

    this.workpackModels = this.asSet(
      this.targetParent,
      tier2Model1,
      tier3Model1
    );
  }

  private WorkpackModel tier1() {
    final WorkpackModel tier1Model1 = createModel(new WorkpackModel(), TIER_1_MODEL_ID, "nome 1");
    final WorkpackModel tier1Model2 = createModel(new WorkpackModel(), 2, "nome 2", tier1Model1);
    final WorkpackModel tier1Model3 = createModel(new WorkpackModel(), 3, "nome 3", tier1Model2);
    return tier1Model1;
  }

  private WorkpackModel tier2() {
    final WorkpackModel tier2Model1 = createModel(new WorkpackModel(), TIER_2_MODEL_ID, "nome 4");
    final WorkpackModel tier2Model2 = createModel(new WorkpackModel(), 5, "nome 5", tier2Model1);
    final WorkpackModel tier2Model3 = createModel(new WorkpackModel(), 6, "nome 6", tier2Model1);
    final WorkpackModel tier2Model4 = createModel(new WorkpackModel(), 7, "nome 7", tier2Model3);
    final WorkpackModel tier2Model5 = createModel(new WorkpackModel(), 8, "nome 8", tier2Model4);
    this.deepParent = createModel(new WorkpackModel(), 9, "nome 9", tier2Model4);
    this.childOfAnyTier.addParent(this.deepParent);
    return tier2Model1;
  }

  private WorkpackModel tier3() {
    final WorkpackModel tier3Model1 = createModel(new WorkpackModel(), TIER_3_MODEL_ID, "nome 10");
    final WorkpackModel tier3Model2 = createModel(new WorkpackModel(), 12, "nome 12", tier3Model1);

    this.childOfAnyTier = createModel(new WorkpackModel(), 11, "nome 11", tier3Model1);

    return tier3Model1;
  }

  @NotNull private HashSet<WorkpackModel> asSet(final WorkpackModel... models) {
    return new HashSet<>(asList(models));
  }


  @Nested
  class ReusableHierarchy {

    @Test
    void shouldNotReuseChildrenOfTier3() {
      when(WorkpackModelReuseServiceTest.this.repository.findAllByIdPlanModelWithChildren(anyLong()))
        .thenReturn(WorkpackModelReuseServiceTest.this.workpackModels);
      final List<ReusableWorkpackModelHierarchyDto> hierarchy = WorkpackModelReuseServiceTest.this.service.findWorkpackModelReusable(
        TIER_3_MODEL_ID,
        ID_PLAN_MODEL
      );
      final Optional<ReusableWorkpackModelHierarchyDto> tier3 = hierarchy.stream()
        .filter(dto -> dto.getId().equals(TIER_3_MODEL_ID))
        .findFirst();

      if(!tier3.isPresent()) {
        fail("Should have tier 3 in hierarchy");
      }
      assertFalse(tier3.get().getReusable(), "Workpack of Tier 3 should not be reusable");

      tier3.get().getChildren().forEach(children -> assertFalse(
        children.getReusable(),
        "Children of tier 3 should not be reusable"
      ));
    }

    @Test
    void shouldNotReuseAscendingItemsInHierarchy() {
      when(WorkpackModelReuseServiceTest.this.repository.findAllByIdPlanModelWithChildren(anyLong()))
        .thenReturn(WorkpackModelReuseServiceTest.this.workpackModels);

      final long TARGET_WORKPACK = 7L;
      final long FIRST_LEVEL_WORKPACK = 4L;

      final List<ReusableWorkpackModelHierarchyDto> hierarchy = WorkpackModelReuseServiceTest.this.service.findWorkpackModelReusable(
        TARGET_WORKPACK,
        ID_PLAN_MODEL
      );

      final ReusableWorkpackModelHierarchyDto tier2 = this.fetchWorkpackInHierarchy(
        FIRST_LEVEL_WORKPACK,
        hierarchy,
        "Should have tier 2 in hierarchy"
      );
      assertFalse(tier2.getReusable(), "Workpack of Tier 2 should not be reusable");
      final long PARENT_WORKPACK = 6L;
      tier2.getChildren().forEach(model -> {
        if(model.getId() != PARENT_WORKPACK) {
          assertTrue(model.getReusable(), "Workpack not member of parent hierarchy should be reusable");
        }
      });

      final ReusableWorkpackModelHierarchyDto childTier2 = this.fetchWorkpackInHierarchy(
        PARENT_WORKPACK,
        tier2.getChildren(),
        "Should have parent of target workpack"
      );
      assertFalse(childTier2.getReusable(), "Workpack parent should not be reusable");

      final ReusableWorkpackModelHierarchyDto target = this.fetchWorkpackInHierarchy(
        TARGET_WORKPACK,
        childTier2.getChildren(),
        "Should have target workpack"
      );
      assertFalse(target.getReusable(), "Should not reuse target Workpack");
      target.getChildren().forEach(model -> assertFalse(model.getReusable(), "Should not reuse children of target Workpack"));
    }

    private ReusableWorkpackModelHierarchyDto fetchWorkpackInHierarchy(
      final long workpackId,
      final Collection<ReusableWorkpackModelHierarchyDto> hierarchy,
      final String failMessage
    ) {
      final Optional<ReusableWorkpackModelHierarchyDto> maybeModel = hierarchy.stream()
        .filter(model -> model.getId() == workpackId)
        .findFirst();

      if(!maybeModel.isPresent()) {
        fail(failMessage);
      }
      return maybeModel.get();
    }

    @Test
    void shouldReuseAllItemsDescendingOfChildren() {
      when(WorkpackModelReuseServiceTest.this.repository.findAllByIdPlanModelWithChildren(anyLong()))
        .thenReturn(WorkpackModelReuseServiceTest.this.workpackModels);

      final List<ReusableWorkpackModelHierarchyDto> hierarchy = WorkpackModelReuseServiceTest.this.service.findWorkpackModelReusable(
        4L,
        ID_PLAN_MODEL
      );

      final ReusableWorkpackModelHierarchyDto tier2 = this.fetchWorkpackInHierarchy(
        4L,
        hierarchy,
        "Should have tier 2 in hierarchy"
      );

      tier2.getChildren().forEach(child -> {
        assertFalse(
          child.getReusable(),
          "Should not reuse children of target Workpack"
        );

        child.getChildren().forEach(grandChild -> assertTrue(
          grandChild.getReusable()
        ));

      });


    }

    @Test
    void shouldNotReuseChildrenOfMultipleTiers() {
      // TODO: ajustar criação do ReusableWorkpackModelHierarchyDto para suportar múltiplos parents
      when(WorkpackModelReuseServiceTest.this.repository.findAllByIdPlanModelWithChildren(anyLong()))
        .thenReturn(WorkpackModelReuseServiceTest.this.workpackModels);

      final long CHILD_OF_MULTIPLE_PARENT = 11L;

      final List<ReusableWorkpackModelHierarchyDto> hierarchy = WorkpackModelReuseServiceTest.this.service.findWorkpackModelReusable(
        CHILD_OF_MULTIPLE_PARENT,
        ID_PLAN_MODEL
      );

    }

    void ensureNotReuseOfParent(final ReusableWorkpackModelHierarchyDto dto) {
      assertEquals(1, dto.getParent().size());
      dto.getParent().forEach(parent -> assertFalse(parent.getReusable()));
    }
  }

  @Nested
  class Reuse {

    @Test
    void shouldReuseWorkpack() {
      when(WorkpackModelReuseServiceTest.this.repository.findById(anyLong()))
        .thenReturn(
          Optional.of(WorkpackModelReuseServiceTest.this.targetParent),
          Optional.of(WorkpackModelReuseServiceTest.this.childOfAnyTier)
        );
      final long PARENT_ID = 1L;
      final long CHILDREN_ID = 11L;
      WorkpackModelReuseServiceTest.this.service.reuse(PARENT_ID, CHILDREN_ID);

      verify(WorkpackModelReuseServiceTest.this.repository, times(2)).findById(anyLong());
      verify(WorkpackModelReuseServiceTest.this.repository, times(1)).save(isA(WorkpackModel.class));
    }

    @Test
    void shouldThrowExceptionWhenParentWorkpackIdIsNull() {
      final Long PARENT_ID = null;
      final Long CHILDREN_ID = 11L;

      assertThatThrownBy(() -> WorkpackModelReuseServiceTest.this.service.reuse(PARENT_ID, CHILDREN_ID))
        .hasMessage(ApplicationMessage.ID_WORKPACK_NOT_NULL)
        .isInstanceOf(NegocioException.class);

      verify(WorkpackModelReuseServiceTest.this.repository, never()).findById(anyLong());
      verify(WorkpackModelReuseServiceTest.this.repository, never()).save(isA(WorkpackModel.class));
    }

    @Test
    void shouldThrowExceptionWhenChildrenWorkpackIdIsNull() {
      final Long PARENT_ID = 1L;
      final Long CHILDREN_ID = null;

      assertThatThrownBy(() -> WorkpackModelReuseServiceTest.this.service.reuse(PARENT_ID, CHILDREN_ID))
        .hasMessage(ApplicationMessage.ID_WORKPACK_NOT_NULL)
        .isInstanceOf(NegocioException.class);

      verify(WorkpackModelReuseServiceTest.this.repository, never()).findById(anyLong());
      verify(WorkpackModelReuseServiceTest.this.repository, never()).save(isA(WorkpackModel.class));
    }
  }

}
