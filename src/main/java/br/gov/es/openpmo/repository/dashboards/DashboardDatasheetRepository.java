package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.datasheet.ChildrenByTypeQueryResult;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetStakeholderQueryResult;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DashboardDatasheetRepository extends Neo4jRepository<Workpack, Long> {

    @Query("match (w:Workpack)<-[:IS_IN*]-(v:Workpack) " +
            "where id(w)=$workpackId " +
            "with v, collect([x in labels(v) where x<>'Workpack'][0]) as label " +
            "unwind label as type " +
            "return count(v) as quantity, type")
    List<ChildrenByTypeQueryResult> childrenByType(Long workpackId);

    @Query("match (a:Actor)-[s:IS_STAKEHOLDER_IN]->(w:Workpack) " +
            "optional match (w)-[:IS_IN*]->(v:Workpack) " +
            "optional match (a)<-[:IS_A_PORTRAIT_OF]-(file:File) " +
            "with a,s,w,v,file " +
            "where " +
            "    ( " +
            "        (a)-[s]->(w) and id(w)=$workpackId " +
            "    ) " +
            "    or " +
            "    ( " +
            "        (a)-[s]->(w)-[:IS_IN*]->(v:Workpack) and id(v)=$workpackId " +
            "    ) " +
            "return " +
            "    distinct id(a) as id, " +
            "    a.name as name, " +
            "    a.fullName as fullName, " +
            "    s.role as role, " +
            "    file, " +
            "    'Organization' in labels(a) as organization")
    Set<DatasheetStakeholderQueryResult> stakeholders(Long workpackId);

}
