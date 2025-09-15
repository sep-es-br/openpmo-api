/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.dto.budget;

import br.gov.es.openpmo.dto.costaccount.CostAccountDto;
import br.gov.es.openpmo.model.budget.UnidadeOrcamentaria;
import java.util.Set;

/**
 *
 * @author gean.carneiro
 */
public class UnidadeOrcamentariaDto {
    
    private Long id;
    private String code;
    private String name;
    private String fullName;
    private Set<PlanoOrcamentarioDto> planoOrcamentario;
    private Set<CostAccountDto> costAccount;
    
    /**
     * 
     * @param model
     * @param planoOrcamentarioDto
     * @param costAccountDto
     * @return 
     */
    public static UnidadeOrcamentariaDto of(
            final UnidadeOrcamentaria model,
            final Set<PlanoOrcamentarioDto> planoOrcamentarioDto,
            final Set<CostAccountDto> costAccountDto
    ) {
        return null;
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

    public Set<PlanoOrcamentarioDto> getPlanoOrcamentario() {
        return planoOrcamentario;
    }

    public void setPlanoOrcamentario(Set<PlanoOrcamentarioDto> planoOrcamentario) {
        this.planoOrcamentario = planoOrcamentario;
    }

    public Set<CostAccountDto> getCostAccount() {
        return costAccount;
    }

    public void setCostAccount(Set<CostAccountDto> costAccount) {
        this.costAccount = costAccount;
    }
    
    
    
}
