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

    public static ActorData of(DatasheetActor from) {
        if (from == null) {
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

    public void setIdActor(Long idActor) {
        this.idActor = idActor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setOrganization(Boolean organization) {
        this.organization = organization;
    }

    public void setAvatar(AvatarData avatar) {
        this.avatar = avatar;
    }

    public Long getIdActor() {
        return idActor;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public AvatarData getAvatar() {
        return avatar;
    }

    public Boolean getOrganization() {
        return organization;
    }

    public DatasheetActor getResponse() {
        return null;
    }

}
