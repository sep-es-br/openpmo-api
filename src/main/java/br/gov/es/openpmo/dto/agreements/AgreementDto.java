package br.gov.es.openpmo.dto.agreements;

import br.gov.es.openpmo.model.agreements.Agreement;
import br.gov.es.openpmo.model.agreements.AgreementType;

public class AgreementDto {

    private Long id;
    private Long idWorkpack;
    private AgreementType type;
    private Long processId;
    private String object;

    public AgreementDto() {
    }

    public AgreementDto(final Agreement agreement) {
        this.id = agreement.getId();
        this.idWorkpack = agreement.getIdWorkpack();
        this.type = agreement.getType();
        this.processId = agreement.getProcessId();
        this.object = agreement.getObject();
    }

    public static AgreementDto of(final Agreement agreement) { return new AgreementDto(agreement); }
    public Long getId() { return this.id; }
    public void setId(final Long id) { this.id = id; }
    public Long getIdWorkpack() { return this.idWorkpack; }
    public void setIdWorkpack(final Long idWorkpack) { this.idWorkpack = idWorkpack; }
    public AgreementType getType() { return this.type; }
    public void setType(final AgreementType type) { this.type = type; }
    public Long getProcessId() { return this.processId; }
    public void setProcessId(final Long processId) { this.processId = processId; }
    public String getObject() { return this.object; }
    public void setObject(final String object) { this.object = object; }
}
