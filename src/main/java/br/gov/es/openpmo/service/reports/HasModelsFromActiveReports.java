package br.gov.es.openpmo.service.reports;

import java.util.Optional;

import org.springframework.stereotype.Component;

import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.repository.ReportDesignRepository;

@Component
public class HasModelsFromActiveReports {

  private final ReportDesignRepository repository;

  public HasModelsFromActiveReports(final ReportDesignRepository repository) {this.repository = repository;}

  public Boolean execute(final Long idPlan) {    
    final Optional<PlanModel> planModelOptional = this.repository.hasModelFromReportsActiveAndPlan(idPlan);
    return planModelOptional.isPresent();
  }

}
