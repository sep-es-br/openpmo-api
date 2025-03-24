package br.gov.es.openpmo.service.properties;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.repository.PropertyRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PropertyService {

  private final PropertyRepository propertyRepository;

  @Autowired
  public PropertyService(final PropertyRepository propertyRepository) {
    this.propertyRepository = propertyRepository;
  }

  public Property findById(final Long id) {
    return this.propertyRepository.findById(id)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PROPERTY_MODEL_NOT_FOUND));
  }

  public void delete(final Set<Property> properties) {
    this.propertyRepository.deleteAll(properties);
  }

}
