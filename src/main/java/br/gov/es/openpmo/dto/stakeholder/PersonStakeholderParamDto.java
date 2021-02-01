package br.gov.es.openpmo.dto.stakeholder;

import java.util.List;

import javax.validation.constraints.NotNull;

import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.utils.ApplicationMessage;

public class PersonStakeholderParamDto {

    private Long id;
    @NotNull(message = ApplicationMessage.ID_WORKPACK_NOT_NULL)
    private Long idWorkpack;
    private String name;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String email;
    private String contactEmail;
    private List<RoleDto> roles;
    private List<PermissionDto> permissions;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactEmail() {
        return this.contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<RoleDto> getRoles() {
        return roles;
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
