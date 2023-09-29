package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardMenuResponse;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACKMODEL_NOT_FOUND;

@Component
public class GetDashboardMenu {

  private final WorkpackRepository workpackRepository;
  private final WorkpackModelRepository workpackModelRepository;

  public GetDashboardMenu(
    WorkpackRepository workpackRepository,
    WorkpackModelRepository workpackModelRepository
  ) {
    this.workpackRepository = workpackRepository;
    this.workpackModelRepository = workpackModelRepository;
  }

  public List<DashboardMenuResponse> execute(Long idWorkpackActual, Long idWorkpackModel, Long menuLevel) {
    final WorkpackModel workpackModel = getWorkpackModel(idWorkpackModel);
    final Set<Workpack> workpacks = getWorkpacks(idWorkpackActual, idWorkpackModel, menuLevel);
    return getResponses(workpacks, workpackModel);
  }

  private Set<Workpack> getWorkpacks(Long idWorkpackActual, Long idWorkpackModel, Long menuLevel) {
    if (menuLevel == 1) {
      return this.workpackRepository.findWorkpackByWorkpackModelLevel1(idWorkpackActual, idWorkpackModel);
    }
    return this.workpackRepository.findWorkpackByWorkpackModelLevel2(idWorkpackActual, idWorkpackModel);
  }

  private WorkpackModel getWorkpackModel(Long idWorkpackModel) {
    return workpackModelRepository.findByIdWithChildren(idWorkpackModel)
      .orElseThrow(() -> new RegistroNaoEncontradoException(WORKPACKMODEL_NOT_FOUND));
  }

  private static List<DashboardMenuResponse> getResponses(Set<Workpack> workpacks, WorkpackModel workpackModel) {
    if (workpacks.isEmpty()) {
      return new ArrayList<>();
    }
    final List<DashboardMenuResponse> result = new ArrayList<>();
    for (Workpack workpack : workpacks) {
      final DashboardMenuResponse response = new DashboardMenuResponse();
      response.setId(workpack.getId());
      response.setName(workpack.getWorkpackName());
      if (workpack.isLinkedTo(workpackModel)) {
        response.setLinked(true);
        response.setIdWorkpackModel(workpackModel.getId());
        response.setIcon(workpackModel.getFontIcon());
      }
      if (!Boolean.TRUE.equals(response.getLinked())) {
        final WorkpackModel instance = workpack.getWorkpackModelInstance();
        if (instance != null) {
          response.setLinked(false);
          response.setIdWorkpackModel(instance.getId());
          response.setIcon(instance.getFontIcon());
        }
      }
      final Set<Workpack> children = workpack.getChildren();
      if (children != null) {
        response.setWorkpacks(getResponseChildren(children, workpackModel.getChildren()));
      }
      result.add(response);
    }
    return result;
  }

  private static List<DashboardMenuResponse> getResponseChildren(Set<Workpack> workpacks, Set<WorkpackModel> workpackModels) {
    if (workpacks.isEmpty()) {
      return new ArrayList<>();
    }
    final List<DashboardMenuResponse> result = new ArrayList<>();
    for (Workpack workpack : workpacks) {
      final DashboardMenuResponse response = new DashboardMenuResponse();
      response.setId(workpack.getId());
      response.setName(workpack.getWorkpackName());
      for (WorkpackModel workpackModel : workpackModels) {
        if (workpack.isLinkedTo(workpackModel)) {
          response.setLinked(true);
          response.setIdWorkpackModel(workpackModel.getId());
          response.setIcon(workpackModel.getFontIcon());
        }
      }
      if (!Boolean.TRUE.equals(response.getLinked())) {
        final WorkpackModel instance = workpack.getWorkpackModelInstance();
        if (instance != null) {
          response.setLinked(false);
          response.setIdWorkpackModel(instance.getId());
          response.setIcon(instance.getFontIcon());
        }
      }
      result.add(response);
    }
    return result;
  }

}
