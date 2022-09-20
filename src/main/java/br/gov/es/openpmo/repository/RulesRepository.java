package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.Rules;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RulesRepository extends Neo4jRepository<Rules, Long> {

  @Query("MATCH (customFilter:CustomFilter)<-[has:HAS]-(rules:Rules) " +
         "WHERE id(customFilter)=$id " +
         "RETURN rules, collect(customFilter), collect(has)"
  )
  List<Rules> findByCustomFilterId(Long id);

  List<Rules> findByCustomFilter(CustomFilter customFilter);

}
