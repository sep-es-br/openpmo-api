package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.model.workpacks.models.PortfolioModel;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
public class Portfolio extends Workpack {

  @Relationship("IS_INSTANCE_BY")
  private PortfolioModel instance;

  public PortfolioModel getInstance() {
    return this.instance;
  }

  public void setInstance(final PortfolioModel instance) {
    this.instance = instance;
  }

  @Override
  public Workpack snapshot() {
    return new Portfolio();
  }

}
