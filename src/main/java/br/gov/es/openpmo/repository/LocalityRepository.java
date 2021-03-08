package br.gov.es.openpmo.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.openpmo.model.Locality;

public interface LocalityRepository extends Neo4jRepository<Locality, Long> {

    @Query("MATCH (d:Domain)<-[:BELONGS_TO]-(l:Locality)" + " WHERE id(d) = {0} RETURN l, "
            + " [ [(l)<-[btl:IS_IN]-(lc:Locality)-[:BELONGS_TO]->(d) | [btl, lc] ] " + "]")
    Collection<Locality> findAllByDomain(@Param("idDomain") Long idDomain);

    @Query("MATCH (d:Domain)<-[:BELONGS_TO]-(l:Locality)"
               + " WHERE id(d) = {0} AND NOT (l)-[:IS_IN]->(:Locality) RETURN l, "
               + " [ [(l)<-[btl:IS_IN]-(lc:Locality)-[:BELONGS_TO]->(d) | [btl, lc] ] " + "]")
    Collection<Locality> findAllByDomainFirstLevel(@Param("idDomain") Long idDomain);

    @Query("MATCH (d:Domain)<-[:BELONGS_TO]-(l:Locality)"
            + " WHERE id(d) = {0} AND NOT (l)-[:IS_IN*1..]->(:Locality) RETURN l, "
            + " [ [(l)<-[btl:IS_IN*1..]-(lc:Locality)-[:BELONGS_TO]->(d) | [btl, lc] ] " + "]")
    Collection<Locality> findAllByDomainProperties(@Param("idDomain") Long idDomain);

    @Query("MATCH (l:Locality) WHERE ID(l) = $id RETURN l, [ [(l)-[ii:IS_IN*]->(lc:Locality) | [ii, lc] ]]")
    Optional<Locality> findByIdWithParent(@Param("id") Long id);
}
