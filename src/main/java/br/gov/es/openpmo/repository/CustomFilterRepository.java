package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.CustomFilterEnum;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomFilterRepository extends Neo4jRepository<CustomFilter, Long> {

  @Query("MATCH (person:Person)-[has1:HAS]->(customFilter:CustomFilter) " +
         "OPTIONAL MATCH (customFilter)<-[has2:HAS]-(rules:Rules) " +
         "WITH person, has1, customFilter, has2, rules " +
         "WHERE ID(person)=$idPerson and customFilter.type=$type " +
         "RETURN person, has1, customFilter, has2, rules")
  List<CustomFilter> findByType(
    @Param("type") CustomFilterEnum type,
    @Param("idPerson") Long idPerson
  );

  @Query("MATCH (person:Person)-[has1:HAS]->(customFilter:CustomFilter {favorite: true} ) " +
         "MATCH (customFilter)-[:FOR]->(workpackModel:WorkpackModel)<-[:IS_INSTANCE_BY]-(workpack:Workpack) " +
         "OPTIONAL MATCH (customFilter)<-[has2:HAS]-(rules:Rules) " +
         "WITH * " +
         "WHERE ID(person)=$idPerson and id(workpack)=$idWorkpack " +
         "RETURN person, has1, customFilter, has2, rules")
  Optional<CustomFilter> findDefaultByTypeAndWorkpackId(
    @Param("idPerson") Long idPerson,
    @Param("idWorkpack") Long idWorkpack
  );


  @Query("MATCH (workpackModel:WorkpackModel)<-[for:FOR]-(customFilter:CustomFilter)" +
         "MATCH (customFilter)<-[:HAS]-(person:Person) " +
         "OPTIONAL MATCH (customFilter)<-[has:HAS]-(rules:Rules) " +
         "WITH workpackModel, for, customFilter, person, has, rules " +
         "WHERE ID(workpackModel)=$workpackModelId and customFilter.type=$filter and ID(person)=$idPerson " +
         "RETURN workpackModel,customFilter,rules,for,has")
  List<CustomFilter> findByWorkpackModelIdAndType(
    @Param("workpackModelId") Long workpackModelId,
    @Param("filter") CustomFilterEnum filter,
    @Param("idPerson") Long idPerson
  );

  @Query("MATCH (customFilter:CustomFilter) WHERE ID(customFilter)=$idFilter RETURN customFilter, [" +
         "  [ (rules:Rules)-[has:HAS]->(customFilter) | [rules, has] ]," +
         "  [ (customFilter)-[for:FOR]->(workpackModel:WorkpackModel) | [for, workpackModel] ]," +
         "  [ (customFilter)-[:FOR]->(:WorkpackModel)<-[feat:FEATURES]-(propertyModel:PropertyModel) | [feat, propertyModel] ]," +
         "  [ (customFilter)-[:FOR]->(:WorkpackModel)<-[featGroup:FEATURES]-(groupModel:GroupModel) | [featGroup, groupModel] ]," +
         "  [ (customFilter)-[:FOR]->(:WorkpackModel)<-[:FEATURES]-(:GroupModel)-[groups:GROUPS]->(groupedProperty:PropertyModel) | [groups, groupedProperty] ]" +
         "]")
  Optional<CustomFilter> findByIdWithRelationships(@Param("idFilter") Long idFilter);

  @Query("MATCH (person:Person)-[has1:HAS]->(customFilter:CustomFilter) " +
         "OPTIONAL MATCH (customFilter:CustomFilter)<-[has2:HAS]-(rules:Rules) " +
         "WITH person, has1, customFilter, has2, rules " +
         "WHERE ID(person)=$idPerson and ID(customFilter)=$id " +
         "RETURN person, has1, customFilter, has2, rules")
  Optional<CustomFilter> findByIdAndPersonId(
    @Param("id") Long id,
    @Param("idPerson") Long idPerson
  );

}
