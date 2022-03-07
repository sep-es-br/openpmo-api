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

    List<CustomFilter> findByType(CustomFilterEnum type);

    @Query("match (w:WorkpackModel)<-[f:FOR]-(c:CustomFilter)<-[h:HAS]-(r:Rules) " +
            "where id(w)=$workpackModelId and c.type=$filter " +
            "return w,c,r,f,h")
    List<CustomFilter> findByWorkpackModelIdAndType(Long workpackModelId, CustomFilterEnum filter);

    @Query("MATCH (customFilter:CustomFilter) WHERE ID(customFilter)=$idFilter RETURN customFilter, [" +
            "  [ (rules:Rules)-[has:HAS]->(customFilter) | [rules, has] ]," +
            "  [ (customFilter)-[for:FOR]->(workpackModel:WorkpackModel) | [for, workpackModel] ]," +
            "  [ (workpackModel)<-[feat:FEATURES]-(propertyModel:PropertyModel) | [feat, propertyModel] ]," +
            "  [ (workpackModel)<-[featGroup:FEATURES]-(groupModel:GroupModel) | [featGroup, groupModel] ]," +
            "  [ (groupModel)-[groups:GROUPS]->(groupedProperty:PropertyModel) | [groups, groupedProperty] ]" +
            "]")
    Optional<CustomFilter> findByIdWithRelationships(@Param("idFilter") Long idFilter);
}
