package br.gov.es.openpmo.service.reports.models;

import br.gov.es.openpmo.dto.reports.models.GetReportModelByIdResponse;
import br.gov.es.openpmo.dto.reports.models.GetReportModelFileItem;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.actors.File;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.reports.ReportDesign;
import br.gov.es.openpmo.repository.ReportDesignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GetReportModelDetailById {

  private static final Logger log = LoggerFactory.getLogger(GetReportModelDetailById.class);

  private final ReportDesignRepository reportDesignRepository;

  private final GetReportModelById getReportModelById;

  private final GetPropertyModelDtoFromEntity getPropertyModelDtoFromEntity;

  public GetReportModelDetailById(
    final ReportDesignRepository reportDesignRepository,
    final GetReportModelById getReportModelById,
    final GetPropertyModelDtoFromEntity getPropertyModelDtoFromEntity
  ) {
    this.reportDesignRepository = reportDesignRepository;
    this.getReportModelById = getReportModelById;
    this.getPropertyModelDtoFromEntity = getPropertyModelDtoFromEntity;
  }

  public GetReportModelByIdResponse execute(final Long idReportDesign) {
    log.info(
      "Iniciando busca do report model pelo ID: {}.",
      idReportDesign
    );
    final ReportDesign reportDesign = this.getReportDesignById(idReportDesign);
    final GetReportModelByIdResponse response = this.getResponse(reportDesign);
    log.info("Finalizando busca do report model.");
    return response;
  }

  private ReportDesign getReportDesignById(final Long idReportDesign) {
    return this.getReportModelById.execute(idReportDesign);
  }

  private GetReportModelByIdResponse getResponse(final ReportDesign reportDesign) {
    log.debug("Mapeando reportDesign. ID {}.", reportDesign.getId());
    final List<PropertyModelDto> properties = this.getPropertyModelDtos(reportDesign);

    final List<GetReportModelFileItem> files = getFiles(reportDesign);

    final GetReportModelByIdResponse response = GetReportModelByIdResponse.of(
      reportDesign,
      properties,
      files
    );
    log.debug("Mapeado com sucesso. Retornando DTO.");
    return response;
  }

  private List<PropertyModelDto> getPropertyModelDtos(final ReportDesign reportDesign) {
    final List<PropertyModelDto> dtos = new ArrayList<>();
    final Set<PropertyModel> propertiesModel = reportDesign.getPropertiesModel();
    if (propertiesModel == null || propertiesModel.isEmpty()) {
      log.debug("PropertyModel vazio. Retornando lista vazia.");
      return dtos;
    }
    log.debug(
      "Mapeando {} propertiesModel para DTO.",
      propertiesModel.size()
    );
    for (final PropertyModel propertyModel : propertiesModel) {
      final PropertyModelDto dto = this.getPropertyModelDtoFromEntity.execute(propertyModel);
      dtos.add(dto);
    }
    log.debug("Mapeado com sucesso. Retornando DTO.");
    return dtos.stream()
      .sorted(Comparator.comparing(PropertyModelDto::getSortIndex))
      .collect(Collectors.toList());
  }

  private static List<GetReportModelFileItem> getFiles(final ReportDesign reportDesign) {
    final Set<File> templateSource = Optional.ofNullable(reportDesign.getTemplateSource()).orElseGet(HashSet::new);
    log.debug("Mapeando {} templateSource para DTO", templateSource.size());
    return templateSource.stream()
      .map(GetReportModelFileItem::of)
      .sorted(Comparator.comparing(GetReportModelFileItem::getMain).reversed())
      .collect(Collectors.toList());
  }

}
