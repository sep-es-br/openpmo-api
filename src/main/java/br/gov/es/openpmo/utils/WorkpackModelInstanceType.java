package br.gov.es.openpmo.utils;

import br.gov.es.openpmo.model.workpacks.models.DeliverableModel;
import br.gov.es.openpmo.model.workpacks.models.MilestoneModel;
import br.gov.es.openpmo.model.workpacks.models.OrganizerModel;
import br.gov.es.openpmo.model.workpacks.models.PortfolioModel;
import br.gov.es.openpmo.model.workpacks.models.ProgramModel;
import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum WorkpackModelInstanceType {

  TYPE_NAME_MODEL_PORTFOLIO(
    "br.gov.es.openpmo.model.workpacks.models.PortfolioModel",
    "PortfolioModel",
    PortfolioModel::new,
    PortfolioModel.class
  ),
  TYPE_NAME_MODEL_PROGRAM(
    "br.gov.es.openpmo.model.workpacks.models.ProgramModel",
    "ProgramModel",
    ProgramModel::new,
    ProgramModel.class
  ),
  TYPE_NAME_MODEL_ORGANIZER(
    "br.gov.es.openpmo.model.workpacks.models.OrganizerModel",
    "OrganizerModel",
    OrganizerModel::new,
    OrganizerModel.class
  ),
  TYPE_NAME_MODEL_DELIVERABLE(
    "br.gov.es.openpmo.model.workpacks.models.DeliverableModel",
    "DeliverableModel",
    DeliverableModel::new,
    DeliverableModel.class
  ),
  TYPE_NAME_MODEL_PROJECT(
    "br.gov.es.openpmo.model.workpacks.models.ProjectModel",
    "ProjectModel",
    ProjectModel::new,
    ProjectModel.class
  ),
  TYPE_NAME_MODEL_MILESTONE(
    "br.gov.es.openpmo.model.workpacks.models.MilestoneModel",
    "MilestoneModel",
    MilestoneModel::new,
    MilestoneModel.class
  );

  private final String className;

  private final String shortName;

  private final Supplier<? extends WorkpackModel> instanceSupplier;

  private final Class<? extends WorkpackModel> workpackClassType;

  WorkpackModelInstanceType(
    final String className,
    String shortName,
    final Supplier<WorkpackModel> instance,
    final Class<? extends WorkpackModel> workpackClassType
  ) {
    this.className = className;
    this.shortName = shortName;
    this.instanceSupplier = instance;
    this.workpackClassType = workpackClassType;
  }

  public static WorkpackModel createFrom(final WorkpackModel workpackModel) {
    for (final WorkpackModelInstanceType type : values()) {
      if (type.isTypeOf(workpackModel)) {
        return type.instanceSupplier.get();
      }
    }
    throw new IllegalArgumentException(ApplicationMessage.WORKPACK_MODEL_TYPE_MISMATCH);
  }

  public boolean isTypeOf(final WorkpackModel model) {
    return this.getClassName().equals(model.getClass().getTypeName());
  }

  public String getClassName() {
    return this.className;
  }

  public String getShortName() {
    return this.shortName;
  }

  public static Collection<String> allTypeClassName() {
    return Arrays.stream(values())
      .map(WorkpackModelInstanceType::getClassName)
      .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  public <T extends WorkpackModel> T cast(final WorkpackModel workpackModel) {
    return (T) this.workpackClassType.cast(workpackModel);
  }
}
