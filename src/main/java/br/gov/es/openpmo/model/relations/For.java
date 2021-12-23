package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.filter.CustomFilter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "FOR")
public class For extends Entity {

  @EndNode
  private Person person;

  @StartNode
  private CustomFilter customFilter;

  public For(final Person person, final CustomFilter customFilter) {
    this.person = person;
    this.customFilter = customFilter;
  }

  public Person getPerson() {
    return this.person;
  }

  public void setPerson(final Person person) {
    this.person = person;
  }

  public CustomFilter getCustomFilter() {
    return this.customFilter;
  }

  public void setCustomFilter(final CustomFilter customFilter) {
    this.customFilter = customFilter;
  }

}
