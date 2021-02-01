package br.gov.es.openpmo.dto.costaccount;

import java.util.List;

import javax.validation.constraints.NotNull;

import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.utils.ApplicationMessage;

public class CostAccountUpdateDto {

    @NotNull(message = ApplicationMessage.ID_NOT_NULL)
    private Long id;

    @NotNull(message = ApplicationMessage.ID_WORKPACK_NOT_NULL)
    private Long idWorkpack;

    private List<? extends PropertyDto> properties;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public List<? extends PropertyDto> getProperties() {
        return properties;
    }

    public void setProperties(List<? extends PropertyDto> properties) {
        this.properties = properties;
    }
}
