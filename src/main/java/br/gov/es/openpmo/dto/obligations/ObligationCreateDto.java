package br.gov.es.openpmo.dto.obligations;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ObligationCreateDto {

    @NotNull(message = "O id do workpack é obrigatório")
    private Long idWorkpack;

    @NotBlank(message = "O número do empenho é obrigatório")
    private String obligationNumber;

    @NotBlank(message = "A descrição é obrigatória")
    private String description;
    @NotBlank
    private String managementUnitCode;

    public Long getIdWorkpack() {
        return this.idWorkpack;
    }

    public void setIdWorkpack(final Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public String getObligationNumber() {
        return this.obligationNumber;
    }

    public void setObligationNumber(
        final String obligationNumber
    ) {
        this.obligationNumber = obligationNumber;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(
        final String description
    ) {
        this.description = description;
    }
    public String getManagementUnitCode() { return managementUnitCode; }
    public void setManagementUnitCode(String value) { managementUnitCode = value; }
}
