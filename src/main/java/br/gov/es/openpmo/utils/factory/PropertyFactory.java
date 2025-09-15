/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.utils.factory;

import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.utils.PropertyInstanceType;
import br.gov.es.openpmo.utils.PropertyModelType;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 *
 * @author gean.carneiro
 */
@Component
@RequestScope
public class PropertyFactory extends BaseFactory<Property, PropertyDto>{
    
    public PropertyFactory() {
        super(Property.class, PropertyDto.class);
    }

    @Override
    protected Property createModel(PropertyDto dto) {
        return null;
    }

    @Override
    protected Long getModelId(Property model) {
        return model.getId();
    }

    @Override
    protected PropertyDto createDto(Property model) {
        return PropertyInstanceType.createFrom(model);
    }

    @Override
    protected Long getDtoId(PropertyDto dto) {
        return dto.getId();
    }

    
}
