package br.gov.es.openpmo.dto.stakeholder;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.PersonDto;

import java.util.ArrayList;
import java.util.List;

public class StakeholderPersonDto {

    private Long idWorkpack;
    private PersonDto person;
    private List<RoleDto> roles = new ArrayList<>(0);
    private List<PermissionDto> permissions = new ArrayList<>(0);

    public StakeholderPersonDto() {
    }

    public StakeholderPersonDto(final Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public Long getIdWorkpack() {
        return this.idWorkpack;
    }

    public void setIdWorkpack(final Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public PersonDto getPerson() {
        return this.person;
    }

    public void setPerson(final PersonDto person) {
        this.person = person;
    }

    public List<RoleDto> getRoles() {
        return this.roles;
    }

    public void setRoles(final List<RoleDto> roles) {
        this.roles = roles;
    }

    public List<PermissionDto> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(final List<PermissionDto> permissions) {
        this.permissions = permissions;
    }

}
