package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetActor;
import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static br.gov.es.openpmo.model.dashboards.DashboardUtils.apply;

@NodeEntity
public class ActorData extends Entity {

  private Long idActor;

  private String name;

  private String fullName;

  @Relationship("CONTAINS")
  private AvatarData avatar;

  private Boolean organization;

  public static ActorData of(final DatasheetActor from) {
    if(from == null) {
      return null;
    }

    final ActorData to = new ActorData();

    to.setIdActor(from.getId());
    to.setName(from.getName());
    to.setFullName(from.getFullName());
    to.setOrganization(from.getOrganization());
    apply(from.getAvatar(), AvatarData::of, to::setAvatar);

    return to;

  }

  public Long getIdActor() {
    return this.idActor;
  }

  public void setIdActor(final Long idActor) {
    this.idActor = idActor;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public AvatarData getAvatar() {
    return this.avatar;
  }

  public void setAvatar(final AvatarData avatar) {
    this.avatar = avatar;
  }

  public Boolean getOrganization() {
    return this.organization;
  }

  public void setOrganization(final Boolean organization) {
    this.organization = organization;
  }

  public DatasheetActor getResponse() {
    return null;
  }

}
