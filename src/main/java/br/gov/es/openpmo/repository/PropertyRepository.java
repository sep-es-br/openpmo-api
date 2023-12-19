package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.properties.Property;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PropertyRepository extends Neo4jRepository<Property, Long> {

  @Query("MATCH (p:Property)<-[:FEATURES]-(w:Workpack) WHERE ID(w) = $idWorkpack RETURN p ")
  List<Property> findAllByIdWorkpack(@Param("idWorkpack") Long idWorkpack);

  @Query("match (ls:Property) " +
         "where id(ls)=$idLocalitySelection " +
         " OPTIONAL MATCH (ls)-[v:VALUES]->(l:Locality) " +
         "return ls, [ " +
         "    [[v,l]] " +
         "]")
  Optional<Property> findByIdWithProperties(Long idLocalitySelection);

  @Query("match (m:Property)<-[i:IS_SNAPSHOT_OF]-(s:Property)-[c:COMPOSES]->(b:Baseline)  " +
         "where id(m)=$idProperty and id(b)=$idBaseline " +
         " OPTIONAL MATCH (s)<-[v1:VALUES]->(l:Locality) " +
         " OPTIONAL MATCH (s)<-[v2:VALUES]->(o:Organization) " +
         " OPTIONAL MATCH (s)<-[v3:VALUES]->(u:UnitMeasure) " +
         "return s, [ " +
         "    [ [v1,l]],  " +
         "    [ [v2,o]],  " +
         "    [ [v3,u]] " +
         "]")
  Optional<Property> findSnapshotByMasterIdAndBaselineId(
    Long idProperty,
    Long idBaseline
  );

  @Query("match (a:Property)-[:IS_SNAPSHOT_OF]->(m:Property)<-[i:IS_SNAPSHOT_OF]-(s:Property)-[c:COMPOSES]->(b:Baseline)   " +
         "where id(a)=$idProperty and id(b)=$idBaseline  " +
         " OPTIONAL MATCH (s)<-[v1:VALUES]->(l:Locality) " +
         " OPTIONAL MATCH (s)<-[v2:VALUES]->(o:Organization) " +
         " OPTIONAL MATCH (s)<-[v3:VALUES]->(u:UnitMeasure) " +
         "return s, [  " +
         "    [ [v1,l]],   " +
         "    [ [v2,o]],   " +
         "    [ [v3,u]]  " +
         "]")
  Optional<Property> findAnotherSnapshotOfMasterBySnapshotIdAndAnotherBaselineId(
    Long idProperty,
    Long idBaseline
  );

  @Query("match (m:Property)<-[i:IS_SNAPSHOT_OF]-(s:Property) " +
         "where id(s)=$idSnapshot " +
         "return m")
  Optional<Property> findMasterBySnapshotId(Long idSnapshot);

  @Query("match (w:Workpack)<-[f:FEATURES]-(p:Property) " +
         "where id(w)=$workpackId and id(p)=$propertyId " +
         "detach delete f")
  void deleteFeaturesRelationshipByPropertyIdAndWorkpackId(
    Long workpackId,
    Long propertyId
  );

  @Query("match (p:Property) where id(p)=$propertyId  " +
         "match (pm:PropertyModel) where id(pm)=$propertyModelId " +
         "create (p)-[:IS_DRIVEN_BY]->(pm)")
  void createIsDrivenByRelationship(
    Long propertyId,
    Long propertyModelId
  );


  @Query(
    "MATCH (workpack:Workpack)-[:IS_INSTANCE_BY]->(model:WorkpackModel)-[isSortedBy:IS_SORTED_BY]->(sorterModel:PropertyModel) " +
    "MATCH (workpack)<-[features:FEATURES]-(sorter:Property)-[isDrivenBy:IS_DRIVEN_BY]->(sorterModel) " +
    "return workpack, sorter, isDrivenBy, sorterModel, model, isSortedBy"
  )
  List<Property> findAllSorterProperties();

  @Query(
    "MATCH (workpack:Workpack)<-[featuresWorkpack:FEATURES]-(property:Property)-[isDrivenBy:IS_DRIVEN_BY]->(propertyModel:PropertyModel)" +
    "WHERE id(propertyModel)=$idPropertyModel " +
    "RETURN property, workpack, featuresWorkpack, isDrivenBy, propertyModel"
  )
  List<Property> findAllByPropertyModelId(Long idPropertyModel);

  @Query("match (p:Property) where id(p)=$propertyId " +
    "match (w:Workpack) where id(w)=$workpackId " +
    "create (p)-[:FEATURES]->(w)")
  void createFeaturesRelationship(
    Long propertyId,
    Long workpackId
  );

}
