package br.gov.es.openpmo.service.reports;

import br.gov.es.openpmo.dto.reports.ActiveReportItem;
import br.gov.es.openpmo.model.reports.ReportDesign;
import br.gov.es.openpmo.repository.ReportDesignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GetActiveReports {

  private static final Logger log = LoggerFactory.getLogger(GetActiveReports.class);
  private final ReportDesignRepository repository;

  public GetActiveReports(final ReportDesignRepository repository) {this.repository = repository;}


  public List<ActiveReportItem> execute(final Long idPlan) {
    log.info("Consultando relatórios ativos relacionados ao planId={}", idPlan);
    final Set<ReportDesign> reports = this.repository.findAllReportsActiveByPlan(idPlan);
    log.info("Foram encontrados {} relatório(s) ativos relacionados ao planId={}", reports.size(), idPlan);
    return reports.stream()
      .map(ActiveReportItem::of)
      .collect(Collectors.toList());
  }

}
