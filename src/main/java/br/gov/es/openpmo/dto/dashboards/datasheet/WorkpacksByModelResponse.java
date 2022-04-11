package br.gov.es.openpmo.dto.dashboards.datasheet;

public class WorkpacksByModelResponse {

    private Long quantity;

    private String modelName;

    private String icon;

    public static WorkpacksByModelResponse from(WorkpackByModelQueryResult from) {
        final WorkpacksByModelResponse to = new WorkpacksByModelResponse();
        to.setQuantity(from.getQuantity());
        to.setModelName(from.getWorkpackName());
        to.setIcon(from.getIcon());
        return to;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
