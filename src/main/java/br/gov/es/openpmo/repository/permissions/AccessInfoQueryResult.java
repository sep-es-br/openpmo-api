package br.gov.es.openpmo.repository.permissions;

import java.util.List;

public class AccessInfoQueryResult {

    private final List<String> permissions;
    private final List<String> statusList;

    // O Spring Data usa este construtor para mapear os campos do RETURN
    public AccessInfoQueryResult(List<String> permissions, List<String> statusList) {
        this.permissions = permissions;
        this.statusList = statusList;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public List<String> getStatusList() {
        return statusList;
    }
}