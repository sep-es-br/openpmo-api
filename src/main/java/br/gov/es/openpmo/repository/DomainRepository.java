package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.office.Domain;
import br.gov.es.openpmo.repository.custom.CustomRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface DomainRepository extends Neo4jRepository<Domain, Long>, CustomRepository {

  @Query("MATCH (d:Domain)-[apl:APPLIES_TO]->(o:Office) WHERE id(o) = $idOffice OR $idOffice IS NULL "
         + " OPTIONAL MATCH (d)<-[bt:BELONGS_TO]-(l:Locality) RETURN d, bt, l, [ " +
         " [ (d)-[apl:APPLIES_TO]->(o:Office) | [apl, o] ], " +
         " [ (d)-[isRootOf:IS_ROOT_OF]->(root:Locality) | [isRootOf, root] ], " +
         " [ (l)<-[btl:IS_IN]-(lc:Locality) | [btl, lc] ], " +
         " [ (l)-[btl:IS_IN]->(lc:Locality) | [btl, lc] ] " +
         " ] ORDER BY d.name")
  Collection<Domain> findAll(@Param("idOffice") Long idOffice);

  @Query("MATCH (d:Domain) WHERE id(d) = $id OPTIONAL MATCH (d)<-[bt:BELONGS_TO]-(l:Locality) RETURN d, bt, l, [ " +
         " [ (d)-[apl:APPLIES_TO]->(o:Office) | [apl, o] ], " +
         " [ (l)<-[btl:IS_IN]-(lc:Locality) | [btl, lc] ], " +
         " [ (l)-[btl:IS_IN]->(lc:Locality) | [btl, lc] ] " +
         " ] ORDER BY d.name")
  Optional<Domain> findByIdWithLocalities(@Param("id") Long id);
  
  @Query("MATCH (d:Domain)-[apl:APPLIES_TO]->(o:Office) WHERE id(o) = $idOffice OR $idOffice IS NULL " +
         " OPTIONAL MATCH (d)<-[bt:BELONGS_TO]-(l:Locality) " +
		 " WITH d, bt, l, apoc.text.levenshteinSimilarity(apoc.text.clean(d.name + d.fullName), apoc.text.clean($term)) AS score " +
		 " WHERE score > $searchCutOffScore " +
         " RETURN d, bt, l, [ " +
         " [ (d)-[apl:APPLIES_TO]->(o:Office) | [apl, o] ], " +
         " [ (d)-[isRootOf:IS_ROOT_OF]->(root:Locality) | [isRootOf, root] ], " +
         " [ (l)<-[btl:IS_IN]-(lc:Locality) | [btl, lc] ], " +
         " [ (l)-[btl:IS_IN]->(lc:Locality) | [btl, lc] ] " +
         " ] ORDER BY score DESC, d.name")
  Collection<Domain> findAllByTerm(@Param("idOffice") Long idOffice,
						  		   @Param("term") String term,
						  		   @Param("searchCutOffScore") double searchCutOffScore);

}
