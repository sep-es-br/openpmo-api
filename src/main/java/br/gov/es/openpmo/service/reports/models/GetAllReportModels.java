package br.gov.es.openpmo.service.reports.models;

import br.gov.es.openpmo.dto.reports.models.GetAllReportModelsResponse;
import br.gov.es.openpmo.model.reports.ReportDesign;
import br.gov.es.openpmo.repository.ReportDesignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Component
public class GetAllReportModels {

  private static final Logger log = LoggerFactory.getLogger(GetAllReportModels.class);

  private final ReportDesignRepository reportDesignRepository;

  public GetAllReportModels(ReportDesignRepository reportDesignRepository) {
    this.reportDesignRepository = reportDesignRepository;
  }

  public List<GetAllReportModelsResponse> execute(Long idPlanModel) {
    log.info(
      "Buscando ReportDesigns pelo ID do PlanModel {}.",
      idPlanModel
    );
    final List<ReportDesign> reportDesigns = getReportDesigns(idPlanModel);
    final List<GetAllReportModelsResponse> reportModelsResponses = getReportModelsResponses(reportDesigns);
    log.info(
      "Retornando {} registros com sucesso.",
      reportModelsResponses.size()
    );
    return reportModelsResponses;
  }

  private List<ReportDesign> getReportDesigns(Long idPlanModel) {
    log.debug(
      "Buscando ReportDesigns pelo ID do PlanModel {}.",
      idPlanModel
    );
    final List<ReportDesign> reportDesigns = reportDesignRepository.findAllByPlanModelId(idPlanModel);
    log.debug(
      "Foram encontrados {} reportDesigns.",
      reportDesigns.size()
    );
    return reportDesigns;
  }

  private List<GetAllReportModelsResponse> getReportModelsResponses(Collection<ReportDesign> reportDesigns) {
    log.debug(
      "Mapeando {} reportDesigns para DTO.",
      reportDesigns.size()
    );
    List<GetAllReportModelsResponse> reportModelsResponses = new ArrayList<>();
    for (ReportDesign reportDesign : reportDesigns) {
      GetAllReportModelsResponse response = getResponse(reportDesign);
      reportModelsResponses.add(response);
    }
    log.debug(
      "Retornando {} DTOs.",
      reportModelsResponses.size()
    );
    reportModelsResponses.sort(Comparator.comparing(GetAllReportModelsResponse::getId));
    return reportModelsResponses;
  }

  private GetAllReportModelsResponse getResponse(ReportDesign reportDesign) {
    log.debug(
      "Mapeando reportDesign. ID {}.",
      reportDesign.getId()
    );
    final GetAllReportModelsResponse reportModelsResponse = new GetAllReportModelsResponse();
    reportModelsResponse.setId(reportDesign.getId());
    reportModelsResponse.setIdPlanModel(reportDesign.getIdPlanModel());
    reportModelsResponse.setName(reportDesign.getName());
    reportModelsResponse.setFullName(reportDesign.getFullName());
    reportModelsResponse.setActive(reportDesign.getActive());
    reportModelsResponse.setPreferredOutputFormat(reportDesign.getPreferredOutputFormat());
    log.debug("Mapeado com sucesso. Retornando DTO.");
    return reportModelsResponse;
  }

}
