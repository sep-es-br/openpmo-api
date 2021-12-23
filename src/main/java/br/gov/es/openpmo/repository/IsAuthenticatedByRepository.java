package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.relations.IsAuthenticatedBy;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface IsAuthenticatedByRepository extends Neo4jRepository<IsAuthenticatedBy, Long> {


  @Query("MATCH (person:Person)-[authenticatedBY:IS_AUTHENTICATED_BY]->(authService:AuthService) " +
         "WHERE id(person)=$idPerson AND toLower(authService.server)=toLower($authenticationServiceName) " +
         "RETURN person, authenticatedBY, authService"
  )
  Optional<IsAuthenticatedBy> findAuthenticatedByUsingPersonAndDefaultServerName(Long idPerson, String authenticationServiceName);
}
