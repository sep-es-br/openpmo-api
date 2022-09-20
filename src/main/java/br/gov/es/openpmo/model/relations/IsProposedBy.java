package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.actors.Actor;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.baselines.Baseline;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.text.MessageFormat;


@RelationshipEntity("IS_PROPOSED_BY")
public class IsProposedBy {

  @Id
  @GeneratedValue
  private Long id;
  @EndNode
  private Baseline baseline;
  @StartNode
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
