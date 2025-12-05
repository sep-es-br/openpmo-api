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

    @Query("MATCH (model:ProjectModel) " +
        "WHERE id(model) = $projectModelId " +
        "  AND model.notificationsEventScheduleEnabled = true " +
        "MATCH (model)<-[:IS_INSTANCE_BY]-(p:Project {canceled: false, deleted: false, completed: false}) " +
        "MATCH (p)<-[rel:IS_STAKEHOLDER_IN]-(person:Person) " +
        "WHERE rel.active = true " +
        "  AND (rel.from IS NULL OR date(rel.from) <= date()) " +
        "  AND (rel.to IS NULL OR date(rel.to) >= date()) " +
        "  AND any(role IN model.notificationsSelectedRoles " +
        "          WHERE toLower(role) = toLower(rel.role)) " +
        "OPTIONAL MATCH (p)-[:BELONGS_TO]->(:Plan)-[:IS_ADOPTED_BY]->(office:Office) " +
        "OPTIONAL MATCH (person)-[c:IS_IN_CONTACT_BOOK_OF]->(office) " +
        "OPTIONAL MATCH (person)-[auth:IS_AUTHENTICATED_BY]->(:AuthService) " +
        "OPTIONAL MATCH (p)<-[:FEATURES]-(pProp:Property)-[:IS_DRIVEN_BY]->(pPM:PropertyModel) " +
        "WHERE pPM.name IN ['Status', 'Situação'] " +
        "OPTIONAL MATCH (p)<-[:IS_IN*]-(d:Deliverable {canceled: false, deleted: false, completed: false})<-[:FEATURES]-(sc:Schedule) " +
        "WHERE sc IS NOT NULL " +
        "  AND date({ " +
        "        year: date(sc.start).year, " +
        "        month: date(sc.start).month, " +
        "        day: 1 " +
        "      }) <= date({ " +
        "        year: date().year, " +
        "        month: date().month, " +
        "        day: 1 " +
        "      }) " +
        "  AND date({ " +
        "        year: date(sc.end).year, " +
        "        month: date(sc.end).month, " +
        "        day: 1 " +
        "      }) >= date({ " +
        "        year: date().year, " +
        "        month: date().month, " +
        "        day: 1 " +
        "      }) " +
        "WITH person.fullName AS fullName, " +
        "     CASE " +
        "         WHEN c.email IS NOT NULL AND trim(c.email) <> '' THEN c.email " +
        "         WHEN auth.email IS NOT NULL AND trim(auth.email) <> '' THEN auth.email " +
        "         ELSE NULL " +
        "     END AS email, " +
        "     p AS project, " +
        "     pProp.value AS projectStatus, " +
        "     collect(DISTINCT d) AS deliverables " +
        "WHERE size(deliverables) > 0 " +
        "WITH fullName, email, projectStatus, project, " +
        "     [d IN deliverables | { name: d.name, fullName: d.fullName }] AS deliverableNames " +
        "RETURN fullName, email, collect(DISTINCT { projectName: project.name, projectFullName: project.fullName, status: projectStatus, items: deliverableNames }) AS projects " +
        "ORDER BY fullName")
    List<NotificationResultDto> fetchScheduleNotificationData(@Param("projectModelId") Long projectModelId);

    @Query("MATCH (model:ProjectModel) " +
       "WHERE id(model) = $projectModelId " +
       "  AND model.notificationsEventMilestoneEnabled = true " +
       "MATCH (model)<-[:IS_INSTANCE_BY]-(p:Project {canceled: false, deleted: false, completed: false}) " +
       "MATCH (p)<-[rel:IS_STAKEHOLDER_IN]-(person:Person) " +
       "WHERE rel.active = true " +
       "  AND (rel.from IS NULL OR date(rel.from) <= date()) " +
       "  AND (rel.to IS NULL OR date(rel.to) >= date()) " +
       "  AND any(role IN model.notificationsSelectedRoles " +
       "          WHERE toLower(role) = toLower(rel.role)) " +
       "OPTIONAL MATCH (p)-[:BELONGS_TO]->(:Plan)-[:IS_ADOPTED_BY]->(office:Office) " +
       "OPTIONAL MATCH (person)-[c:IS_IN_CONTACT_BOOK_OF]->(office) " +
       "OPTIONAL MATCH (person)-[auth:IS_AUTHENTICATED_BY]->(:AuthService) " +
       "OPTIONAL MATCH (p)<-[:FEATURES]-(pProp:Property)-[:IS_DRIVEN_BY]->(pPM:PropertyModel) " +
       "WHERE pPM.name IN ['Status', 'Situação'] " +
       "OPTIONAL MATCH (p)<-[:IS_IN*]-(m:Milestone {canceled: false, deleted: false}) " +
       "WHERE m.canceled = false " +
       "  AND m.deleted = false " +
       "  AND (m.completed = false OR m.completed IS NULL) " +
       "  AND date(substring(m.date, 0, 10)) - duration({days: model.notificationsEventMilestoneDaysBefore}) = date() " +
       "WITH person.fullName AS fullName, " +
       "     CASE " +
       "         WHEN c.email IS NOT NULL AND trim(c.email) <> '' THEN c.email " +
       "         WHEN auth.email IS NOT NULL AND trim(auth.email) <> '' THEN auth.email " +
       "         ELSE NULL " +
       "     END AS email, " +
       "     p AS project, " +
       "     pProp.value AS projectStatus, " +
       "     collect(DISTINCT m) AS milestones " +
       "WHERE size(milestones) > 0 " +
       "WITH fullName, email, projectStatus, project, " +
       "     [m IN milestones | { name: m.name, fullName: m.fullName }] AS milestoneNames " +
       "RETURN fullName, email, collect(DISTINCT { " +
       "    projectName: project.name, " +
       "    projectFullName: project.fullName, " +
       "    status: projectStatus, " +
       "    items: milestoneNames " +
       "}) AS projects " +
       "ORDER BY fullName")
    List<NotificationResultDto> fetchMilestoneNotificationData(
            @Param("projectModelId") Long projectModelId);



}
