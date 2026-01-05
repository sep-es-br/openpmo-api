package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.dto.NotificationResultDto;
import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import org.springframework.stereotype.Repository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ProjectModelRepository extends Neo4jRepository<ProjectModel, Long> {

    @Query("MATCH (p:ProjectModel) WHERE p.notificationsSessionActive = true RETURN p")
    List<ProjectModel> findAllWithNotificationsSessionActive();

    @Query(
        "MATCH (model:ProjectModel) " +
        "WHERE id(model) = $projectModelId " +
        "  AND model.notificationsEventScheduleEnabled = true " +
        "MATCH (model)<-[:IS_INSTANCE_BY]-(p:Project { " +
        "  canceled: false, deleted: false, completed: false " +
        "}) " +
        "MATCH (p)<-[rel:IS_STAKEHOLDER_IN]-(person:Person) " +
        "WHERE rel.active = true " +
        "  AND (rel.from IS NULL OR date(rel.from) <= date()) " +
        "  AND (rel.to IS NULL OR date(rel.to) >= date()) " +
        "  AND any(role IN model.notificationsSelectedRoles " +
        "          WHERE toLower(role) = toLower(rel.role)) " +
        "OPTIONAL MATCH (p)-[:BELONGS_TO]->(plan:Plan)-[:IS_ADOPTED_BY]->(office:Office) " +
        "OPTIONAL MATCH (person)-[c:IS_IN_CONTACT_BOOK_OF]->(office) " +
        "OPTIONAL MATCH (person)-[auth:IS_AUTHENTICATED_BY]->(:AuthService) " +
        "OPTIONAL MATCH (p)<-[:FEATURES]-(pProp:Property)-[:IS_DRIVEN_BY]->(pPM:PropertyModel) " +
        "WHERE pPM.name IN ['Status', 'Situação'] " +
        "OPTIONAL MATCH (p)<-[:IS_IN*]-(d:Deliverable { " +
        "  canceled: false, deleted: false, completed: false " +
        "})<-[:FEATURES]-(sc:Schedule) " +
        "WHERE sc IS NOT NULL " +
        "  AND date({ year: date(sc.start).year, month: date(sc.start).month, day: 1 }) " +
        "      <= date({ year: (date() - duration('P1M')).year, month: (date() - duration('P1M')).month, day: 1 }) " +
        "  AND date({ year: date(sc.end).year, month: date(sc.end).month, day: 1 }) " +
        "      >= date({ year: (date() - duration('P1M')).year, month: (date() - duration('P1M')).month, day: 1 }) " +
        "OPTIONAL MATCH (d)-[:IS_INSTANCE_BY]->(dModel:DeliverableModel) " +
        "WITH person.fullName AS fullName, " +
        "     CASE " +
        "       WHEN c.email IS NOT NULL AND trim(c.email) <> '' THEN c.email " +
        "       WHEN auth.email IS NOT NULL AND trim(auth.email) <> '' THEN auth.email " +
        "       ELSE NULL " +
        "     END AS email, " +
        "     model.modelName AS projectModelName, " +
        "     id(p) AS projectId, " +
        "     id(plan) AS planId, " +
        "     p.name AS projectName, " +
        "     p.fullName AS projectFullName, " +
        "     pProp.value AS projectStatus, " +
        "     d, " +
        "     id(d) AS deliverableId, " + 
        "     dModel.modelName AS deliverableModelName " +
    
        "WHERE d IS NOT NULL " +
        "WITH fullName, email, projectModelName, projectId, planId, projectName, projectFullName, projectStatus, " +
        "     deliverableModelName, " +
        "     collect({ " +
        "       id: deliverableId, " +
        "       name: d.name, " +
        "       fullName: d.fullName " +
        "     }) AS deliverableItems " +
        "WITH fullName, email, projectModelName, projectId, planId, projectName, projectFullName, projectStatus, " +
        "     collect({ modelName: deliverableModelName, items: deliverableItems }) AS itemsByModel " +
        "RETURN fullName, email, " +
        "       collect(DISTINCT { " +
        "         id: projectId, " +               
        "         planId: planId, " +               
        "         modelName: projectModelName, " +
        "         projectName: projectName, " +
        "         projectFullName: projectFullName, " +
        "         status: projectStatus, " +
        "         items: itemsByModel " +
        "       }) AS projects " +
        "ORDER BY fullName"
    )
    List<NotificationResultDto> fetchScheduleNotificationData(
        @Param("projectModelId") Long projectModelId
    );
    
    

    @Query(
        "MATCH (model:ProjectModel) " +
        "WHERE id(model) = $projectModelId " +
        "  AND model.notificationsEventMilestoneEnabled = true " +
        "MATCH (model)<-[:IS_INSTANCE_BY]-(p:Project { " +
        "  canceled: false, deleted: false, completed: false " +
        "}) " +
        "MATCH (p)<-[rel:IS_STAKEHOLDER_IN]-(person:Person) " +
        "WHERE rel.active = true " +
        "  AND (rel.from IS NULL OR date(rel.from) <= date()) " +
        "  AND (rel.to IS NULL OR date(rel.to) >= date()) " +
        "  AND any(role IN model.notificationsSelectedRoles " +
        "          WHERE toLower(role) = toLower(rel.role)) " +
        "OPTIONAL MATCH (p)-[:BELONGS_TO]->(plan:Plan)-[:IS_ADOPTED_BY]->(office:Office) " +
        "OPTIONAL MATCH (person)-[c:IS_IN_CONTACT_BOOK_OF]->(office) " +
        "OPTIONAL MATCH (person)-[auth:IS_AUTHENTICATED_BY]->(:AuthService) " +
        "OPTIONAL MATCH (p)<-[:FEATURES]-(pProp:Property)-[:IS_DRIVEN_BY]->(pPM:PropertyModel) " +
        "WHERE pPM.name IN ['Status', 'Situação'] " +
        "OPTIONAL MATCH (p)<-[:IS_IN*]-(m:Milestone { " +
        "  canceled: false, deleted: false " +
        "}) " +
        "WHERE (m.completed = false OR m.completed IS NULL) " +
        "  AND date(substring(m.date, 0, 10)) " +
        "      - duration({ days: model.notificationsEventMilestoneDaysBefore }) = date() " +
        "OPTIONAL MATCH (m)-[:IS_INSTANCE_BY]->(mModel:MilestoneModel) " +
        "WITH person.fullName AS fullName, " +
        "     CASE " +
        "       WHEN c.email IS NOT NULL AND trim(c.email) <> '' THEN c.email " +
        "       WHEN auth.email IS NOT NULL AND trim(auth.email) <> '' THEN auth.email " +
        "       ELSE NULL " +
        "     END AS email, " +
        "     model.modelName AS projectModelName, " +
        "     id(p) AS projectId, " +
        "     id(plan) AS planId, " +
        "     p.name AS projectName, " +
        "     p.fullName AS projectFullName, " +
        "     pProp.value AS projectStatus, " +
        "     m, " +
        "     id(m) AS milestoneId, " +
        "     mModel.modelName AS milestoneModelName " +
        "WHERE m IS NOT NULL " +
        "WITH fullName, email, projectModelName, projectId, planId, projectName, projectFullName, projectStatus, " +
        "     milestoneModelName, " +
        "     collect({ " +
        "       id: milestoneId, " +
        "       name: m.name, " +
        "       fullName: m.fullName " +
        "     }) AS milestoneItems " +
        "WITH fullName, email, projectModelName, projectId, planId, projectName, projectFullName, projectStatus, " +
        "     collect({ modelName: milestoneModelName, items: milestoneItems }) AS itemsByModel " +
    
        "RETURN fullName, email, " +
        "       collect(DISTINCT { " +
        "         id: projectId, " +
        "         planId: planId, " +
        "         modelName: projectModelName, " +
        "         projectName: projectName, " +
        "         projectFullName: projectFullName, " +
        "         status: projectStatus, " +
        "         items: itemsByModel " +
        "       }) AS projects " +
        "ORDER BY fullName"
    )
    List<NotificationResultDto> fetchMilestoneNotificationData(
        @Param("projectModelId") Long projectModelId
    );
    

}
