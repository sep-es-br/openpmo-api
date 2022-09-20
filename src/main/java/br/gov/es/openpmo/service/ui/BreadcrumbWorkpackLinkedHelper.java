package br.gov.es.openpmo.service.ui;

import br.gov.es.openpmo.model.relations.IsLinkedTo;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

import java.util.Optional;

public interface BreadcrumbWorkpackLinkedHelper {

  WorkpackModel findWorkpackModelLinkedByWorkpackAndPlan(
    final Long idWorkpack,
    final Long idPlan
  );

  Optional<IsLinkedTo> findWorkpackParentLinked(
    Long idWorkpack,
    Long idPlan
  );

}
