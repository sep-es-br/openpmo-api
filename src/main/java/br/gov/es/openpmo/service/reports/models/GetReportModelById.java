package br.gov.es.openpmo.service.reports.models;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.reports.ReportDesign;
import br.gov.es.openpmo.repository.ReportDesignRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetReportModelById {

  private static final Logger log = LoggerFactory.getLogger(GetReportModelById.class);

  private final ReportDesignRepository repository;

  public GetReportModelById(final ReportDesignRepository repository) {
    this.repository = repository;
  }

  public ReportDesign execute(final Long idReportDesign) {
    log.debug("Buscando ReportDesign pelo ID: {}.", idReportDesign);
    final Optional<ReportDesign> maybeReportDesign = this.repository.findByIdWithRelationships(idReportDesign);
    if (maybeReportDesign.isPresent()) {
      log.debug("Retornando PlanModel encontrado. ID {}.", idReportDesign);
      return maybeReportDesign.get();
    }
    log.debug("PlanModel não encontrado com o ID {}.", idReportDesign);
    throw new NegocioException(ApplicationMessage.REPORT_DESIGN_NOT_FOUND);
  }

}
