package br.gov.es.openpmo.dto.dashboards.datasheet;

public class ChildrenByTypeResponse {

    private Long quantity;

    private String type;

    public static ChildrenByTypeResponse from(ChildrenByTypeQueryResult from) {
        final ChildrenByTypeResponse to = new ChildrenByTypeResponse();
        to.setQuantity(from.getQuantity());
        to.setType(from.getType());
        return to;
    }

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
