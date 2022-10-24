package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.actors.Actor;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.baselines.Baseline;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.data.neo4j.core.schema.Property;


import java.text.MessageFormat;


@RelationshipProperties
public class IsProposedBy {

  @RelationshipId
  private Long id;
  @TargetNode
  private Baseline baseline;

  private Person proposer;

  @Property("role")
  private String formattedRole;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Baseline getBaseline() {
    return this.baseline;
  }

  public void setBaseline(final Baseline baseline) {
    this.baseline = baseline;
  }

  public Person getProposer() {
    return this.proposer;
  }

  public String getFormattedRole() {
    return this.formattedRole;
  }

  public void fillProposerData(final Person person) {
    this.proposer = person;
    this.formattedRole = person.getName();
  }

  public void fillProposerData(final IsStakeholderIn proposer) {
    final Actor actor = proposer.getActor();
    this.proposer = (Person) actor;
    final String name = actor.getName();
    final String roleName = proposer.getRole() != null ? proposer.getRole() : null;
    this.formattedRole = MessageFormat.format("{0} ({1})", name, roleName);
  }

}
