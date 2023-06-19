package br.gov.es.openpmo.service.reports.models;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OnlyOneReportModelTemplateSourceValidator {

  private static final Logger log = LoggerFactory.getLogger(OnlyOneReportModelTemplateSourceValidator.class);

  public void execute(final Collection<? extends MainFile> reportTemplateFiles) {
    log.debug("Validando arquivo principal do template de relatório");
    final List<MainFile> mainFiles = reportTemplateFiles.stream()
      .filter(MainFile::getMain)
      .collect(Collectors.toList());
    log.debug("Foram informados {} arquivo(s) como principal", mainFiles.size());
    if (mainFiles.size() != 1) {
      log.error("O relatório pode ter apenas 1 arquivo principal");
      throw new NegocioException(ApplicationMessage.REPORT_DESIGN_MAIN_FILE_TEMPLATE_QUANTITY_DIFFERENT_THAN_ONE);
    }
    log.debug("Validação terminada com sucesso");
  }

}
