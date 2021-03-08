package br.gov.es.openpmo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.PropertyModel;
import br.gov.es.openpmo.repository.PropertyModelRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;

@Service
public class PropertyModelService {

    private final PropertyModelRepository propertyModelRepository;

    @Autowired
    public PropertyModelService(PropertyModelRepository propertyModelRepository) {
        this.propertyModelRepository = propertyModelRepository;
    }

    public List<PropertyModel> findAllByIdWorkpackModel(Long idWorkpackModel) {
        return new ArrayList<>(
            propertyModelRepository.findAllByIdWorkpackModel(idWorkpackModel));
    }

    public PropertyModel findById(Long id) {
        return propertyModelRepository.findById(id).orElseThrow(() -> new NegocioException(ApplicationMessage.PROPERTYMODEL_NOT_FOUND));
    }

    public void delete(Set<PropertyModel> propertyModels) {
        propertyModelRepository.deleteAll(propertyModels);
    }

    public boolean canDeleteProperty(Long id) {
        return propertyModelRepository.countPropertyByIdPropertyModel(id) == 0;
    }
}
