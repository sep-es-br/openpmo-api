package br.gov.es.openpmo.service.workpack.breakdown.structure;

import br.gov.es.openpmo.dto.menu.WorkpackResultDto;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.PlanBreakdownStructure;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.WorkpackBreakdownStructure;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.WorkpackModelBreakdownStructure;
import br.gov.es.openpmo.dto.workpack.breakdown.structure.WorkpackModelRepresentation;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.service.office.plan.PlanService;
import br.gov.es.openpmo.utils.ApplicationCacheUtil;
import org.springframework.stereotype.Component;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class GetPlanBreakdownStructure {

  private final ApplicationCacheUtil cacheUtil;

  private final GetWorkpackBreakdownStructure getWorkpackBreakdownStructure;

  private final PlanService planService;

  private final Collator collator;

  public GetPlanBreakdownStructure(
    final ApplicationCacheUtil cacheUtil,
    final GetWorkpackBreakdownStructure getWorkpackBreakdownStructure,
    final PlanService planService
  ) {
    this.cacheUtil = cacheUtil;
    this.getWorkpackBreakdownStructure = getWorkpackBreakdownStructure;
    this.planService = planService;
    this.collator = Collator.getInstance();
    this.collator.setStrength(Collator.PRIMARY);
  }

  public PlanBreakdownStructure execute(
    final Long idPlan,
    final Boolean allLevels,
    final String authorization
  ) {
    final Plan plan = this.planService.findById(idPlan);
    final List<WorkpackResultDto> roots = this.cacheUtil.getPlanRootWorkpacks(idPlan);
    final Map<Long, WorkpackModelBreakdownStructure> workpackModels = new LinkedHashMap<>();

    roots.forEach(root -> {
      final WorkpackBreakdownStructure workpack = this.getWorkpackBreakdownStructure.execute(
        root.getId(),
        allLevels,
        idPlan,
        authorization
      );
      if (workpack == null) {
        return;
      }

      final WorkpackModelBreakdownStructure model = workpackModels.computeIfAbsent(
        root.getIdWorkpackModel(),
        ignored -> this.createModel(root)
      );
      model.getWorkpacks().add(workpack);
    });

    final List<WorkpackModelBreakdownStructure> models = new ArrayList<>(workpackModels.values());
    models.sort(
      Comparator.comparing(WorkpackModelBreakdownStructure::getRepresentationPosition)
        .thenComparing((first, second) -> this.collator.compare(first.getName(), second.getName()))
    );

    final PlanBreakdownStructure structure = new PlanBreakdownStructure();
    structure.setIdPlan(idPlan);
    structure.setPlanName(plan.getFullName());
    structure.setWorkpackModels(models);
    return structure;
  }

  private WorkpackModelBreakdownStructure createModel(final WorkpackResultDto root) {
    final WorkpackModelRepresentation representation = new WorkpackModelRepresentation();
    representation.setIdWorkpackModel(root.getIdWorkpackModel());
    representation.setWorkpackModelName(root.getModelNameInPlural());
    representation.setWorkpackModelType(root.getType());
    representation.setWorkpackModelPosition(root.getPosition());

    final WorkpackModelBreakdownStructure model = new WorkpackModelBreakdownStructure();
    model.setRepresentation(representation);
    model.setWorkpacks(new ArrayList<>());
    return model;
  }
}
