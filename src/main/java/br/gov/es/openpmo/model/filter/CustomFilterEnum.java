package br.gov.es.openpmo.model.filter;

import br.gov.es.openpmo.dto.stakeholder.StakeholderAndPermissionQuery;
import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.issue.Issue;
import br.gov.es.openpmo.model.office.Domain;
import br.gov.es.openpmo.model.office.Locality;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.UnitMeasure;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.process.Process;
import br.gov.es.openpmo.model.relations.CanAccessOffice;
import br.gov.es.openpmo.model.relations.CanAccessPlan;
import br.gov.es.openpmo.model.risk.Risk;
import br.gov.es.openpmo.model.workpacks.Workpack;

public enum CustomFilterEnum {
  OFFICE("Office", Office.class),
  PLAN("Plan", Plan.class),
  LOCALITY("Locality", Locality.class),
  DOMAIN("Domain", Domain.class),
  ORGANIZATION("Organization", Organization.class),
  PLAN_MODELS("PlanModel", PlanModel.class),
  OFFICE_PERMISSIONS("CanAccessOffice", CanAccessOffice.class),
  PLAN_PERMISSIONS("CanAccessPlan", CanAccessPlan.class),
  UNIT_MEASURES("UnitMeasure", UnitMeasure.class),
  WORKPACK("Workpack", Workpack.class),
  COST_ACCOUNT("CostAccount", Workpack.class),
  RISK("Risk", Risk.class),
  STAKEHOLDER("IsStakeholderIn", StakeholderAndPermissionQuery.class),
  ISSUE("Issue", Issue.class),
  PROCESS("Process", Process.class);

  private final String nodeName;
  private final Class<?> nodeClass;

  <T> CustomFilterEnum(
    final String type,
    final Class<T> nodeClass
  ) {
    this.nodeName = type;
    this.nodeClass = nodeClass;
  }

  public String getNodeName() {
    return this.nodeName;
  }

  public <T> Class<T> getNodeClass() {
    return (Class<T>) this.nodeClass;
  }
}
