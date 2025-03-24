package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardMenuResponse;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.workpack.GetPropertyValue;
import br.gov.es.openpmo.service.workpack.PropertyComparator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

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
    final List<Workpack> workpacks = getWorkpacks(idWorkpackActual, idWorkpackModel, menuLevel);
    return getResponses(workpacks, workpackModel);
  }

  private List<Workpack> getWorkpacks(Long idWorkpackActual, Long idWorkpackModel, Long menuLevel) {
    if (menuLevel == 1) {
      return this.workpackRepository.findWorkpackByWorkpackModelLevel1(idWorkpackActual, idWorkpackModel);
    }
    return this.workpackRepository.findWorkpackByWorkpackModelLevel2(idWorkpackActual, idWorkpackModel);
  }

  private WorkpackModel getWorkpackModel(Long idWorkpackModel) {
    return workpackModelRepository.findByIdWithChildren(idWorkpackModel)
      .orElseThrow(() -> new RegistroNaoEncontradoException(WORKPACKMODEL_NOT_FOUND));
  }

  private static List<DashboardMenuResponse> getResponses(List<Workpack> workpacks, WorkpackModel workpackModel) {
    if (workpacks.isEmpty()) {
      return new ArrayList<>();
    }
    sortWorkpacks(workpacks, workpackModel);
    final List<DashboardMenuResponse> result = new ArrayList<>();
    for (Workpack workpack : workpacks) {
      final DashboardMenuResponse response = new DashboardMenuResponse();
      response.setId(workpack.getId());
      response.setName(workpack.getName());
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
    final Map<WorkpackModel, List<Workpack>> map = new HashMap<>();
    for (WorkpackModel workpackModel : workpackModels) {
      for (Workpack workpack : workpacks) {
        if (workpack.isInstanceByOrLinkedTo(workpackModel.getId())) {
          final List<Workpack> list = map.computeIfAbsent(workpackModel, k -> new ArrayList<>());
          list.add(workpack);
        }
      }
      if (map.containsKey(workpackModel)) {
        sortWorkpacks(map.get(workpackModel), workpackModel);
      }
    }
    final List<DashboardMenuResponse> result = new ArrayList<>();
    for (List<Workpack> value : map.values()) {
      for (Workpack workpack : value) {
        final DashboardMenuResponse response = new DashboardMenuResponse();
        response.setId(workpack.getId());
        response.setName(workpack.getName());
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
    }
    return result;
  }

  private static void sortWorkpacks(
    final List<Workpack> responses,
    final WorkpackModel workpackModel
  ) {
    final PropertyModel sortBy = workpackModel.getSortBy();
    if (sortBy != null) {
      responses.sort((first, second) -> PropertyComparator.compare(
              GetPropertyValue.getValueProperty(first, sortBy),
              GetPropertyValue.getValueProperty(second, sortBy)
      ));
    } else {
      if (!StringUtils.isEmpty(workpackModel.getSortByField())) {
        switch (workpackModel.getSortByField()) {
          case "name":
            responses.sort(Comparator.comparing(Workpack::getName, Comparator.nullsLast(Comparator.naturalOrder())));
            break;
          case "fullName":
            responses.sort(Comparator.comparing(Workpack::getFullName, Comparator.nullsLast(Comparator.naturalOrder())));
            break;
          case "date":
            responses.sort(Comparator.comparing(Workpack::getDate, Comparator.nullsLast(Comparator.naturalOrder())));
            break;
          default:
            break;
        }
      }
    }

  }

}
