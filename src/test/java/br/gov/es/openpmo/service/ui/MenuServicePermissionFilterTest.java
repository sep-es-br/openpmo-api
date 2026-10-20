package br.gov.es.openpmo.service.ui;

import br.gov.es.openpmo.dto.menu.WorkpackMenuResultDto;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MenuServicePermissionFilterTest {

  private MenuService menuService;

  @Before
  public void setUp() {
    this.menuService = new MenuService(null, null, null, null, null, null, null);
  }

  @Test
  public void shouldKeepOnlyAccessibleItemItsDescendantsAndItsAncestorPath() {
    final WorkpackMenuResultDto accessibleChild = item(3L, item(4L));
    final WorkpackMenuResultDto inaccessibleSibling = item(5L);
    final WorkpackMenuResultDto root = item(1L, item(2L, accessibleChild), inaccessibleSibling);

    final Set<WorkpackMenuResultDto> result = this.menuService.filterMenuToPermittedBranches(
      Arrays.asList(root),
      new LinkedHashSet<>(Arrays.asList(3L)),
      false
    );

    final WorkpackMenuResultDto filteredRoot = result.iterator().next();
    final WorkpackMenuResultDto filteredAncestor = filteredRoot.getChildren().iterator().next();
    final WorkpackMenuResultDto filteredAccessibleItem = filteredAncestor.getChildren().iterator().next();

    assertEquals(Long.valueOf(1L), filteredRoot.getId());
    assertEquals(Long.valueOf(2L), filteredAncestor.getId());
    assertEquals(Long.valueOf(3L), filteredAccessibleItem.getId());
    assertEquals(Long.valueOf(4L), filteredAccessibleItem.getChildren().iterator().next().getId());
    assertEquals(1, filteredRoot.getChildren().size());
    assertEquals(1, filteredAncestor.getChildren().size());
  }

  @Test
  public void shouldIncludeWholeSubtreeWhenTheParentItemIsAccessible() {
    final WorkpackMenuResultDto accessibleRoot = item(1L, item(2L, item(3L)), item(4L));

    final Set<WorkpackMenuResultDto> result = this.menuService.filterMenuToPermittedBranches(
      Arrays.asList(accessibleRoot),
      new LinkedHashSet<>(Arrays.asList(1L)),
      false
    );

    assertEquals(2, result.iterator().next().getChildren().size());
  }

  private static WorkpackMenuResultDto item(final Long id, final WorkpackMenuResultDto... children) {
    final WorkpackMenuResultDto item = new WorkpackMenuResultDto();
    item.setId(id);
    item.setChildren(new LinkedHashSet<>(Arrays.asList(children)));
    return item;
  }
}
