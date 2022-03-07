package br.gov.es.openpmo.dto.dashboards.datasheet;

public class DatasheetStakeholderResponse {

    private final DatasheetActor actor;

    private final String role;

    public DatasheetStakeholderResponse(final DatasheetActor actor, final String role) {
        this.actor = actor;
        this.role = role;
    }

    public DatasheetActor getActor() {
        return actor;
    }

    public String getRole() {
        return role;
    }

}
