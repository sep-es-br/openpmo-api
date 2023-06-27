package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.dto.ccbmembers.MemberAs;
import br.gov.es.openpmo.dto.ccbmembers.PersonResponse;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.PermissionEntityProvider;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.scheduler.updateroles.HasRole;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.springframework.data.annotation.Transient;

@RelationshipEntity(type = "IS_CCB_MEMBER_FOR")
public class IsCCBMemberFor implements HasRole, PermissionEntityProvider {

  @Id
  @GeneratedValue
  private Long id;

  @Property("inRole")
  private String role;

  @Property("workLocation")
  private String workLocation;

  @Property("active")
  private Boolean active;

  @StartNode
  private Person person;

  @EndNode
  private Workpack workpack;

  public IsCCBMemberFor() {
  }

  public IsCCBMemberFor(
    final MemberAs memberAs,
    final Person person,
    final Workpack workpack
  ) {
    this.role = memberAs.getRole();
    this.workLocation = memberAs.getWorkLocation();
    this.active = memberAs.getActive();
    this.person = person;
    this.workpack = workpack;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Workpack getWorkpack() {
    return this.workpack;
  }

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

  public MemberAs getMemberAs() {
    final MemberAs memberAs = new MemberAs();
    memberAs.setRole(this.role);
    memberAs.setWorkLocation(this.workLocation);
    memberAs.setActive(this.active);
    return memberAs;
  }

  public boolean getActive() {
    return this.active;
  }

  public void setActive(final Boolean active) {
    this.active = active;
  }

  public String getRole() {
    return this.role;
  }

  public void setRole(final String role) {
    this.role = role;
  }

  public String getWorkLocation() {
    return this.workLocation;
  }

  public void setWorkLocation(final String workLocation) {
    this.workLocation = workLocation;
  }

  @Transient
  public Long getIdPerson() {
    return this.person.getId();
  }

  @Transient
  public Long getWorkpackId() {
    return this.workpack.getId();
  }

  @Transient
  public PersonResponse getPersonResponse() {
    return this.person.getPersonResponse();
  }

  public String getMemberName() {
    return this.person.getName();
  }

  public Person getPerson() {
    return this.person;
  }

  public void setPerson(final Person person) {
    this.person = person;
  }

  @Override
  public Entity getPermissionEntity() {
    return this.workpack;
  }
}
