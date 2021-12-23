package br.gov.es.openpmo.dto.domain;

import br.gov.es.openpmo.enumerator.LocalityTypesEnum;
import br.gov.es.openpmo.utils.ApplicationMessage;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class LocalityStoreDto {

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

  public Long getIdDomain() {
    return this.idDomain;
  }

  public void setIdDomain(final Long idDomain) {
    this.idDomain = idDomain;
  }

  public Long getIdParent() {
    return this.idParent;
  }

  public void setIdParent(final Long idParent) {
    this.idParent = idParent;
  }

  public LocalityTypesEnum getType() {
    return this.type;
  }

  public void setType(final LocalityTypesEnum type) {
    this.type = type;
  }
}
