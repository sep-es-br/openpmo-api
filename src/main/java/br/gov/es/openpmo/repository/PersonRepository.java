package br.gov.es.openpmo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.openpmo.model.Person;

public interface PersonRepository extends Neo4jRepository<Person, Long> {

    @Query("MATCH (p:Person) WHERE p.email = $email RETURN p")
    Optional<Person> findByEmail(@Param("email") String email);

    Optional<Person> findById(Long id);

    @Query("MATCH (p:Person)-[is:CAN_ACCESS_OFFICE]->(o:Office) WHERE ID(o) = $idOffice RETURN p")
    List<Person> findByIdOfficeReturnDistinctPerson(@Param("idOffice") Long idOffice);

    @Query("MATCH (p:Person)-[is:CAN_ACCESS_PLAN]->(o:Plan) WHERE ID(o) = $idPlan RETURN p")
    List<Person> findByIdPlanReturnDistinctPerson(@Param("idPlan") Long idPlan);
    
    @Query("MATCH (p:Person)-[is:IS_STAKEHOLDER_IN]->(o:Workpack) WHERE ID(o) = $idWorkpack RETURN p")
	List<Person> findByIdWorkpackReturnDistinctPerson(@Param("idWorkpack") Long idWorkpack);
}
