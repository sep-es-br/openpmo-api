package br.gov.es.openpmo.dto.domain;

import br.gov.es.openpmo.dto.office.OfficeDto;

public class DomainDto {

    private Long id;
    private String name;
    private String fullName;
    private OfficeDto office;

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

    public OfficeDto getOffice() {
        return office;
    }

    public void setOffice(OfficeDto office) {
        this.office = office;
    }
}
