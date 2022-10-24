package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.filter.CustomFilter;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class For {

  @RelationshipId
  private Long id;

  @TargetNode
  private Person person;

  private CustomFilter customFilter;

  public For(
    final Person person,
    final CustomFilter customFilter
  ) {
    this.person = person;
    this.customFilter = customFilter;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
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
