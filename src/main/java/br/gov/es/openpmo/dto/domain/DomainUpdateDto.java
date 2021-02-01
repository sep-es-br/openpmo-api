package br.gov.es.openpmo.dto.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import br.gov.es.openpmo.utils.ApplicationMessage;

public class DomainUpdateDto {

    @NotNull(message = ApplicationMessage.ID_NOT_NULL)
    private Long id;

    @NotEmpty(message = ApplicationMessage.NAME_NOT_BLANK)
    private String name;

    @NotEmpty(message = ApplicationMessage.FULLNAME_NOT_BLANK)
    private String fullName;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
