package br.gov.es.openpmo.service.reports.models;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
public class GetPropertyModelFromDto {

  private static final Logger log = LoggerFactory.getLogger(GetPropertyModelFromDto.class);

  private final ExtractPropertyModel extractPropertyModel;

  public GetPropertyModelFromDto(ExtractPropertyModel extractPropertyModel) {
    this.extractPropertyModel = extractPropertyModel;
  }

  public Set<PropertyModel> execute(Collection<? extends PropertyModelDto> propertyModelDtos) {
    final Set<PropertyModel> propertyModels = new HashSet<>();
    if (propertyModelDtos == null || propertyModelDtos.isEmpty()) {
      log.debug("PropertyModelDtos vazio. Retonando lista vazia.");
      return propertyModels;
    }
    log.debug(
      "Extraindo {} propertyModelDtos em propertyModels.",
      propertyModelDtos.size()
    );
    for (PropertyModelDto propertyModelDto : propertyModelDtos) {
      extractPropertyModel.execute(
        propertyModels,
        propertyModelDto
      );
    }
    log.debug(
      "Retonando {} propertyModels extraidos.",
      propertyModels.size()
    );
    return propertyModels;
  }

}
