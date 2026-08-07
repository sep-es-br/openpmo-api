package br.gov.es.openpmo.dto.agreements;

import javax.validation.constraints.NotNull;

public class AgreementUpdateDto extends AgreementCreateDto {

    @NotNull(message = "O id é obrigatório")
    private Long id;

    public Long getId() { return this.id; }
    public void setId(final Long id) { this.id = id; }
}
