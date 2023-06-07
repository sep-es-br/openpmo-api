package br.gov.es.openpmo.service.reports;

import br.gov.es.openpmo.dto.reports.models.GetReportModelFileItem;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.reports.ReportDesign;
import br.gov.es.openpmo.repository.ReportDesignRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class GetCompiledReport {

  private final ReportDesignRepository repository;

  public GetCompiledReport(final ReportDesignRepository repository) {
    this.repository = repository;
  }

  public List<GetReportModelFileItem> execute(final Long idReportDesign) {
    List<GetReportModelFileItem> files = new ArrayList<>();
    final ReportDesign reportDesign = this.findByIdWithRelationships(idReportDesign);
    Set<File> compiledSource = reportDesign.getCompiledSource();
    if (compiledSource != null) compiledSource.forEach(file -> files.add(GetReportModelFileItem.of(file)));
    return files;
  }

  private ReportDesign findByIdWithRelationships(final Long idReportDesign) {
    return this.repository.findByIdWithRelationships(idReportDesign)
      .orElseThrow(() -> new RegistroNaoEncontradoException(ApplicationMessage.REPORT_DESIGN_NOT_FOUND));
  }

}
