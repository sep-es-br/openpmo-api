package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.stakeholder.StakeholderAndPermissionQuery;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsStakeholderIn;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface StakeholderRepository extends Neo4jRepository<IsStakeholderIn, Long> {

  @Query("MATCH (workpack:Workpack) " +
         "OPTIONAL MATCH (actor:Actor)-[isStakeholderIn:IS_STAKEHOLDER_IN]->(workpack) " +
         "OPTIONAL MATCH (person:Person)-[canAccessWorkpack:CAN_ACCESS_WORKPACK]->(workpack) " +
         "WITH *, collect(actor) + collect(person) as actors " +
         "WHERE id(workpack)=$idWorkpack " +
         "RETURN collect(actors), " +
         "  collect(isStakeholderIn) AS stakeholderIn, " +
         "  collect(workpack), " +
         "  collect(canAccessWorkpack) AS workpackPermissions"
  )
  StakeholderAndPermissionQuery findByIdWorkpack(
    @Param("idWorkpack") Long idWorkpack
  );

  @Query("MATCH (p:Actor)-[is:IS_STAKEHOLDER_IN]->(o:Workpack) " +
    "WHERE id(o)=$idWorkpack AND ($idActor IS NULL OR id(p)=$idActor) " +
    "RETURN p,o,is")
  List<IsStakeholderIn> findByIdWorkpackAndIdActor(
    @Param("idWorkpack") Long idWorkpack,
    @Param("idActor") Long idActor
  );

  @Query("MATCH (p:Person)-[is:IS_STAKEHOLDER_IN]->(o:Workpack) " +
    "WHERE id(o)=$idWorkpack AND id(p)=$idPerson " +
    "RETURN p,o,is")
  List<IsStakeholderIn> findByIdWorkpackAndIdPerson(
    @Param("idWorkpack") Long idWorkpack,
    @Param("idPerson") Long idPerson
  );

  @Query("MATCH (p:Person)-[is:IS_STAKEHOLDER_IN]->(o:Workpack) " +
    "WHERE id(o)=$idWorkpack AND id(p)=$idPerson " +
    "RETURN count(is)>0")
  boolean existsByIdWorkpackAndIdPerson(
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

  @Query("MATCH (a:Actor) where id(a) = $actorId " +
          "MATCH (w:Workpack) where id(w)= $workpackId " +
          "           CREATE (a)-[r:IS_STAKEHOLDER_IN { " +
          "             role: $role, " +
          "             active: $active " +
          "           }]->(w) " +
          "           WITH r " +
          "           WHERE $from IS NOT NULL " +
          "           SET r.from = $from " +
          "           WITH r " +
          "           WHERE $to IS NOT NULL " +
          "           SET r.to = $to " +
          "           RETURN r")
  IsStakeholderIn createIsStakeholderIn(Long actorId, Long workpackId,
                                        String role, LocalDate from, LocalDate to,
                                        boolean active);

  @Query("MATCH (a:Actor)-[r:IS_STAKEHOLDER_IN]->(w:Workpack) " +
          "WHERE id(a) = $actorId AND id(w) = $workpackId AND id(r) = $relationId " +
          "SET r.from = $from, " +
          "r.to = $to, " +
          "r.role = $role, " +
          "r.active = $active " +
          "RETURN r")
  IsStakeholderIn updateIsStakeholderIn(Long actorId, Long workpackId, Long relationId,
                                        String role, LocalDate from, LocalDate to,
                                        boolean active);

}
