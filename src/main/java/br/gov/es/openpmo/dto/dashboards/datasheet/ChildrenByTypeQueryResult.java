package br.gov.es.openpmo.dto.dashboards.datasheet;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class ChildrenByTypeQueryResult {

    private Long quantity;

    private String type;

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
