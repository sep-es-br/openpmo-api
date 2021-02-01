package br.gov.es.openpmo.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.openpmo.model.Step;

public interface StepRepository extends Neo4jRepository<Step, Long> {

}
