package br.gov.es.openpmo.dto.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import br.gov.es.openpmo.utils.ApplicationMessage;

public class DomainStoreDto {

    @NotEmpty(message = ApplicationMessage.NAME_NOT_BLANK)
    private String name;

    @NotEmpty(message = ApplicationMessage.FULLNAME_NOT_BLANK)
    private String fullName;

    @NotNull(message = ApplicationMessage.OFFICE_NOT_NULL)
    private Long idOffice;

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

    public Long getIdOffice() {
        return idOffice;
    }

    public void setIdOffice(Long idOffice) {
        this.idOffice = idOffice;
    }
}
