package br.gov.es.openpmo.model.properties.models;

import br.gov.es.openpmo.model.relations.Accepts;
import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class CriteriaSelectionModel extends SelectionModel {

  @Relationship("ACCEPTS")
  private Set<Accepts> acceptedOptions;

  public Set<Accepts> getAcceptedOptions() {
    return this.acceptedOptions;
  }

  public void setAcceptedOptions(final Set<Accepts> acceptedOptions) {
    this.acceptedOptions = acceptedOptions;
  }

}
