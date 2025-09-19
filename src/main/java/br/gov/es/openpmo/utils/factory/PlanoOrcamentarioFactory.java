/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.utils.factory;

import br.gov.es.openpmo.dto.budget.PlanoOrcamentarioDto;
import br.gov.es.openpmo.model.budget.PlanoOrcamentario;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 *
 * @author gean.carneiro
 */
@Component
@RequestScope
public class PlanoOrcamentarioFactory extends BaseFactory<PlanoOrcamentario, PlanoOrcamentarioDto>{
    
    @Autowired
    private UnidadeOrcamentariaFactory unidadeOrcamentariaFactory;
    
    @Autowired
    private CostAccountFactory costAccountFactory;

    public PlanoOrcamentarioFactory() {
        super(PlanoOrcamentario.class, PlanoOrcamentarioDto.class);
    }
    
    @Override
    protected PlanoOrcamentario createModel(PlanoOrcamentarioDto dto) {
        
        PlanoOrcamentario model = new PlanoOrcamentario();
                
        model.setId(dto.getId());
        model.setCode(Integer.valueOf(dto.getCode()));
        model.setName(dto.getName());
        model.setFullName(dto.getFullName());
        
        model.setUnidadeOrcamentaria(unidadeOrcamentariaFactory.fromDto(dto.getUnidadeOrcamentaria()));
        model.setCostAccount(costAccountFactory.fromDto(dto.getCostAccount()));

        return model;
    }

    @Override
    protected Long getModelId(PlanoOrcamentario model) {
        return model.getId();
    }

    @Override
    protected PlanoOrcamentarioDto createDto(PlanoOrcamentario model) {
        
        PlanoOrcamentarioDto dto = new PlanoOrcamentarioDto();
        dto.setId(model.getId());
        dto.setCode(Optional.ofNullable(model.getCode()).map(code -> String.format("%06d", code)).orElse(null));
        dto.setName(model.getName());
        dto.setFullName(model.getFullName());
        
        dto.setUnidadeOrcamentaria(unidadeOrcamentariaFactory.fromModel(model.getUnidadeOrcamentaria()));
        dto.setCostAccount(costAccountFactory.fromModel(model.getCostAccount()));
        
        return dto;        
    }

    @Override
    protected Long getDtoId(PlanoOrcamentarioDto dto) {
        return dto.getId();
    }
    
    
}
