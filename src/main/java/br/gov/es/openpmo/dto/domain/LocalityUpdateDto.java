package br.gov.es.openpmo.dto.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.gov.es.openpmo.enumerator.LocalityTypesEnum;
import br.gov.es.openpmo.utils.ApplicationMessage;

public class LocalityUpdateDto {

    @NotNull(message = ApplicationMessage.ID_NOT_NULL)
    private Long id;

    @NotBlank(message = ApplicationMessage.NAME_NOT_BLANK)
    private String name;

    @NotBlank(message = ApplicationMessage.FULLNAME_NOT_BLANK)
    private String fullName;

    private String latitude;
    private String longitude;

    @NotNull(message = ApplicationMessage.LOCALITY_DOMAIN_NOT_NULL)
    private Long idDomain;

    @NotNull(message = ApplicationMessage.LOCALITY_TYPE_NOT_NULL)
    private LocalityTypesEnum type;

    private Long idParent;

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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Long getIdDomain() {
        return idDomain;
    }

    public void setIdDomain(Long idDomain) {
        this.idDomain = idDomain;
    }

    public Long getIdParent() {
        return idParent;
    }

    public void setIdParent(Long idParent) {
        this.idParent = idParent;
    }

    public LocalityTypesEnum getType() {
        return type;
    }

    public void setType(LocalityTypesEnum type) {
        this.type = type;
    }
}
