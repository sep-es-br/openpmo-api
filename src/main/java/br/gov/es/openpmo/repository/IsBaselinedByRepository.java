package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.IsBaselinedBy;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IsBaselinedByRepository extends Neo4jRepository<IsBaselinedBy, Long> {

}
