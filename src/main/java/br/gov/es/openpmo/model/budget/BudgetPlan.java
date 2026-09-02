/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.model.budget;

import br.gov.es.openpmo.model.Entity;
import java.util.Objects;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 *
 * @author gean.carneiro
 */
@NodeEntity
public class BudgetPlan extends Entity{
    
    private String budgetUnitCode;
    private String budgetUnitName;
    private String budgetUnitAcronym;
    private String budgetPlanCode;
    private String budgetPlanName;
    private String identityKey;

    public String getBudgetUnitCode() {
        return budgetUnitCode;
    }

    public void setBudgetUnitCode(String budgetUnitCode) {
        this.budgetUnitCode = budgetUnitCode;
    }

    public String getBudgetUnitName() {
        return budgetUnitName;
    }

    public void setBudgetUnitName(String budgetUnitName) {
        this.budgetUnitName = budgetUnitName;
    }

    public String getBudgetUnitAcronym() {
        return budgetUnitAcronym;
    }

    public void setBudgetUnitAcronym(String budgetUnitAcronym) {
        this.budgetUnitAcronym = budgetUnitAcronym;
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

    public String getIdentityKey() {
        return identityKey;
    }

    public void setIdentityKey(String identityKey) {
        this.identityKey = identityKey;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof BudgetPlan)) {
            return false;
        }
        BudgetPlan that = (BudgetPlan) object;
        return Objects.equals(budgetUnitCode, that.budgetUnitCode)
            && Objects.equals(budgetPlanCode, that.budgetPlanCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(budgetUnitCode, budgetPlanCode);
    }
}
