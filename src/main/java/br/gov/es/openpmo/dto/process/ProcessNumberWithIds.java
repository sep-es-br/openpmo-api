package br.gov.es.openpmo.dto.process;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Resultado da consulta ao Neo4j que agrupa os IDs locais por número de processo.
 */
@QueryResult
public class ProcessNumberWithIds {

    private String processNumber;
    private List<Long> processIds;

    public ProcessNumberWithIds(
        final String processNumber,
        final List<Long> processIds
    ) {
        this.processNumber = processNumber;
        this.processIds = processIds;
    }

    public String getProcessNumber() { return processNumber; }
    public void setProcessNumber(final String processNumber) { this.processNumber = processNumber; }
    public List<Long> getProcessIds() { return processIds; }
    public void setProcessIds(final List<Long> processIds) { this.processIds = processIds; }
}
