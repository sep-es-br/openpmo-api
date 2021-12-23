package br.gov.es.openpmo.service.ui;

import br.gov.es.openpmo.model.office.plan.Plan;

@FunctionalInterface
public interface BreadcrumbPlanHelper {
  Plan findById(Long idPlan);
}
