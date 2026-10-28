package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.enumerator.indicators.IndicatorLinkCategory;
import br.gov.es.openpmo.model.indicators.ProjectIndicatorLink;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectIndicatorLinkRepository extends Neo4jRepository<ProjectIndicatorLink, Long> {

    @Query("MATCH (link:ProjectIndicatorLink)-[:HAS_INDICATOR_LINK]->(workpack:Workpack) " +
        "WHERE id(workpack) = $workpackId " +
        "AND ($category IS NULL OR link.category = $category) " +
        "RETURN link, workpack ORDER BY link.name")
    List<ProjectIndicatorLink> findAllByWorkpack(final Long workpackId, final IndicatorLinkCategory category);

    @Query("MATCH (link:ProjectIndicatorLink)-[:HAS_INDICATOR_LINK]->(workpack:Workpack) " +
        "WHERE id(workpack) = $workpackId " +
        "AND link.category = $category " +
        "AND link.externalId = $externalId " +
        "RETURN link, workpack LIMIT 1")
    Optional<ProjectIndicatorLink> findByExternalIdentity(
        final Long workpackId,
        final IndicatorLinkCategory category,
        final String externalId
    );

    @Query("MATCH (link:ProjectIndicatorLink)-[:HAS_INDICATOR_LINK]->(workpack:Workpack) " +
        "WHERE id(link) = $linkId AND id(workpack) = $workpackId " +
        "RETURN link, workpack LIMIT 1")
    Optional<ProjectIndicatorLink> findByIdAndWorkpack(final Long linkId, final Long workpackId);
}
