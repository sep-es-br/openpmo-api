package br.gov.es.openpmo.dto.menu;

public class BreadcrumbDto {

    private Long id;
    private String name;
    private String fullName;
    private String type;
    private String modelName;

    public BreadcrumbDto() {

    }

    public BreadcrumbDto(Long id, String name, String fullName, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.fullName = fullName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
