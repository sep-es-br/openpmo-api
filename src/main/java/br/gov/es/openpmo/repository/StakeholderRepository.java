package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.stakeholder.StakeholderAndPermissionQuery;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsStakeholderIn;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface StakeholderRepository extends Neo4jRepository<IsStakeholderIn, Long> {

  @Query("MATCH (workpack:Workpack) " +
         "OPTIONAL MATCH (actor:Actor)-[isStakeholderIn:IS_STAKEHOLDER_IN]->(workpack) " +
         "OPTIONAL MATCH (person:Person)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack) " +
         "WITH actor, isStakeholderIn, workpack, person, canAccessWorkpack " +
         "WHERE id(workpack)=$idWorkpack " +
         "RETURN collect(actor), collect(isStakeholderIn) AS stakeholderIn, collect(workpack), " +
         "collect(person), collect(canAccessWorkpack) AS workpackPermissions")
  StakeholderAndPermissionQuery findByIdWorkpack(@Param("idWorkpack") Long idWorkpack);

  @Query("MATCH (p:Actor)-[is:IS_STAKEHOLDER_IN]->(o:Workpack) WHERE id(o) = $idWorkpack AND (id(p) = $idActor OR $idActor IS " +
         "NULL) RETURN p,o,is")
  List<IsStakeholderIn> findByIdWorkpackAndIdActor(
    @Param("idWorkpack") Long idWorkpack,
    @Param("idActor") Long idActor
  );

  @Query("MATCH (p:Person)-[is:IS_STAKEHOLDER_IN]->(o:Workpack) WHERE id(o) = $idWorkpack AND (id(p) = $idPerson) RETURN p,o,is")
  List<IsStakeholderIn> findByIdWorkpackAndIdPerson(
    @Param("idWorkpack") Long idWorkpack,
    @Param("idPerson") Long idPerson
  );

  @Query("MATCH (workpack:Workpack) " +
         "OPTIONAL MATCH (person:Person)-[stakeholderIn:IS_STAKEHOLDER_IN]->(workpack)  " +
         "OPTIONAL MATCH (workpack)-[isIn:IS_IN*]->(parent:Workpack) " +
         "OPTIONAL MATCH (parent)<-[inheritedStakeholderIn:IS_STAKEHOLDER_IN]-(inheritedStakeholder:Person) " +
         "WITH person, stakeholderIn, workpack, isIn, parent, inheritedStakeholderIn, inheritedStakeholder " +
         "WHERE id(workpack)=$idWorkpack " +
         "RETURN person, inheritedStakeholder")
  Set<Person> findStakeholdersAndAscendingByWorkpackId(Long idWorkpack);

}
