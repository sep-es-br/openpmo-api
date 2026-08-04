package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.IsInContactBookOf;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IsInContactBookOfRepository extends Neo4jRepository<IsInContactBookOf, Long> {

  @Query("MATCH (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(workPlace:WorkPlace)" +
         "-[forOffice:`FOR`]->(office:Office)," +
         "(office)<-[isAdoptedBy:IS_ADOPTED_BY]-(plan:Plan)<-[belongsTo:BELONGS_TO]-(workpack:Workpack) " +
         "WHERE id(person)=$personId " +
         "AND id(workpack)=$workpackId " +
         "AND id(plan)=$planId " +
         "RETURN person, isInContactBookOf, workPlace, forOffice, office ")
  Optional<IsInContactBookOf> findIsInContactBookOfUsingPersonIdAndWorkpackId(
    Long personId,
    Long workpackId,
    Long planId
  );

  @Query("MATCH (person:Person)-[isInContactBookOf:IS_IN_CONTACT_BOOK_OF]->(workPlace:WorkPlace)" +
         "-[forOffice:`FOR`]->(office:Office) " +
         "WHERE id(person)=$personId " +
         "AND id(office)=$idOffice " +
         "RETURN person, isInContactBookOf, workPlace, forOffice, office ")
  Optional<IsInContactBookOf> findIsInContactBookOfByPersonIdAndOfficeId(
    @Param("personId") Long personId,
    @Param("idOffice") Long idOffice
  );

  @Query("MATCH (p:Person)-[i:IS_IN_CONTACT_BOOK_OF]->(:WorkPlace)-[:`FOR`]->(o:Office) " +
         "where id(p)=$personId and id(o)=$officeId " +
         "return count(i)>0")
  boolean existsByPersonIdAndOfficeId(
    @Param("personId") Long personId,
    @Param("officeId") Long officeId
  );

  @Query("MATCH (p:Person) where id(p) = $personId " +
          "MATCH (o:Office) where id(o) = $officeId " +
          "CREATE (wp:WorkPlace) " +
          "CREATE (p)-[r:IS_IN_CONTACT_BOOK_OF { " +
          "  email: $email, " +
          "  address: $address, " +
          "  phoneNumber: $phoneNumber " +
          "}]->(wp) " +
          "CREATE (wp)-[forOffice:`FOR`]->(o) " +
          "RETURN p, r, wp, forOffice, o")
  IsInContactBookOf createIsInContactBookOf(Long personId, Long officeId,
                                       String email, String address,
                                       String phoneNumber);

  @Query("MATCH (p:Person)-[r:IS_IN_CONTACT_BOOK_OF]->(wp:WorkPlace)-[forOffice:`FOR`]->(o:Office) WHERE " +
          "id(p) = $personId and id(o) = $officeId " +
          "SET r.email = $email, " +
          "    r.address = $address, " +
          "    r.phoneNumber = $phoneNumber " +
          "RETURN p, r, wp, forOffice, o")
  IsInContactBookOf updateIsInContactBookOf(Long personId, Long officeId,
                                      String email, String address,
                                      String phoneNumber);
}
