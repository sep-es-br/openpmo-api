package br.gov.es.openpmo.model.properties.models;

import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Fixed project property that stores the transversal views associated with a
 * project.
 *
 * <p>This is intentionally a direct {@link PropertyModel} subtype.  A
 * transversal view is supplied by the transversal-view menu and is not a
 * user-defined {@link SelectionModel} with manually maintained options.</p>
 */
@NodeEntity
public class TransversalViewSelectionModel extends PropertyModel {

  private String defaultValue;

  private String possibleValues;

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

  public boolean isMultipleSelection() {
    return this.multipleSelection;
  }

  public void setMultipleSelection(final boolean multipleSelection) {
    this.multipleSelection = multipleSelection;
  }
}
