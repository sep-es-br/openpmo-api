package br.gov.es.openpmo.dto.domain;

import br.gov.es.openpmo.enumerator.LocalityTypesEnum;

public class LocalityDto {

    private Long id;
    private String name;
    private String fullName;
    private String latitude;
    private String longitude;
    private DomainDto domain;
    private LocalityTypesEnum type;

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

    public DomainDto getDomain() {
        return domain;
    }

    public void setDomain(DomainDto domain) {
        this.domain = domain;
    }

    public LocalityTypesEnum getType() {
        return type;
    }

    public void setType(LocalityTypesEnum type) {
        this.type = type;
    }
}
