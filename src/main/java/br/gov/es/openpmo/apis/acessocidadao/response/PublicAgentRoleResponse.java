package br.gov.es.openpmo.apis.acessocidadao.response;

import org.json.JSONObject;

public class PublicAgentRoleResponse {

    private final String guid;
    private final String name;
    private final String type;
    private final String sub;
    private final String organizationGuid;

    public PublicAgentRoleResponse(
            final String guid,
            final String name,
            final String type,
            final String sub,
            final String organizationGuid
    ) {
        this.guid = guid;
        this.name = name;
        this.type = type;
        this.sub = sub;
        this.organizationGuid = organizationGuid;
    }

    public PublicAgentRoleResponse(final JSONObject json) {
        this.guid = json.getString("Guid");
        this.name = json.getString("Nome");
        this.type = json.getString("Tipo");
        this.sub = json.getString("AgentePublicoSub");
        this.organizationGuid = json.optString("LotacaoGuid");
    }

    public String getGuid() {
        return this.guid;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public String getSub() {
        return this.sub;
    }

    public String getOrganizationGuid() {
        return this.organizationGuid;
    }

}
