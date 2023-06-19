package br.gov.es.openpmo.service.reports.models;

import br.gov.es.openpmo.dto.reports.models.CreateReportModelFileItem;
import br.gov.es.openpmo.dto.reports.models.CreateReportModelRequest;
import br.gov.es.openpmo.dto.reports.models.CreateReportModelResponse;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.reports.ReportDesign;
import br.gov.es.openpmo.model.reports.ReportFormat;
import br.gov.es.openpmo.repository.PlanModelRepository;
import br.gov.es.openpmo.repository.ReportDesignRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class CreateReportModel {

  private static final Logger log = LoggerFactory.getLogger(CreateReportModel.class);

  private final ReportDesignRepository reportDesignRepository;

  private final PlanModelRepository planModelRepository;

  private final GetPropertyModelFromDto getPropertyModelFromDto;

  private final OnlyOneReportModelTemplateSourceValidator onlyOneReportModelTemplateSourceValidator;

  private final CreateReportModelTemplateSource createReportModelTemplateSource;

  public CreateReportModel(
    final ReportDesignRepository reportDesignRepository,
    final PlanModelRepository planModelRepository,
    final GetPropertyModelFromDto getPropertyModelFromDto,
    final OnlyOneReportModelTemplateSourceValidator onlyOneReportModelTemplateSourceValidator,
    final CreateReportModelTemplateSource createReportModelTemplateSource
  ) {
    this.reportDesignRepository = reportDesignRepository;
    this.planModelRepository = planModelRepository;
    this.getPropertyModelFromDto = getPropertyModelFromDto;
    this.onlyOneReportModelTemplateSourceValidator = onlyOneReportModelTemplateSourceValidator;
    this.createReportModelTemplateSource = createReportModelTemplateSource;
  }

  private static ReportFormat getPreferredOutputFormat(final CreateReportModelRequest request) {
    final ReportFormat preferredOutputFormat = request.getPreferredOutputFormat();
    if (preferredOutputFormat != null) {
      log.debug("Selecionando o formato {} como preferido para o relatório", preferredOutputFormat);
      return preferredOutputFormat;
    }
    log.debug("Nenhum formato preferido informado, selecionando o formato PDF");
    return ReportFormat.PDF;
  }

  public CreateReportModelResponse execute(final CreateReportModelRequest request) {
    log.info("Iniciando criação de um novo report design.");
    final ReportDesign reportDesign = this.getReportDesign(request);
    final Long reportDesignId = this.saveReportDesign(reportDesign).getId();
    log.info("Finalizando criação do report design.");
    return new CreateReportModelResponse(reportDesignId);
  }

  private ReportDesign saveReportDesign(final ReportDesign reportDesign) {
    log.debug("Persistindo as mudanças no banco.");
    final ReportDesign persistedReportDesign = this.reportDesignRepository.save(reportDesign);
    log.info(
      "Report design criado com sucesso. ID {}.",
      reportDesign.getId()
    );
    return persistedReportDesign;
  }

  private ReportDesign getReportDesign(final CreateReportModelRequest request) {
    log.debug("Criando a entidade ReportDesign com os atributos recebidos.");
    final ReportDesign reportDesign = new ReportDesign();
    reportDesign.setId(null);
    reportDesign.setName(request.getName());
    reportDesign.setFullName(request.getFullName());
    reportDesign.setActive(false); // TODO: Para Active = On, é necessária a presença de um arquivo fonte compilado com sucesso
    final ReportFormat preferredOutputFormat = getPreferredOutputFormat(request);
    reportDesign.setPreferredOutputFormat(preferredOutputFormat);
    final PlanModel planModel = this.getPlanModel(request);
    reportDesign.setPlanModel(planModel);
    final Set<PropertyModel> propertiesModel = this.getPropertyModelFromDto.execute(request.getParamModels());
    reportDesign.setPropertiesModel(propertiesModel);
    final List<CreateReportModelFileItem> requestFiles = request.getFiles();
    if (requestFiles != null && !requestFiles.isEmpty()) {
      final Set<File> fileTemplate = this.getFileTemplate(requestFiles);
      reportDesign.setTemplateSource(fileTemplate);
    }
    log.debug("Retornando entidade com os atributos preenchidos.");
    return reportDesign;
  }

  private Set<File> getFileTemplate(List<CreateReportModelFileItem> requestFiles) {
    log.debug("Criando {} novos arquivos de template", requestFiles.size());
    final Set<File> createdFiles = this.createReportModelTemplateSource.execute(
      requestFiles,
      this.onlyOneReportModelTemplateSourceValidator::execute
    );
    log.debug("Arquivos de template criados com sucesso");
    return createdFiles;
  }

  private PlanModel getPlanModel(final CreateReportModelRequest request) {
    final Long idPlanModel = request.getIdPlanModel();
    log.debug("Buscando PlanModel com o ID {}.", idPlanModel);
    final Optional<PlanModel> maybePlanModel = this.planModelRepository.findById(idPlanModel);
    if (maybePlanModel.isPresent()) {
      log.debug("Retornando PlanModel encontrado. ID {}.", idPlanModel);
      return maybePlanModel.get();
    }
    log.debug("PlanModel não encontrado com o ID {}.", idPlanModel);
    throw new NegocioException(ApplicationMessage.PLAN_MODEL_NOT_FOUND);
  }

}
