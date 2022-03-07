package br.gov.es.openpmo.model.dashboards;

import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetActor;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetStakeholderResponse;
import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.util.Optional;

import static br.gov.es.openpmo.model.dashboards.DashboardUtils.apply;

@NodeEntity
public class StakeholderData extends Entity {

    @Relationship("CONTAINS")
    private ActorData actor;

    private String role;

    public static StakeholderData of(DatasheetStakeholderResponse from) {
        if (from == null) {
            return null;
        }

        final StakeholderData to = new StakeholderData();

        to.setRole(from.getRole());
        apply(from.getActor(), ActorData::of, to::setActor);

        return to;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setActor(ActorData actor) {
        this.actor = actor;
    }

    public ActorData getActor() {
        return actor;
    }

    public String getRole() {
        return role;
    }

    @Transient
    public DatasheetStakeholderResponse getResponse() {
        return new DatasheetStakeholderResponse(
                getDatasheetActor(),
                this.role
        );
    }

    @Transient
    private DatasheetActor getDatasheetActor() {
        return Optional.ofNullable(this.actor)
                .map(ActorData::getResponse)
                .orElse(null);
    }

}
