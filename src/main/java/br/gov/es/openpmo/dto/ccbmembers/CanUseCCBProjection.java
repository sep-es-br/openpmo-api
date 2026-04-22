package br.gov.es.openpmo.dto.ccbmembers;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class CanUseCCBProjection {
    public Long idWorkpack;
    public boolean canUseCCB;

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public boolean isCanUseCCB() {
        return canUseCCB;
    }

    public void setCanUseCCB(boolean canUseCCB) {
        this.canUseCCB = canUseCCB;
    }
}
