/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.dto.universalSearch;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author gean.carneiro
 */
public class UniversalSearchParameters {
    
    private Long planId;
    private Long workpackId;
    private Long userId;
    private String userKey;
    private List<Long> transversalWorkpackIds = Collections.emptyList();
    private List<Long> transversalProgramIds = Collections.emptyList();
    private Map<Long, List<Long>> transversalProgramsByWorkpack = Collections.emptyMap();
    private String term;

    public UniversalSearchParameters() {
    }

    public UniversalSearchParameters(Long planId, Long workpackId, Long userId, String userKey, String term) {
        this.planId = planId;
        this.workpackId = workpackId;
        this.userId = userId;
        this.userKey = userKey;
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

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public List<Long> getTransversalWorkpackIds() {
        return transversalWorkpackIds;
    }

    public void setTransversalWorkpackIds(List<Long> transversalWorkpackIds) {
        this.transversalWorkpackIds = transversalWorkpackIds;
    }

    public List<Long> getTransversalProgramIds() { return transversalProgramIds; }

    public void setTransversalProgramIds(final List<Long> transversalProgramIds) {
        this.transversalProgramIds = transversalProgramIds;
    }

    public Map<Long, List<Long>> getTransversalProgramsByWorkpack() { return transversalProgramsByWorkpack; }

    public void setTransversalProgramsByWorkpack(final Map<Long, List<Long>> programsByWorkpack) {
        this.transversalProgramsByWorkpack = programsByWorkpack;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
    
    
    
}
