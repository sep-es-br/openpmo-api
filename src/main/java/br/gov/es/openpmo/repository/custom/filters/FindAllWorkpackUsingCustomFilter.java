package br.gov.es.openpmo.repository.custom.filters;


import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.Rules;
import br.gov.es.openpmo.repository.PropertyModelRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.custom.FindAllUsingCustomFilterBuilder;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FindAllWorkpackUsingCustomFilter extends FindAllUsingCustomFilterBuilder implements ApplyFilterUsingPropertyModel {

    private final WorkpackRepository repository;
    private final PropertyModelRepository propertyModelRepository;

    @Autowired
    public FindAllWorkpackUsingCustomFilter(final WorkpackRepository repository, final PropertyModelRepository propertyModelRepository) {
        this.repository = repository;
        this.propertyModelRepository = propertyModelRepository;
    }

    @Override
    public Session getSession() {
        return this.repository.getSession();
    }

    @Override
    public void buildMatchClause(final CustomFilter filter, final StringBuilder query) {
        query.append("MATCH (node:Workpack)-[rf:BELONGS_TO]->(p:Plan), " +
                "(p)-[is:IS_STRUCTURED_BY]->(pm:PlanModel)<-[bt:BELONGS_TO]-(wm:WorkpackModel), " +
                "(wm)<-[:FEATURES]-(propertyModel:PropertyModel), " +
                "(node)<-[:FEATURES]-(property:Property)-[:IS_DRIVEN_BY]->(propertyModel) " +
                "OPTIONAL MATCH (propertyModel)-[:GROUPS]->(groupedProperty:PropertyModel) " +
                "OPTIONAL MATCH (node)-[ii:IS_INSTANCE_BY]->(wm) " +
                "OPTIONAL MATCH (node)-[lt:IS_LINKED_TO]->(wm) " +
                "OPTIONAL MATCH (node)<-[:FEATURES]-(:Property)-[:VALUES]->(values) " +
                "WITH node, rf, p, is, pm, ii, wm, property, propertyModel, groupedProperty, " +
                "collect( property.value ) as properties, " +
                "collect( id(values) ) as selectedValues ");
    }

    @Override
    public void buildWhereClause(final CustomFilter filter, final StringBuilder query) {
        query.append("WHERE (" +
                "   id(p)=$idPlan " +
                "   AND (id(pm)=$idPlanModel OR $idPlanModel IS NULL) " +
                "   AND (id(wm)=$idWorkPackModel OR $idWorkPackModel IS NULL) " +
                ")" +
                "AND ");
    }

    @Override
    public void buildReturnClause(final StringBuilder query) {
        query.append("RETURN node, rf, p, ii, pm, wm, bt, [ ")
                .append("  [(node)<-[wi:IS_IN]-(w2:Workpack) | [wi, w2] ], ")
                .append("  [(node)-[wi2:IS_IN]->(w3:Workpack) | [wi2, w3] ], ")
                .append("  [(node)<-[wa:APPLIES_TO]-(ca:CostAccount) | [wa, ca] ], ")
                .append("  [(node)<-[wfg:FEATURES]-(wg:Group) | [wfg, wg] ], ")
                .append("  [(wg)-[wgps:GROUPS]->(wgp:Property)-[gpd:IS_DRIVEN_BY]->(pm:PropertyModel) | [wgps, wgp, gpd, pm] ], ")
                .append("  [(ca)<-[f1:FEATURES]-(p2:Property)-[d1:IS_DRIVEN_BY]->(pmc:PropertyModel) | [ca, f1, p2, d1, pmc ] ], ")
                .append("  [(wm)<-[wmi:IS_IN]-(wm2:WorkpackModel) | [wmi,wm2] ], ")
                .append("  [(wm)-[wmi2:IS_IN]->(wm3:WorkpackModel) | [wmi2,wm3] ], ")
                .append("  [(wm)<-[f2:FEATURES]-(pm2:PropertyModel) | [f2, pm2] ], ")
                .append("  [(wm)-[fg:FEATURES]->(gp:GroupModel) | [fg, gp] ], ")
                .append("  [(node)-[isLinkedTo:IS_LINKED_TO]->(workpackModel:WorkpackModel) | [isLinkedTo, workpackModel] ], ")
                .append("  [(wp)-[parentSharedWith]->(officeParent:Office) | [parentSharedWith, officeParent]], ")
                .append("  [(gp)-[gps:GROUPS]->(gpm:PropertyModel) | [gps, gpm] ] ")
                .append("] ");
    }

    @Override
    protected String buildCustomFilterRule(final Rules rule, final String label) {
        return this.buildFilterRuleForWorkpack(rule, label);
    }

    @Override
    protected void buildOrderingAndDirectionClause(final CustomFilter filter, final StringBuilder query) {
        this.buildOrderingAndDirectionClauseForWorkpack(filter, query);
    }

    @Override
    public String[] getDefinedExternalParams() {
        return new String[]{"idPlan", "idPlanModel", "idWorkPackModel"};
    }

    @Override
    public String getNodeName() {
        return this.nodeName;
    }

    @Override
    public void setNodeName(final String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public PropertyModelRepository getPropertyModelRepository() {
        return this.propertyModelRepository;
    }
}
