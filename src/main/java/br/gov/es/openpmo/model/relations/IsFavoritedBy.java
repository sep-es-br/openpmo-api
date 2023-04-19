package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity("IS_FAVORITED_BY")
public class IsFavoritedBy {

  @Id
  @GeneratedValue
  private Long id;

  private Long idPlan;

  @StartNode
  private Workpack workpack;

  @EndNode
  private Person person;


  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Long getIdPlan() {
    return this.idPlan;
  }

  public void setIdPlan(final Long idPlan) {
    this.idPlan = idPlan;
  }

  public Workpack getWorkpack() {
    return this.workpack;
  }

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

  public Person getPerson() {
    return this.person;
  }

  public void setPerson(final Person person) {
    this.person = person;
  }

  public boolean isEqual(
    final Long idWorkpack,
    final Long idPlan
  ) {
    return this.workpack.getId().equals(idWorkpack) && this.idPlan.equals(idPlan);
  }

}
