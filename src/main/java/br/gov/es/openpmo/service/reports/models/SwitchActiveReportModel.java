package br.gov.es.openpmo.service.reports.models;

import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.reports.ReportDesign;
import br.gov.es.openpmo.repository.ReportDesignRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SwitchActiveReportModel {

  private final ReportDesignRepository reportDesignRepository;

  public SwitchActiveReportModel(
    final ReportDesignRepository reportDesignRepository
  ) {
    this.reportDesignRepository = reportDesignRepository;
  }

  @Transactional
  public void execute(final Long idReportModel) {
    ReportDesign reportDesign = this.getReportDesign(idReportModel);
    if (reportDesign.getCompiledSource() == null || reportDesign.getCompiledSource().isEmpty()) {
      throw new RegistroNaoEncontradoException(ApplicationMessage.FILE_COMPILED_NOT_FOUND);
    }
    Boolean newActive = BooleanUtils.isFalse(reportDesign.getActive()) ? Boolean.TRUE : Boolean.FALSE;
    reportDesign.setActive(newActive);
    this.reportDesignRepository.save(reportDesign, 0);
  }

  private ReportDesign getReportDesign(final Long idReportModel) {
    return this.reportDesignRepository.findByIdWithRelationships(idReportModel)
      .orElseThrow(() -> new RegistroNaoEncontradoException(ApplicationMessage.REPORT_DESIGN_NOT_FOUND));
  }

}
