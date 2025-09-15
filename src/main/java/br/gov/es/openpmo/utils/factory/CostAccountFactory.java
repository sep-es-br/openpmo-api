/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.utils.factory;

import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.utils.PropertyModelInstanceType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
public class CostAccountFactory extends BaseFactory<CostAccount, CostAccountDto> {
    
    private final PropertyFactory propertyFactory;
    private final UnidadeOrcamentariaFactory unidadeOrcamentariaFactory;
    private final PlanoOrcamentarioFactory planoOrcamentarioFactory;
    
    @Autowired
    public CostAccountFactory(
            PropertyFactory propertyFactory,
            UnidadeOrcamentariaFactory unidadeOrcamentariaFactory,
            PlanoOrcamentarioFactory planoOrcamentarioFactory
    ) {
        super(CostAccount.class, CostAccountDto.class);
        this.propertyFactory = propertyFactory;
        this.unidadeOrcamentariaFactory = unidadeOrcamentariaFactory;
        this.planoOrcamentarioFactory = planoOrcamentarioFactory;
    }

    @Override
    protected CostAccount createModel(CostAccountDto dto) {
        
        return new CostAccount();
        
    }

    @Override
    protected Long getModelId(CostAccount model) {
        return model.getId();
    }

    @Override
    protected CostAccountDto createDto(CostAccount model) {
        
        CostAccountDto dto = new CostAccountDto();
        
        dto.setId(model.getId());
        dto.setIdWorkpack(model.getWorkpackId());
        dto.setProperties(getPropertiesFrom(model));
        dto.setModels(getModelsFrom(model));
        dto.setIdCostAccountModel(getIdCostAccountModel(model));
        dto.setUnidadeOrcamentaria(unidadeOrcamentariaFactory.fromModel(model.getUnidadeOrcamentaria()));
        dto.setPlanoOrcamentario(planoOrcamentarioFactory.fromModel(model.getPlanoOrcamentario()));
        dto.setInstruments(model.getInstruments());
        
        return dto;
        
    }

    @Override
    protected Long getDtoId(CostAccountDto dto) {
        return dto.getId();
    }

  private static Long getIdCostAccountModel(CostAccount costAccount) {
    return Optional.ofNullable(costAccount)
      .map(CostAccount::getInstance)
      .map(Entity::getId)
      .orElse(null);
  }


    private List<PropertyModelDto> getModelsFrom(final CostAccount costAccount) {
      return Optional.ofNullable(costAccount)
        .map(ca -> ca.getPropertyModels()
          .stream()
          .map(PropertyModelInstanceType::map)
          .collect(Collectors.toList())
        )
        .orElse(new ArrayList<>());
    }


    private List<PropertyDto> getPropertiesFrom(final CostAccount costAccount) {
      return Optional.of(costAccount.getProperties())
        .map(ca -> ca.stream().map(propertyFactory::fromModel).collect(Collectors.toList()))
        .orElse(new ArrayList<>());
    }
    
}
