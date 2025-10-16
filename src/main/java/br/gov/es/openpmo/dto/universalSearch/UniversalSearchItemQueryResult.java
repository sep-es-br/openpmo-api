/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.dto.universalSearch;

import java.util.List;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 *
 * @author gean.carneiro
 */
@QueryResult
public class UniversalSearchItemQueryResult {
    
    private Long planId;
    private Long id;
    private String model;
    private String icon;
    private String name;
    private String fullName;
    private Double matchDistance;
    private List<BreadCrumb> breadcrumbs;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public List<BreadCrumb> getBreadcrumbs() {
        return breadcrumbs;
    }

    public void setBreadcrumbs(List<BreadCrumb> breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }

    public Double getMatchDistance() {
        return matchDistance;
    }

    public void setMatchDistance(Double matchDistance) {
        this.matchDistance = matchDistance;
    }
        
    public static class BreadCrumb {
        
        private Long id;
        private String nome;
        private String modelo;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getModelo() {
            return modelo;
        }

        public void setModelo(String modelo) {
            this.modelo = modelo;
        }
        
        
        
    }
}
