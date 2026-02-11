package br.gov.es.openpmo.apis.edocs.dto;

import java.util.List;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class ProcessNumberWithIds {
    private String processNumber;
    private List<Long> processIds;

    public ProcessNumberWithIds(String processNumber, List<Long> processIds) {
        this.processNumber = processNumber;
        this.processIds = processIds;
    }

    public String getProcessNumber() { return processNumber; }
    public void setProcessNumber(String processNumber) { this.processNumber = processNumber; }

    public List<Long> getProcessIds() { return processIds; }
    public void setProcessIds(List<Long> processIds) { this.processIds = processIds; }
}