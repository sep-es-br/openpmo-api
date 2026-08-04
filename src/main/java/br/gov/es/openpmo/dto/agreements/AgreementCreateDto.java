package br.gov.es.openpmo.dto.agreements;

import br.gov.es.openpmo.model.agreements.AgreementType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AgreementCreateDto {

    @NotNull(message = "O id do workpack é obrigatório")
    private Long idWorkpack;

    @NotNull(message = "O tipo do instrumento é obrigatório")
    private AgreementType type;

    @NotNull(message = "O id do processo é obrigatório")
    private Long processId;

    @NotBlank(message = "O objeto é obrigatório")
    private String object;

    @NotBlank(message = "O identificador do órgão é obrigatório")
    private String organizationIdentifier;

    public Long getIdWorkpack() { return this.idWorkpack; }
    public void setIdWorkpack(final Long idWorkpack) { this.idWorkpack = idWorkpack; }
    public AgreementType getType() { return this.type; }
    public void setType(final AgreementType type) { this.type = type; }
    public Long getProcessId() { return this.processId; }
    public void setProcessId(final Long processId) { this.processId = processId; }
    public String getObject() { return this.object; }
    public void setObject(final String object) { this.object = object; }
    public String getOrganizationIdentifier() { return this.organizationIdentifier; }
    public void setOrganizationIdentifier(final String organizationIdentifier) { this.organizationIdentifier = organizationIdentifier; }
}
