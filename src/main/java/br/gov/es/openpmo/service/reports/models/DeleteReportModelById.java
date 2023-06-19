package br.gov.es.openpmo.service.reports.models;

import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.reports.ReportDesign;
import br.gov.es.openpmo.repository.PropertyModelRepository;
import br.gov.es.openpmo.repository.ReportDesignRepository;
import br.gov.es.openpmo.service.reports.files.DeleteReportModelFileById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DeleteReportModelById {

  private static final Logger log = LoggerFactory.getLogger(DeleteReportModelById.class);

  private final ReportDesignRepository reportDesignRepository;

  private final PropertyModelRepository propertyModelRepository;

  private final GetReportModelById getReportModelById;

  private final DeleteReportModelFileById deleteReportModelFileById;

  public DeleteReportModelById(
    final ReportDesignRepository reportDesignRepository,
    final PropertyModelRepository propertyModelRepository,
    final GetReportModelById getReportModelById,
    final DeleteReportModelFileById deleteReportModelFileById
  ) {
    this.reportDesignRepository = reportDesignRepository;
    this.propertyModelRepository = propertyModelRepository;
    this.getReportModelById = getReportModelById;
    this.deleteReportModelFileById = deleteReportModelFileById;
  }

  public void execute(final Long idReportDesign) {
    log.info("Iniciando exclusão do report model pelo ID: {}.", idReportDesign);
    final ReportDesign reportDesign = this.getReportDesignById(idReportDesign);

    this.deleteAllFiles(reportDesign);

    this.deleteAllPropertyModels(reportDesign);

    this.reportDesignRepository.deleteById(idReportDesign);

    log.info("Finalizando exclusão do report model.");
  }

  private void deleteAllFiles(final ReportDesign reportDesign) {
    this.deleteTemplateSource(reportDesign.getId(), reportDesign.getTemplateSource());
    this.deleteCompiledSource(reportDesign.getId(), reportDesign.getCompiledSource());
  }

  private void deleteCompiledSource(final Long idReportDesign, final Set<? extends File> compiledSource) {
    if (CollectionUtils.isEmpty(compiledSource)) {
      log.debug("Não foi encontrado nenhum arquivo compilado do relatório {}", idReportDesign);
      return;
    }
    log.debug("Removendo {} arquivos compilados do relatório {}", compiledSource.size(), idReportDesign);
    compiledSource
      .forEach(source -> this.deleteReportModelFileById.execute(source.getId()));
  }

  private void deleteTemplateSource(final Long idReportDesign, final Set<? extends File> templateSource) {
    if (CollectionUtils.isEmpty(templateSource)) {
      log.debug("Não foi encontrado nenhum arquivo de template do relatório {}", idReportDesign);
      return;
    }
    log.debug("Removendo {} arquivos template do relatório {}", templateSource.size(), idReportDesign);
    templateSource
      .forEach(source -> this.deleteReportModelFileById.execute(source.getId()));
  }

  private void deleteAllPropertyModels(final ReportDesign reportDesignById) {
    final Set<PropertyModel> propertiesModel = reportDesignById.getPropertiesModel();
    if (CollectionUtils.isEmpty(propertiesModel)) {
      log.debug("O report model {} não possui PropertyModel relacionado", reportDesignById.getId());
      return;
    }
    log.debug("Excluindo {} propertiesModel e properties.", propertiesModel.size());
    this.propertyModelRepository.deletePropertyModelAndInstances(
      propertiesModel.stream()
        .map(PropertyModel::getId)
        .collect(Collectors.toList())
    );
    log.debug("PropertyModels e properties excluídos.");
  }

  private ReportDesign getReportDesignById(final Long idReportDesign) {
    return this.getReportModelById.execute(idReportDesign);
  }

}
