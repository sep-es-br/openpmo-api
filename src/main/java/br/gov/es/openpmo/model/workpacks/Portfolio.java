package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.model.workpacks.models.PortfolioModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Portfolio extends Workpack {

    @Relationship("IS_INSTANCE_BY")
    private PortfolioModel instance;

    public PortfolioModel getInstance() {
        return this.instance;
    }

    @Override
    public Workpack snapshot() {
        return new Portfolio();
    }

    public void setInstance(final PortfolioModel instance) {
        this.instance = instance;
    }

}
