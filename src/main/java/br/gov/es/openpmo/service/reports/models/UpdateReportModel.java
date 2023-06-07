package br.gov.es.openpmo.service.reports.models;

import br.gov.es.openpmo.dto.reports.models.UpdateReportModelFileItem;
import br.gov.es.openpmo.dto.reports.models.UpdateReportModelRequest;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.reports.ReportDesign;
import br.gov.es.openpmo.model.reports.ReportFormat;
import br.gov.es.openpmo.repository.ReportDesignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class UpdateReportModel {


  private static final Logger log = LoggerFactory.getLogger(UpdateReportModel.class);

  private final ReportDesignRepository reportDesignRepository;

  private final CreateReportModelTemplateSource createReportModelTemplateSource;

  private final GetPropertyModelFromDto getPropertyModelFromDto;

  private final GetReportModelById getReportModelById;

  private final UpdatePropertyModel updatePropertyModel;

  public UpdateReportModel(
    final ReportDesignRepository reportDesignRepository,
    final CreateReportModelTemplateSource createReportModelTemplateSource,
    final GetPropertyModelFromDto getPropertyModelFromDto,
    final GetReportModelById getReportModelById,
    final UpdatePropertyModel updatePropertyModel
  ) {
    this.reportDesignRepository = reportDesignRepository;
    this.createReportModelTemplateSource = createReportModelTemplateSource;
    this.getPropertyModelFromDto = getPropertyModelFromDto;
    this.getReportModelById = getReportModelById;
    this.updatePropertyModel = updatePropertyModel;
  }

  public void execute(final UpdateReportModelRequest request) {
    log.info("Iniciando atualização do ReportDesign.");
    final ReportDesign reportDesign = this.getReportDesignById(request.getId());

    this.updateReportDesign(reportDesign, request);
    this.saveReportDesign(reportDesign);

    log.info("Finalizando atualização do ReportDesign.");
  }

  private void updateTemplateSource(final ReportDesign reportDesign, final UpdateReportModelRequest request) {

    if (CollectionUtils.isEmpty(request.getFiles())) {
      log.debug("Não foi informado nenhum novo arquivo de template para o reportDesignId={}", reportDesign.getId());
      return;
    }

    final List<UpdateReportModelFileItem> newFilesRequest = request.getFiles().stream()
      .filter(file -> Objects.isNull(file.getId()))
      .collect(Collectors.toList());

    log.debug(
      "De {} arquivos de template {} foram adicionados para o reportDesignId={}, iniciando criação dos novos arquivos...",
      request.getFiles().size(),
      newFilesRequest.size(),
      reportDesign.getId()
    );

    final Set<File> createdFiles = this.createReportModelTemplateSource.execute(newFilesRequest);

    log.debug("{} arquivos de template criados com sucesso para o reportDesignId={}", createdFiles.size(), reportDesign.getId());

    reportDesign.addAllTemplates(createdFiles);
  }

  private ReportDesign getReportDesignById(final Long idReportDesign) {
    return this.getReportModelById.execute(idReportDesign);
  }

  private void saveReportDesign(final ReportDesign reportDesign) {
    log.debug("Persistindo as mudanças no banco.");
    this.reportDesignRepository.save(reportDesign);
    log.debug("Mudanças persistidas com sucesso.");
  }

  private void updateReportDesign(
    final ReportDesign reportDesign,
    final UpdateReportModelRequest request
  ) {
    log.debug("Atualizando atributos do reportDesign.");
    reportDesign.setName(request.getName());
    reportDesign.setFullName(request.getFullName());
    final ReportFormat preferredOutputFormat = getPreferredOutputFormat(request);
    reportDesign.setPreferredOutputFormat(preferredOutputFormat);
    this.updatePropertyModels(reportDesign, request);
    this.updateTemplateSource(reportDesign, request);
    log.debug("ReportDesign atualizado com sucesso.");
  }

  private void updatePropertyModels(
    final ReportDesign reportDesign,
    final UpdateReportModelRequest request
  ) {
    log.debug("Atualizando propriedades do reportDesign.");
    final Set<PropertyModel> propertiesModel = this.getPropertyModelFromDto.execute(request.getParamModels());
    this.updatePropertyModel.execute(
      reportDesign,
      propertiesModel
    );
    log.debug("Propriedades atualizadas com sucesso.");
  }

  private static ReportFormat getPreferredOutputFormat(final UpdateReportModelRequest request) {
    final ReportFormat preferredOutputFormat = request.getPreferredOutputFormat();
    if (preferredOutputFormat != null) {
      return preferredOutputFormat;
    }
    return ReportFormat.PDF;
  }

}
