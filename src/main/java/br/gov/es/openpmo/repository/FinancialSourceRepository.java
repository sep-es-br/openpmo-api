package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.budget.FinancialSource;
import java.util.Optional;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface FinancialSourceRepository extends Neo4jRepository<FinancialSource, Long> {

  @Query("MATCH (financialSource:FinancialSource) "
    + "WHERE (financialSource.typeCode = $typeCode OR (financialSource.typeCode IS NULL AND $typeCode IS NULL)) "
    + "AND (financialSource.sourceGroupCode = $sourceGroupCode OR (financialSource.sourceGroupCode IS NULL AND $sourceGroupCode IS NULL)) "
    + "AND (financialSource.sourceCode = $sourceCode OR (financialSource.sourceCode IS NULL AND $sourceCode IS NULL)) "
    + "AND (financialSource.detailedSourceCode = $detailedSourceCode OR (financialSource.detailedSourceCode IS NULL AND $detailedSourceCode IS NULL)) "
    + "RETURN financialSource LIMIT 1")
  Optional<FinancialSource> findByCodes(
    String typeCode,
    String sourceGroupCode,
    String sourceCode,
    String detailedSourceCode
  );

  @Query("MERGE (financialSource:FinancialSource {identityKey: $identityKey}) "
    + "SET financialSource.typeCode = $typeCode, "
    + "financialSource.typeName = $typeName, "
    + "financialSource.sourceGroupCode = $sourceGroupCode, "
    + "financialSource.sourceGroupName = $sourceGroupName, "
    + "financialSource.sourceCode = $sourceCode, "
    + "financialSource.sourceName = $sourceName, "
    + "financialSource.detailedSourceCode = $detailedSourceCode, "
    + "financialSource.detailedSourceName = $detailedSourceName "
    + "RETURN financialSource")
  FinancialSource mergeByIdentity(
    String identityKey,
    String typeCode,
    String typeName,
    String sourceGroupCode,
    String sourceGroupName,
    String sourceCode,
    String sourceName,
    String detailedSourceCode,
    String detailedSourceName
  );
}
