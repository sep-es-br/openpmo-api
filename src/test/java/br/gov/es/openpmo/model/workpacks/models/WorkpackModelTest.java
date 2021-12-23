package br.gov.es.openpmo.model.workpacks.models;

import br.gov.es.openpmo.util.WorkpackHierarchyUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class WorkpackModelTest {

  WorkpackModel target;
  WorkpackModel child;
  WorkpackModel parent;

  @BeforeEach
  void setUp() {
    this.target = WorkpackHierarchyUtil.createModel(new WorkpackModel(), 1, "Target");
    this.child = WorkpackHierarchyUtil.createModel(new WorkpackModel(), 2, "Child");
    this.parent = WorkpackHierarchyUtil.createModel(new WorkpackModel(), 3, "Parent");
  }

  @Nested
  class AddParent {
    @Test
    void shouldAddParent() {
      WorkpackModelTest.this.target.addParent((WorkpackModelTest.this.parent));
      assertTrue(WorkpackModelTest.this.target.getParent().contains(WorkpackModelTest.this.parent));
    }

    @Test
    void shouldParentAddChild() {
      WorkpackModelTest.this.target.addParent((WorkpackModelTest.this.parent));
      assertTrue(WorkpackModelTest.this.parent.getChildren().contains(WorkpackModelTest.this.target));
    }

    @Test
    void shouldContainsParent() {
      WorkpackModelTest.this.target.addParent((WorkpackModelTest.this.parent));
      assertTrue(WorkpackModelTest.this.target.containsParent(WorkpackModelTest.this.parent));
    }

    @Test
    void shouldHasParent() {
      WorkpackModelTest.this.target.addParent((WorkpackModelTest.this.parent));
      assertTrue(WorkpackModelTest.this.target.hasParent());
    }
  }

  @Nested
  class AddChild {
    @Test
    void shouldAddChildren() {
      WorkpackModelTest.this.target.addChildren((WorkpackModelTest.this.child));
      assertTrue(WorkpackModelTest.this.child.getParent().contains(WorkpackModelTest.this.target));
    }

    @Test
    void shouldChildrenAddParent() {
      WorkpackModelTest.this.target.addChildren(WorkpackModelTest.this.child);
      assertTrue(WorkpackModelTest.this.child.getParent().contains(WorkpackModelTest.this.target));
    }

    @Test
    void shouldContainsChildren() {
      WorkpackModelTest.this.target.addChildren(WorkpackModelTest.this.child);
      assertTrue(WorkpackModelTest.this.target.containsChild(WorkpackModelTest.this.child));
    }
  }
}
