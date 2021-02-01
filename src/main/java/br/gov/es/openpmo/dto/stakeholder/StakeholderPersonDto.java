package br.gov.es.openpmo.dto.stakeholder;

import java.util.ArrayList;
import java.util.List;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.person.PersonDto;

public class StakeholderPersonDto {

    private Long idWorkpack;
    private PersonDto person;
    private List<RoleDto> roles = new ArrayList<>(0);
    private List<PermissionDto> permissions = new ArrayList<>(0);

    public Long getIdWorkpack() {
        return this.idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public PersonDto getPerson() {
        return this.person;
    }

    public void setPerson(PersonDto person) {
        this.person = person;
    }

    public List<RoleDto> getRoles() {
        return this.roles;
    }

    public void setRoles(List<RoleDto> roles) {
        this.roles = roles;
    }

    public List<PermissionDto> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(List<PermissionDto> permissions) {
        this.permissions = permissions;
    }

}
