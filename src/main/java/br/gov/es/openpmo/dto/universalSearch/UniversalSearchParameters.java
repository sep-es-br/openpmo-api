/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.dto.universalSearch;

/**
 *
 * @author gean.carneiro
 */
public class UniversalSearchParameters {
    
    private Long planId;
    private Long workpackId;
    private Long userId;
    private String term;

    public UniversalSearchParameters() {
    }

    public UniversalSearchParameters(Long planId, Long workpackId, Long userId, String term) {
        this.planId = planId;
        this.workpackId = workpackId;
        this.userId = userId;
        this.term = term;
    }
    
    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public Long getWorkpackId() {
        return workpackId;
    }

    public void setWorkpackId(Long workpackId) {
        this.workpackId = workpackId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
    
    
    
}
