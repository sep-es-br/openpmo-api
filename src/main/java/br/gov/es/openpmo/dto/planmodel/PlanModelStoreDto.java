package br.gov.es.openpmo.dto.planmodel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.gov.es.openpmo.utils.ApplicationMessage;

public class PlanModelStoreDto {

    @NotNull(message = ApplicationMessage.OFFICE_NOT_NULL)
    private Long idOffice;
    @NotBlank(message = ApplicationMessage.NAME_NOT_BLANK)
    private String name;
    @NotBlank(message = ApplicationMessage.FULLNAME_NOT_BLANK)
    private String fullName;

    public Long getIdOffice() {
        return idOffice;
    }

    public void setIdOffice(Long idOffice) {
        this.idOffice = idOffice;
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
