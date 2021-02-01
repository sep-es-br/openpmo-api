package br.gov.es.openpmo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.Property;
import br.gov.es.openpmo.repository.PropertyRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    @Autowired
    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public List<Property> findAllByIdWorkpack(Long idWorkpack) {
        return new ArrayList<>(propertyRepository.findAllByIdWorkpack(idWorkpack));
    }

    public Property findById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new NegocioException(ApplicationMessage.PROPERTYMODEL_NOT_FOUND));
    }

    public void delete(Set<Property> propertys) {
        propertyRepository.deleteAll(propertys);
    }
}
