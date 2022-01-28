package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.office.Locality;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface LocalityRepository extends Neo4jRepository<Locality, Long>, CustomRepository {

  @Query("MATCH (d:Domain)<-[:BELONGS_TO]-(l:Locality)" + " WHERE id(d) = {0} RETURN l, "
         + " [ [(l)<-[btl:IS_IN]-(lc:Locality)-[:BELONGS_TO]->(d) | [btl, lc] ] " + "]")
  Collection<Locality> findAllByDomain(@Param("idDomain") Long idDomain);

  @Query("MATCH (domain:Domain)<-[isRootOf:IS_ROOT_OF]-(locality:Locality) " +
         "WHERE id(domain)=$idDomain " +
         "RETURN domain, isRootOf, locality"
  )
  Optional<Locality> findLocalityRootFromDomain(Long idDomain);

  @Query(
    "MATCH (d:Domain)<-[:IS_ROOT_OF]-(root:Locality)" +
    "MATCH (root)<-[:IS_IN]-(locality:Locality)-[:BELONGS_TO]->(d) " +
    "WHERE id(d)=$idDomain " +
    "RETURN locality, [ " +
    "    [(locality)<-[isIn:IS_IN]-(children:Locality)-[:BELONGS_TO]->(d) | [isIn, children] ] " +
    "]"
  )
  Collection<Locality> findAllByDomainFirstLevel(@Param("idDomain") Long idDomain);

  @Query(
    "MATCH (d:Domain)<-[:BELONGS_TO]-(l:Locality) " +
    "WHERE id(d)={0} AND NOT (l)-[:IS_IN*1..]->(:Locality) " +
    "RETURN l, " +
    "    [ [(l)<-[btl:IS_IN*1..]-(lc:Locality)-[:BELONGS_TO]->(d) | [btl, lc] ] " +
    "]"
  )
  Collection<Locality> findAllByDomainProperties(@Param("idDomain") Long idDomain);

  @Query("MATCH (l:Locality) WHERE id(l) = $id RETURN l, [ [(l)-[ii:IS_IN*]->(lc:Locality) | [ii, lc] ]]")
  Optional<Locality> findByIdWithParent(@Param("id") Long id);
}
