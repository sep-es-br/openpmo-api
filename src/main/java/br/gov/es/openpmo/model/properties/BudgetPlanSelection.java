/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.model.properties;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.budget.BudgetPlan;
import br.gov.es.openpmo.model.properties.models.BudgetPlanSelectionModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 *
 * @author gean.carneiro
 */
@NodeEntity
public class BudgetPlanSelection extends Property<BudgetPlanSelection, Set<BudgetPlan>> {
    
    @Relationship("VALUES")
    private Set<BudgetPlan> value;

    @Relationship("IS_DRIVEN_BY")
    private BudgetPlanSelectionModel driver;

    private CategoryEnum category;

    @Override
    public Set<BudgetPlan> getValue() {
        return value;
    }

    @Override
    public void setValue(Set<BudgetPlan> value) {
        this.value = value;
    }

    public BudgetPlanSelectionModel getDriver() {
        return driver;
    }

    public void setDriver(BudgetPlanSelectionModel driver) {
        this.driver = driver;
    }

    @Override
    public PropertyModel getPropertyModel() {
        return this.driver;
    }

    @Override
    public CategoryEnum getCategory() {
        return this.category;
    }

    @Override
    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    @Override
    public BudgetPlanSelection snapshot() {
        BudgetPlanSelection snapshot = new BudgetPlanSelection();
        snapshot.setValue(Optional.ofNullable(this.value).map(HashSet::new).orElse(null));
        return snapshot;
    }

    @Override
    public boolean hasChanges(BudgetPlanSelection other) {
        return (this.value != null || other.value != null)
           && (this.value != null && other.value == null || this.value == null || !this.value.equals(other.value));
    }

    
 
    
    
}
