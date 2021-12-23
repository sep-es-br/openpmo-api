package br.gov.es.openpmo.util;

import br.gov.es.openpmo.dto.workpackreuse.ReusableWorkpackModelHierarchyDto;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;
import java.util.Optional;

import static java.util.Arrays.asList;

public final class WorkpackHierarchyUtil {

  private WorkpackHierarchyUtil() {
  }

  public static ReusableWorkpackModelHierarchyDto fetchWorkpackInHierarchy(
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

  public static WorkpackModel createModel(final WorkpackModel type, final long id, final String name, final WorkpackModel... parent) {
    type.setId(id);
    type.setModelName(name);
    if(parent != null && parent.length > 0) {
      type.addParent(asList(parent));
    }
    return type;
  }

}
