package br.gov.es.openpmo.service.properties;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.properties.models.GroupModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.repository.PropertyModelRepository;
import br.gov.es.openpmo.utils.PropertyModelType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.gov.es.openpmo.utils.ApplicationMessage.PROPERTY_MODEL_NOT_FOUND;

@Service
public class PropertyModelService {

  private final PropertyModelRepository propertyModelRepository;

  @Autowired
  public PropertyModelService(final PropertyModelRepository propertyModelRepository) {
    this.propertyModelRepository = propertyModelRepository;
  }

  public PropertyModel findById(final Long id) {
    return this.propertyModelRepository.findById(id)
      .orElseThrow(() -> new NegocioException(PROPERTY_MODEL_NOT_FOUND));
  }

  public void delete(final Iterable<PropertyModel> propertiesModel) {
    this.propertyModelRepository.deleteAll(propertiesModel);
  }

  public boolean canDeleteProperty(final Long id) {
    final PropertyModel propertyModel = this.propertyModelRepository.findById(id)
            .orElseThrow(() -> new NegocioException(PROPERTY_MODEL_NOT_FOUND));

    if(propertyModel.getTypeName().equals(PropertyModelType.TYPE_NAME_MODEL_GROUP)){
      final GroupModel groupProperty = (GroupModel) propertyModel;
      if(groupProperty.getGroupedProperties() == null) {
        return true;
      }
    }
    return this.propertyModelRepository.countPropertyByIdPropertyModel(id) == 0;
  }

}
