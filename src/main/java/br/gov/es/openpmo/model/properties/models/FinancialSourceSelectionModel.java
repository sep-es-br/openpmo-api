package br.gov.es.openpmo.model.properties.models;

import br.gov.es.openpmo.enumerator.SelectionLevel;
import br.gov.es.openpmo.model.budget.FinancialSource;
import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class FinancialSourceSelectionModel extends PropertyModel {
    
    @Relationship("DEFAULTS_TO")
    private Set<FinancialSource> defaultValue;
    
    private SelectionLevel selectionLevel;
    private boolean multipleSelection;

    public Set<FinancialSource> getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Set<FinancialSource> defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isMultipleSelection() {
        return multipleSelection;
    }

    public void setMultipleSelection(boolean multipleSelection) {
        this.multipleSelection = multipleSelection;
    }

    public SelectionLevel getSelectionLevel() {
        return selectionLevel;
    }

    public void setSelectionLevel(SelectionLevel selectionLevel) {
        this.selectionLevel = selectionLevel;
    }
        
}
