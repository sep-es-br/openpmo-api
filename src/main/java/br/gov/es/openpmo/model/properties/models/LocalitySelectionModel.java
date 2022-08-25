package br.gov.es.openpmo.model.properties.models;

import br.gov.es.openpmo.model.office.Domain;
import br.gov.es.openpmo.model.office.Locality;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.beans.Transient;
import java.util.Optional;
import java.util.Set;

@NodeEntity
public class LocalitySelectionModel extends PropertyModel {

  private boolean multipleSelection;

  @Relationship("DEFAULTS_TO")
  private Set<Locality> defaultValue;

  @Relationship("IS_LIMITED_BY")
  private Domain domain;

  public boolean isMultipleSelection() {
    return this.multipleSelection;
  }

  public void setMultipleSelection(final boolean multipleSelection) {
    this.multipleSelection = multipleSelection;
  }

  public Set<Locality> getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final Set<Locality> defaultValue) {
    this.defaultValue = defaultValue;
  }

  public Domain getDomain() {
    return this.domain;
  }

  public void setDomain(final Domain domain) {
    this.domain = domain;
  }

  @Transient
  public Long getDomainId() {
    return Optional.ofNullable(this.domain)
      .map(Domain::getId)
      .orElse(null);
  }

}
