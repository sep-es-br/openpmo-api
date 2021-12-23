package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.actors.File;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends Neo4jRepository<File, Long> {
}
