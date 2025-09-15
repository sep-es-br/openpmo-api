/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.dto.budget;

import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.model.budget.PlanoOrcamentario;
import br.gov.es.openpmo.model.budget.UnidadeOrcamentaria;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author gean.carneiro
 */
public class PlanoOrcamentarioDto {
    
    private Long id;
    private String code;
    private String name;
    private String fullName;

    private Set<UnidadeOrcamentariaDto> unidadeOrcamentaria;
    private Set<CostAccountDto> costAccount;

    public static PlanoOrcamentarioDto of(
            final PlanoOrcamentario model
        ) {
        PlanoOrcamentarioDto dto = new PlanoOrcamentarioDto();
        dto.id = model.getId();
        dto.code = String.format("%06d", model.getCode());
        dto.name = model.getName();
        dto.fullName = model.getFullName();
               
        
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<UnidadeOrcamentariaDto> getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(Set<UnidadeOrcamentariaDto> unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public Set<CostAccountDto> getCostAccount() {
        return costAccount;
    }

    public void setCostAccount(Set<CostAccountDto> costAccount) {
        this.costAccount = costAccount;
    }
    
}
