package br.gov.es.openpmo.dto.officepermission;

import java.util.List;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.PersonDto;

public class OfficePermissionDto {

    private Long idOffice;
    private PersonDto person;
    private List<PermissionDto> permissions;

    public Long getIdOffice() {
        return this.idOffice;
    }

    public void setIdOffice(Long idOffice) {
        this.idOffice = idOffice;
    }

    public PersonDto getPerson() {
        return this.person;
    }

    public void setPerson(PersonDto person) {
        this.person = person;
    }

    public List<PermissionDto> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<PermissionDto> permissions) {
        this.permissions = permissions;
    }

}
