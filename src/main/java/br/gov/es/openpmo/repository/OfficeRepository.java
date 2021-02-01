package br.gov.es.openpmo.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.openpmo.model.Office;

public interface OfficeRepository extends Neo4jRepository<Office, Long> {

}
