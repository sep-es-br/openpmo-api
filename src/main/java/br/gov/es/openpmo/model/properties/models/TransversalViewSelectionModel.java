package br.gov.es.openpmo.model.properties.models;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

/** Property model that configures a transversal root-view model and its defaults. */
@NodeEntity
public class TransversalViewSelectionModel extends PropertyModel {

  private String defaultValue;

  private String possibleValues;

  /** The transversal model whose instances can be selected by this property. */
  @Property("idRootTransversalViewModel")
  private Long idRootTransversalViewModel;

  private boolean multipleSelection;

  public String getDefaultValue() {
    return this.defaultValue;
  }

  public void setDefaultValue(final String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getPossibleValues() {
    return this.possibleValues;
  }

  public void setPossibleValues(final String possibleValues) {
    this.possibleValues = possibleValues;
  }

  public Long getIdRootTransversalViewModel() {
    return this.idRootTransversalViewModel;
  }

  public void setIdRootTransversalViewModel(final Long idRootTransversalViewModel) {
    this.idRootTransversalViewModel = idRootTransversalViewModel;
  }

  public boolean isMultipleSelection() {
    return this.multipleSelection;
  }

  public void setMultipleSelection(final boolean multipleSelection) {
    this.multipleSelection = multipleSelection;
  }
}
