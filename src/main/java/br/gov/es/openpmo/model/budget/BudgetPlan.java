/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.model.budget;

import br.gov.es.openpmo.model.Entity;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 *
 * @author gean.carneiro
 */
@NodeEntity
public class BudgetPlan extends Entity{
    
    private String budgetUnitCode;
    private String budgetPlanCode;
    private String budgetPlanName;

    public String getBudgetUnitCode() {
        return budgetUnitCode;
    }

    public void setBudgetUnitCode(String budgetUnitCode) {
        this.budgetUnitCode = budgetUnitCode;
    }

    public String getBudgetPlanCode() {
        return budgetPlanCode;
    }

    public void setBudgetPlanCode(String budgetPlanCode) {
        this.budgetPlanCode = budgetPlanCode;
    }

    public String getBudgetPlanName() {
        return budgetPlanName;
    }

    public void setBudgetPlanName(String budgetPlanName) {
        this.budgetPlanName = budgetPlanName;
    }
    
    
    
}
