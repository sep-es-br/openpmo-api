/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.utils.factory;

import br.gov.es.openpmo.dto.budget.UnidadeOrcamentariaDto;
import br.gov.es.openpmo.model.budget.UnidadeOrcamentaria;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 *
 * @author gean.carneiro
 */
@Component
@RequestScope
public class UnidadeOrcamentariaFactory extends BaseFactory<UnidadeOrcamentaria, UnidadeOrcamentariaDto>{

    @Autowired
    private PlanoOrcamentarioFactory planoOrcamentarioFactory;
    
    @Autowired
    private CostAccountFactory costAccountFactory;
    
    public UnidadeOrcamentariaFactory() {
        super(UnidadeOrcamentaria.class, UnidadeOrcamentariaDto.class);
    }
        
    @Override
    protected UnidadeOrcamentaria createModel(UnidadeOrcamentariaDto dto) {
        
        UnidadeOrcamentaria model = new UnidadeOrcamentaria();
                
        model.setId(dto.getId());
        model.setCode(Optional.ofNullable(dto.getCode()).map(Integer::valueOf).orElse(null));
        model.setName(dto.getName());
        model.setFullName(dto.getFullName());
        
        model.setPlanoOrcamentario(planoOrcamentarioFactory.fromDto(dto.getPlanoOrcamentario()));
        model.setCostAccount(costAccountFactory.fromDto(dto.getCostAccount()));
        
        return model;
    }

    @Override
    protected Long getModelId(UnidadeOrcamentaria model) {
       return model.getId();
    }

    @Override
    protected UnidadeOrcamentariaDto createDto(UnidadeOrcamentaria model) {
        
        
        
        UnidadeOrcamentariaDto dto = new UnidadeOrcamentariaDto();
        dto.setId(model.getId());
        dto.setCode(String.format("%05d", model.getCode()));
        dto.setName(model.getName());
        dto.setFullName(model.getFullName());
        
        dto.setPlanoOrcamentario(planoOrcamentarioFactory.fromModel(model.getPlanoOrcamentario()));
        dto.setCostAccount(costAccountFactory.fromModel(model.getCostAccount()));
        
        
        
        return dto;
    }

    @Override
    protected Long getDtoId(UnidadeOrcamentariaDto dto) {
        return dto.getId();
    }
    
}
