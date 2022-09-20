package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class ParentWorkpackTypeVerifier {

  private final WorkpackModelRepository repository;

  @Autowired
  public ParentWorkpackTypeVerifier(final WorkpackModelRepository repository) {
    this.repository = repository;
  }

  private static boolean hasNoParents(final WorkpackModel model) {
    return !model.hasParent();
  }

  public boolean verify(
    final Long id,
    final Predicate<WorkpackModel> typeVerifier
  ) {
    final WorkpackModel model = this.findByIdWithParents(id);
    if(hasNoParents(model)) return typeVerifier.test(model);
    return this.hasParentOfTypeProject(model, typeVerifier);
  }

  private WorkpackModel findByIdWithParents(final Long id) {
    return this.repository.findByIdWithParents(id)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACKMODEL_NOT_FOUND));
  }

  private boolean hasParentOfTypeProject(
    final WorkpackModel model,
    final Predicate<WorkpackModel> typeVerifier
  ) {
    for(final WorkpackModel parent : model.getParent()) {
      if(typeVerifier.test(parent)) return true;
      if(hasNoParents(parent)) return false;
      if(this.hasParentOfTypeProject(parent, typeVerifier)) return true;
    }
    return false;
  }

}
