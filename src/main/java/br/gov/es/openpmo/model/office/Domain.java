package br.gov.es.openpmo.model.office;

import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.util.Set;

@NodeEntity
public class Domain extends Entity {

  private String name;
  private String fullName;

  @Relationship(type = "IS_ROOT_OF", direction = Relationship.INCOMING)
  private Locality localityRoot;

  @Relationship(type = "APPLIES_TO")
  private Office office;

  @Relationship(type = "BELONGS_TO", direction = Relationship.INCOMING)
  private Set<Locality> localities;

  public Set<Locality> getLocalities() {
    return this.localities;
  }

  public void setLocalities(final Set<Locality> localities) {
    this.localities = localities;
  }

  public Office getOffice() {
    return this.office;
  }

  public void setOffice(final Office office) {
    this.office = office;
  }

  public Locality getLocalityRoot() {
    return this.localityRoot;
  }

  public void setLocalityRoot(final Locality localityRoot) {
    this.localityRoot = localityRoot;
  }

  public void update(final Domain domain) {
    this.setName(domain.getName());
    this.setFullName(domain.getFullName());
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

  @Transient
  public Long getLocalityRootId() {
    return this.localityRoot.getId();
  }

}
