package br.gov.es.openpmo.model.budget;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity
public class PlanoOrcamentario extends Entity {

    @Id
    @GeneratedValue
    private Long id;

    private Integer code;

    private String name;

    private String fullName;

    @Relationship(type = "CONTROLS", direction = Relationship.INCOMING)
    private Set<UnidadeOrcamentaria> unidadeOrcamentaria;
        @Relationship(type = "ASSIGNED")
    private Set<CostAccount> costAccount;

    public PlanoOrcamentario() {}

    public PlanoOrcamentario(Long id, Integer code, String name, String fullName) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.fullName = fullName;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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

    public Set<UnidadeOrcamentaria> getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(Set<UnidadeOrcamentaria> unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public Set<CostAccount> getCostAccount() {
        return costAccount;
    }

    public void setCostAccount(Set<CostAccount> costAccount) {
        this.costAccount = costAccount;
    }
}
