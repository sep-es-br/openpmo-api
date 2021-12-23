package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.IsProposedBy;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface IsProposedByRepository extends Neo4jRepository<IsProposedBy, Long> {
}
