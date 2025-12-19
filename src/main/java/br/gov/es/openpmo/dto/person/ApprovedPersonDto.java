package br.gov.es.openpmo.dto.person;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class ApprovedPersonDto {
    private Long personId;
    private String evaluationDate;

    public ApprovedPersonDto(Long personId, String evaluationDate) {
        this.personId = personId;
        this.evaluationDate = evaluationDate;
    }

    public Long getPersonId() { return personId; }
    public String getEvaluationDate() { return evaluationDate; }
}