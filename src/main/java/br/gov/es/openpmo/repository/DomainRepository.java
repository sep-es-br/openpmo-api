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
         + " OPTIONAL MATCH (d)<-[bt:BELONGS_TO]-(l:Locality) "
         + " OPTIONAL MATCH (d)-[apl:APPLIES_TO]->(o:Office) " 
         + " OPTIONAL MATCH (d)-[isRootOf:IS_ROOT_OF]->(root:Locality) " 
         + " OPTIONAL MATCH (l)<-[btlc:IS_IN]-(lc:Locality) " 
         + " OPTIONAL MATCH (l)-[btlp:IS_IN]->(lp:Locality) "          
         + " RETURN d, bt, l, [ " +
         " [ [apl, o] ], " +
         " [ [isRootOf, root] ], " +
         " [ [btlc, lc] ] " +
         " [ [btlp, lp] ] " +
         " ] ORDER BY d.name")
  Collection<Domain> findAll(@Param("idOffice") Long idOffice);

  @Query("MATCH (d:Domain) WHERE id(d) = $id OPTIONAL MATCH (d)<-[bt:BELONGS_TO]-(l:Locality) "
       + " OPTIONAL MATCH (d)-[apl:APPLIES_TO]->(o:Office) " 
       + " OPTIONAL MATCH (l)<-[btlc:IS_IN]-(lc:Locality) " 
       + " OPTIONAL MATCH (l)-[btlp:IS_IN]->(lp:Locality) " 
       + " RETURN d, bt, l, [ " +
         " [ [apl, o] ], " +
         " [ [btlc, lc] ], " +
         " [ [btlp, lp] ], " +
         " ] ORDER BY d.name")
  Optional<Domain> findByIdWithLocalities(@Param("id") Long id);

    @Query("MATCH (d:Domain)-[apl:APPLIES_TO]->(o:Office) WHERE id(o) = $idOffice OR $idOffice IS NULL " +
            " OPTIONAL MATCH (d)<-[bt:BELONGS_TO]-(l:Locality) " +
            " WITH d, bt, l, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(d.name), apoc.text.clean($term)) AS nameScore, " +
            "apoc.text.levenshteinSimilarity(apoc.text.clean(d.fullName), apoc.text.clean($term)) AS fullNameScore " +
            "WITH *, CASE WHEN nameScore > fullNameScore THEN nameScore ELSE fullNameScore END AS score " +
            " WHERE score > $searchCutOffScore " +
            " OPTIONAL MATCH (d)-[apl:APPLIES_TO]->(o:Office) " +
            " OPTIONAL MATCH (d)-[isRootOf:IS_ROOT_OF]->(root:Locality) " +
            " OPTIONAL MATCH (l)<-[btlc:IS_IN]-(lc:Locality)" +
            " OPTIONAL MATCH (l)-[btlp:IS_IN]->(lp:Locality)" +
            " RETURN d, bt, l, [ " +
            " [ [apl, o] ], " +
            " [ [isRootOf, root] ], " +
            " [ [btlc, lc] ], " +
            " [ [btlp, lp] ] " +
            " ] ORDER BY score DESC, d.name")
    Collection<Domain> findAllByTerm(@Param("idOffice") Long idOffice,
                                     @Param("term") String term,
                                     @Param("searchCutOffScore") double searchCutOffScore);

}
