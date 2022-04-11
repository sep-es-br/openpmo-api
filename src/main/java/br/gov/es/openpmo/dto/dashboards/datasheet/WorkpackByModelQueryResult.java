package br.gov.es.openpmo.dto.dashboards.datasheet;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class WorkpackByModelQueryResult {

    private Long quantity;

    private String singularName;

    private String pluralName;

    private String icon;

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getSingularName() {
        return singularName;
    }

    public void setSingularName(String singularName) {
        this.singularName = singularName;
    }

    public String getPluralName() {
        return pluralName;
    }

    public void setPluralName(String pluralName) {
        this.pluralName = pluralName;
    }

    public String getWorkpackName() {
        return this.quantity == 1 ? this.singularName : this.pluralName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
