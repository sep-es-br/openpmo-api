package br.gov.es.openpmo.repository.dashboards;

import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetStakeholderQueryResult;
import br.gov.es.openpmo.dto.dashboards.datasheet.WorkpackByModelQueryResult;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DashboardDatasheetRepository extends Neo4jRepository<Workpack, Long> {

    @Query("match (w:Workpack{deleted:false})<-[:IS_IN*]-(v:Workpack{deleted:false})-[:IS_INSTANCE_BY]->(m:WorkpackModel) " +
            "where id(w)=$workpackId " +
            "with v, collect(distinct m) as modelList " +
            "with v, collect([n in modelList where id(n)=v.idWorkpackModel][0]) as modelGroup " +
            "unwind modelGroup as models " +
            "return id(models), " +
            "    count(v) as quantity, " +
            "    models.modelName as singularName, " +
            "    models.modelNameInPlural as pluralName, " +
            "    models.fontIcon as icon")
    List<WorkpackByModelQueryResult> workpackByModel(Long workpackId);

    @Query("match (a:Actor)-[s:IS_STAKEHOLDER_IN]->(w:Workpack{deleted:false}) " +
            "optional match (w)-[:IS_IN*]->(v:Workpack{deleted:false}) " +
            "optional match (a)<-[:IS_A_PORTRAIT_OF]-(file:File) " +
            "with a,s,w,v,file " +
            "where " +
            "    ( " +
            "        (a)-[s]->(w) and id(w)=$workpackId " +
            "    ) " +
            "    or " +
            "    ( " +
            "        (a)-[s]->(w)-[:IS_IN*]->(v:Workpack{deleted:false}) and id(v)=$workpackId " +
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
