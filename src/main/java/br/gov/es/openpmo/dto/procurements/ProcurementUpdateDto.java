package br.gov.es.openpmo.dto.procurements;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ProcurementUpdateDto {

    @NotNull(message = "O id é obrigatório")
    private Long id;

    @NotNull(message = "O id do workpack é obrigatório")
    private Long idWorkpack;

    @NotBlank(message = "O número do processo é obrigatório")
    private String processNumber;

    @NotBlank(message = "O objeto é obrigatório")
    private String object;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getIdWorkpack() {
        return this.idWorkpack;
    }

    public void setIdWorkpack(final Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public String getProcessNumber() {
        return this.processNumber;
    }

    public void setProcessNumber(final String processNumber) {
        this.processNumber = processNumber;
    }

    public String getObject() {
        return this.object;
    }

    public void setObject(final String object) {
        this.object = object;
    }
}
