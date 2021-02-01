package br.gov.es.openpmo.dto.domain;

import java.util.Set;

public class LocalityPropertyDto {
    
    private Long id;
    private String name;
    private String fullName;
    private Set<LocalityPropertyDto> children;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<LocalityPropertyDto> getChildren() {
        return this.children;
    }

    public void setChildren(Set<LocalityPropertyDto> children) {
        this.children = children;
    }

}
