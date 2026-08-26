/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.model.properties.models;

import br.gov.es.openpmo.model.budget.BudgetPlan;
import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 *
 * @author gean.carneiro
 */
@NodeEntity
public class BudgetPlanSelectionModel extends PropertyModel {
    
    @Relationship("DEFAULTS_TO")
    private Set<BudgetPlan> defaultValue;
    
    private boolean multipleSelection;

    public Set<BudgetPlan> getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Set<BudgetPlan> defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isMultipleSelection() {
        return multipleSelection;
    }

    public void setMultipleSelection(boolean multipleSelection) {
        this.multipleSelection = multipleSelection;
    }
        
}
