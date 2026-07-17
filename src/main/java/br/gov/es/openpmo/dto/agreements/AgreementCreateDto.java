package br.gov.es.openpmo.dto.agreements;

import br.gov.es.openpmo.model.agreements.AgreementType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AgreementCreateDto {

    @NotNull(message = "O id do workpack é obrigatório")
    private Long idWorkpack;

    @NotNull(message = "O tipo do instrumento é obrigatório")
    private AgreementType type;

    @NotBlank(message = "O número do processo é obrigatório")
    private String processNumber;

    @NotBlank(message = "O objeto é obrigatório")
    private String object;

    public Long getIdWorkpack() { return this.idWorkpack; }
    public void setIdWorkpack(final Long idWorkpack) { this.idWorkpack = idWorkpack; }
    public AgreementType getType() { return this.type; }
    public void setType(final AgreementType type) { this.type = type; }
    public String getProcessNumber() { return this.processNumber; }
    public void setProcessNumber(final String processNumber) { this.processNumber = processNumber; }
    public String getObject() { return this.object; }
    public void setObject(final String object) { this.object = object; }
}
