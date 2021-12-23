package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface IsInContactBookOfRepository extends Neo4jRepository<IsInContactBookOf, Long> {

  @Query("MATCH (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office)," +
         "(office)<-[isAdoptedBy:IS_ADOPTED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO]-(workpack:Workpack) " +
         "WHERE id(person)=$personId " +
         "AND id(workpack)=$workpackId " +
         "RETURN person, isInContactBookOf, office ")
  Optional<IsInContactBookOf> findIsInContactBookOfUsingPersonIdAndWorkpackId(Long personId, Long workpackId);

  @Query("MATCH (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(office:Office) " +
         "WHERE id(person)=$personId " +
         "AND id(office)=$idOffice " +
         "RETURN person, isInContactBookOf, office ")
  Optional<IsInContactBookOf> findIsInContactBookOfByPersonIdAndOfficeId(Long personId, Long idOffice);

}
