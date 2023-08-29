package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.service.properties.PropertyModelService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;

import static br.gov.es.openpmo.utils.ApplicationMessage.PROPERTY_MODEL_DELETE_RELATIONSHIP_ERROR;

@Component
public class DeletePropertyModel {

  private final PropertyModelService propertyModelService;

  public DeletePropertyModel(PropertyModelService propertyModelService) {
    this.propertyModelService = propertyModelService;
  }

  @Transactional
  public void execute(final Long idPropertyModel) {
    final PropertyModel propertyModel = this.propertyModelService.findById(idPropertyModel);
    if (!this.canDeletePropertyModel(idPropertyModel)) {
      throw new NegocioException(PROPERTY_MODEL_DELETE_RELATIONSHIP_ERROR);
    }
    final Collection<PropertyModel> properties = new HashSet<>();
    properties.add(propertyModel);
    this.propertyModelService.delete(properties);
  }

  public boolean canDeletePropertyModel(final Long idPropertyModel) {
    return this.propertyModelService.canDeleteProperty(idPropertyModel);
  }

}