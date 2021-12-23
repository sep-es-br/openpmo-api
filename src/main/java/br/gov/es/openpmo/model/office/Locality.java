package br.gov.es.openpmo.model.office;

import br.gov.es.openpmo.enumerator.LocalityTypesEnum;
import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@NodeEntity
public class Locality extends Entity {

  private String name;
  private String fullName;
  private String latitude;
  private String longitude;

  private LocalityTypesEnum type;

  @Relationship(type = "IS_IN")
  private Locality parent;

  @Relationship(type = "BELONGS_TO")
  private Domain domain;

  @Relationship(type = "IS_ROOT_OF")
  private Domain domainRoot;

  @Relationship(type = "IS_IN", direction = Relationship.INCOMING)
  private Set<Locality> children;

  public Locality getParent() {
    return this.parent;
  }

  public void setParent(final Locality parent) {
    this.parent = parent;
  }

  public Domain getDomain() {
    return this.domain;
  }

  public void setDomain(final Domain domain) {
    this.domain = domain;
  }

  public Set<Locality> getChildren() {
    return this.children;
  }

  public void setChildren(final Set<Locality> children) {
    this.children = children;
  }

  public void update(final Locality locality) {
    this.setName(locality.getName());
    this.setFullName(locality.getFullName());
    this.setLatitude(locality.getLatitude());
    this.setLongitude(locality.getLongitude());

    if(locality.getType() != null) {
      this.setType(locality.getType());
    }
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getLatitude() {
    return this.latitude;
  }

  public void setLatitude(final String latitude) {
    this.latitude = latitude;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public String getLongitude() {
    return this.longitude;
  }

  public void setLongitude(final String longitude) {
    this.longitude = longitude;
  }

  public LocalityTypesEnum getType() {
    return this.type;
  }

  public void setType(final LocalityTypesEnum type) {
    this.type = type;
  }

  public Domain getDomainRoot() {
    return this.domainRoot;
  }

  public void setDomainRoot(final Domain domainRoot) {
    this.domainRoot = domainRoot;
  }

  @Transactional
  public Long getDomainId() {
    return this.domain.getId();
  }

}
