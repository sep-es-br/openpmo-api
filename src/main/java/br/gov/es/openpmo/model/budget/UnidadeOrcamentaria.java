package br.gov.es.openpmo.model.budget;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity
public class UnidadeOrcamentaria extends Entity {

    @Id
    @GeneratedValue
    private Long id;

    private Integer code;

    private String name;

    private String fullName;

    @Relationship(type = "CONTROLS", direction = Relationship.OUTGOING)
    private Set<PlanoOrcamentario> planoOrcamentario;

    @Relationship(type = "CONTROLS")
    private Set<CostAccount> costAccount;

    public UnidadeOrcamentaria() {}

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

    public Set<PlanoOrcamentario> getPlanoOrcamentario() {
        return planoOrcamentario;
    }

    public void setPlanoOrcamentario(Set<PlanoOrcamentario> planoOrcamentario) {
        this.planoOrcamentario = planoOrcamentario;
    }

    public Set<CostAccount> getCostAccount() {
        return costAccount;
    }

    public void setCostAccount(Set<CostAccount> costAccount) {
        this.costAccount = costAccount;
    }
}
