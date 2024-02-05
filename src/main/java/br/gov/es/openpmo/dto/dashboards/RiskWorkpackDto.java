package br.gov.es.openpmo.dto.dashboards;

import org.springframework.data.neo4j.annotation.QueryResult;

import br.gov.es.openpmo.model.risk.Risk;

@QueryResult
public class RiskWorkpackDto {
    private Long idWorkpack;
    private Risk risk;

    public RiskWorkpackDto() {
    }

    public RiskWorkpackDto(Long idWorkpack, Risk risk) {
        this.idWorkpack = idWorkpack;
        this.risk = risk;
    }

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public Risk getRisk() {
        return risk;
    }

    public void setRisk(Risk risk) {
        this.risk = risk;
    }
}
