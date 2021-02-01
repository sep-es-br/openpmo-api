package br.gov.es.openpmo.dto;

public class EntityDto {
    private Long id;

    public EntityDto() {
    }

    public EntityDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
