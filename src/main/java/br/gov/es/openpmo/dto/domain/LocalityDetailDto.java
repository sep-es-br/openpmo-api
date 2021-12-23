package br.gov.es.openpmo.dto.domain;

import br.gov.es.openpmo.enumerator.LocalityTypesEnum;

import java.util.Set;

public class LocalityDetailDto {

  private Long id;
  private String name;
  private String fullName;
  private String latitude;
  private String longitude;
  private DomainDto domain;
  private DomainDto domainRoot;
  private LocalityTypesEnum type;
  private Set<LocalityDetailDto> children;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public String getLatitude() {
    return this.latitude;
  }

  public void setLatitude(final String latitude) {
    this.latitude = latitude;
  }

  public String getLongitude() {
    return this.longitude;
  }

  public void setLongitude(final String longitude) {
    this.longitude = longitude;
  }

  public DomainDto getDomain() {
    return this.domain;
  }

  public void setDomain(final DomainDto domain) {
    this.domain = domain;
  }

  public Set<LocalityDetailDto> getChildren() {
    return this.children;
  }

  public void setChildren(final Set<LocalityDetailDto> children) {
    this.children = children;
  }

  public LocalityTypesEnum getType() {
    return this.type;
  }

  public void setType(final LocalityTypesEnum type) {
    this.type = type;
  }

  public DomainDto getDomainRoot() {
    return this.domainRoot;
  }

  public void setDomainRoot(final DomainDto domainRoot) {
    this.domainRoot = domainRoot;
  }
}
