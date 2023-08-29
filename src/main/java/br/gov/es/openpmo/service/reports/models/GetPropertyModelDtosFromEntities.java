package br.gov.es.openpmo.service.reports.models;

import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GetPropertyModelDtosFromEntities {

  private final GetPropertyModelDtoFromEntity getPropertyModelDtoFromEntity;

  public GetPropertyModelDtosFromEntities(GetPropertyModelDtoFromEntity getPropertyModelDtoFromEntity) {
    this.getPropertyModelDtoFromEntity = getPropertyModelDtoFromEntity;
  }

  public List<? extends PropertyModelDto> execute(Collection<PropertyModel> properties) {
    if (properties == null || properties.isEmpty()) {
      return Collections.emptyList();
    }
    final List<PropertyModelDto> result = new ArrayList<>();
    for (PropertyModel propertyModel : properties) {
      result.add(this.getPropertyModelDtoFromEntity.execute(propertyModel));
    }
    result.sort(Comparator.comparing(PropertyModelDto::getSortIndex));
    return result;
  }

}
