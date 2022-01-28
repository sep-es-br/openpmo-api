package br.gov.es.openpmo.dto.workpackreuse;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.dto.workpackreuse.ReusableWorkpackModelHierarchyDto.of;
import static br.gov.es.openpmo.util.WorkpackHierarchyUtil.fetchWorkpackInHierarchy;
import static java.util.Arrays.asList;

@Tag("unit")
@DisplayName("Test creation of WorkpackModel reusable hierarchy")
@ExtendWith(MockitoExtension.class)
class ReusableWorkpackModelHierarchyDtoTest {

  private static final int CHILD_ID_MULTIPLE_PARENT = 11;
  private static final long TIER_3_MODEL_ID = 10L;
  private static final long TIER_1_MODEL_ID = 1L;
  private static final long TIER_2_MODEL_ID = 4L;
  Set<WorkpackModel> workpackModels;
  private WorkpackModel childOfAnyTier;

  @BeforeEach
  void setUp() {
  }

  @Test
  @DisplayName("Should create simple hierarchy")
  void shouldCreateSimpleHierarchy() {
    final WorkpackModel workpacksModel = tier2();
    final ReusableWorkpackModelHierarchyDto dto = of(workpacksModel);

    assertTrue(dto.getParent().isEmpty(), "First level should not have parent");

    final Long EXPECTED_PARENT_ID = dto.getId();

    dto.getChildren().forEach(child -> {
      assertNotNull(child.getParent(), "Child should has parent");
      assertEquals(1, child.getParent().size(), "Should have 1 parent");
      assertTrue(
        child.getParent().stream().anyMatch(parent -> EXPECTED_PARENT_ID.equals(parent.getId())),
        "Should have child with id of parent"
      );
    });
  }

  private static WorkpackModel tier2() {
    final WorkpackModel tier2Model1 = model(TIER_2_MODEL_ID, "nome 4");
    final WorkpackModel tier2Model2 = model(5, "nome 5", tier2Model1);
    final WorkpackModel tier2Model3 = model(6, "nome 6", tier2Model1);
    final WorkpackModel tier2Model4 = model(7, "nome 7", tier2Model3);
    final WorkpackModel tier2Model6 = model(9, "nome 9", tier2Model4);
    final WorkpackModel tier2Model5 = model(8, "nome 8", tier2Model4);

    return tier2Model1;
  }

  private static WorkpackModel model(final long id, final String name, final WorkpackModel... parent) {
    final WorkpackModel workpackModel = new WorkpackModel();
    workpackModel.setId(id);
    workpackModel.setModelName(name);
    if(parent != null && parent.length > 0) {
      workpackModel.addParent(asList(parent));
    }
    return workpackModel;
  }

  @Test
  @DisplayName("Should create hierarchy with multiple parents")
  void shouldCreateHierarchyWithMultipleParents() {
    final Set<WorkpackModel> workpacks = this.complex();

    final Set<ReusableWorkpackModelHierarchyDto> hierarchy = workpacks.stream()
      .map(ReusableWorkpackModelHierarchyDto::of)
      .collect(Collectors.toSet());

    final ReusableWorkpackModelHierarchyDto tier3 = fetchWorkpackInHierarchy(
      TIER_3_MODEL_ID,
      hierarchy,
      "Should found tier 2 in first layer of hierarchy"
    );

    assertTrue(tier3.getParent().isEmpty(), "Should not have parent");

    final int NUMBER_OF_CHILDREN = 2;
    assertEquals(NUMBER_OF_CHILDREN, tier3.getChildren().size(), "Should have 2 children");

    final ReusableWorkpackModelHierarchyDto target = fetchWorkpackInHierarchy(
      CHILD_ID_MULTIPLE_PARENT,
      tier3.getChildren(),
      "Should found child with multiple parent"
    );

    assertEquals(1, target.getParent().size(), "Should have 2 parent");
    assertTrue(target.getChildren().isEmpty(), "Should not have children");
  }

  private Set<WorkpackModel> complex() {
    final WorkpackModel tier3Model1 = this.tier3();
    final WorkpackModel tier2Model1 = this.complexTier2();
    final WorkpackModel tier1Model1 = tier1();

    return asSet(
      tier1Model1,
      tier2Model1,
      tier3Model1
    );
  }

  private WorkpackModel complexTier2() {
    final WorkpackModel tier2Model1 = model(TIER_2_MODEL_ID, "nome 4");
    final WorkpackModel tier2Model2 = model(5, "nome 5", tier2Model1);
    final WorkpackModel tier2Model3 = model(6, "nome 6", tier2Model1);
    final WorkpackModel tier2Model4 = model(7, "nome 7", tier2Model3);
    final WorkpackModel tier2Model5 = model(8, "nome 8", tier2Model4);

    WorkpackModel deepParent = model(9, "nome 9", tier2Model4);
    this.childOfAnyTier.addParent(deepParent);

    return tier2Model1;
  }

  private static WorkpackModel tier1() {
    final WorkpackModel tier1Model1 = model(TIER_1_MODEL_ID, "nome 1");
    final WorkpackModel tier1Model2 = model(2, "nome 2", tier1Model1);
    final WorkpackModel tier1Model3 = model(3, "nome 3", tier1Model2);

    return tier1Model1;
  }

  private WorkpackModel tier3() {
    final WorkpackModel tier3Model1 = model(TIER_3_MODEL_ID, "nome 10");
    final WorkpackModel tier3Model2 = model(12, "nome 12", tier3Model1);
    this.childOfAnyTier = model(CHILD_ID_MULTIPLE_PARENT, "nome 11", tier3Model1);

    return tier3Model1;
  }

  private static Set<WorkpackModel> asSet(final WorkpackModel... models) {
    return new HashSet<>(asList(models));
  }


}
