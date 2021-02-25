package br.gov.es.openpmo.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.openpmo.model.Domain;

public interface DomainRepository extends Neo4jRepository<Domain, Long> {
    @Query("MATCH (d:Domain)-[apl:APPLIES_TO]->(o:Office) WHERE ID(o) = $idOffice OR $idOffice IS NULL "
               + " OPTIONAL MATCH (d)<-[bt:BELONGS_TO]-(l:Locality) RETURN d, bt, l, [ " +
               " [ (d)-[apl:APPLIES_TO]->(o:Office) | [apl, o] ], " +
               " [ (l)<-[btl:IS_IN]-(lc:Locality) | [btl, lc] ], " +
               " [ (l)-[btl:IS_IN]->(lc:Locality) | [btl, lc] ] " +
               " ] ORDER BY d.name")
    Collection<Domain> findAll(@Param("idOffice") Long idOffice);

    @Query("MATCH (d:Domain) WHERE ID(d) = $id OPTIONAL MATCH (d)<-[bt:BELONGS_TO]-(l:Locality) RETURN d, bt, l, [ " +
               " [ (d)-[apl:APPLIES_TO]->(o:Office) | [apl, o] ], " +
               " [ (l)<-[btl:IS_IN]-(lc:Locality) | [btl, lc] ], " +
               " [ (l)-[btl:IS_IN]->(lc:Locality) | [btl, lc] ] " +
               " ] ORDER BY d.name")
    Optional<Domain> findByIdWithLocalities(@Param("id") Long id);
}
