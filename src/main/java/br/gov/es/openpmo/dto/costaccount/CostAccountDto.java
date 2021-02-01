package br.gov.es.openpmo.dto.costaccount;

import java.util.List;

import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.dto.workpackmodel.PropertyModelDto;

public class CostAccountDto {
    private Long id;
    private List<? extends PropertyDto> properties;
    private List<PropertyModelDto> models;
    private Long idWorkpack;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<? extends PropertyDto> getProperties() {
        return properties;
    }

    public void setProperties(List<? extends PropertyDto> properties) {
        this.properties = properties;
    }

    public List<PropertyModelDto> getModels() {
        return models;
    }

    public void setModels(List<PropertyModelDto> models) {
        this.models = models;
    }

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }
}
