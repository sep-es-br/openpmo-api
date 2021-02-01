package br.gov.es.openpmo.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Portfolio extends Workpack {

    @Relationship(value = "IS_INSTANCE_BY")
    private PortfolioModel instance;

    public PortfolioModel getInstance() {
        return instance;
    }

    public void setInstance(PortfolioModel instance) {
        this.instance = instance;
    }
}
