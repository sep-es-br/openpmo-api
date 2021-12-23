package br.gov.es.openpmo.service.ui;

import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

@FunctionalInterface
public interface BreadcrumbWorkpackModelHelper {
  WorkpackModel findById(Long idWorkpackModel);
}
