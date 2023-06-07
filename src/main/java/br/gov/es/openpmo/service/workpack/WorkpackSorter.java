package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.SortByDirectionEnum;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.service.workpack.GetPropertyValue.getValueProperty;
import static br.gov.es.openpmo.service.workpack.PropertyComparator.compare;
import static br.gov.es.openpmo.utils.ApplicationMessage.CUSTOM_FILTER_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACKMODEL_NOT_FOUND;

@Component
public class WorkpackSorter {

  private final WorkpackModelRepository workpackModelRepository;
  private final CustomFilterRepository customFilterRepository;

  @Autowired
  public WorkpackSorter(
    final WorkpackModelRepository workpackModelRepository,
    final CustomFilterRepository customFilterRepository
  ) {
    this.workpackModelRepository = workpackModelRepository;
    this.customFilterRepository = customFilterRepository;
  }

  public List<Workpack> sort(final WorkpackSorterRequest request) {
    if(StringUtils.hasText(request.getTerm())) return request.getWorkpacks();

    if(Objects.isNull(request.getIdFilter())) {
      final WorkpackModel workpackModel = this.findById(
        request.getIdWorkpackModel()
      );
      if(workpackModelHasSortBy(workpackModel)) {
        sortWorkpacks(request.getWorkpacks(), workpackModel);
      }
      return Collections.unmodifiableList(request.getWorkpacks());
    }

    final CustomFilter customFilter = this.findCustomFilterById(request.getIdFilter());

    return request.getWorkpacks().stream()
      .sorted((w1, w2) -> compareUsing(w1, w2, customFilter))
      .collect(Collectors.toList());
  }


  private static int compareUsing(
    final Workpack w1,
    final Workpack w2,
    final CustomFilter customFilter
  ) {

    final Property propertySorter1 = getPropertySorter(w1, customFilter.getSortBy());
    final Property propertySorter2 = getPropertySorter(w2, customFilter.getSortBy());

    final SortByDirectionEnum direction = customFilter.getDirection();
    return compare(getValueProperty(propertySorter1), getValueProperty(propertySorter2)) * direction.getOrder();
  }

  private static Property getPropertySorter(
    final Workpack workpack,
    final String sortBy
  ) {
    final Long propertyModelId = Long.parseLong(sortBy);
    return workpack.getProperties().stream()
      .filter(property -> property.getPropertyModel().getId().equals(propertyModelId))
      .findFirst()
      .orElse(null);
  }

  private static void sortWorkpacks(
    final List<? extends Workpack> workpacks,
    final WorkpackModel workpackModel
  ) {
    workpacks.sort((a, b) -> compare(
      getValueProperty(a, workpackModel.getSortBy()),
      getValueProperty(b, workpackModel.getSortBy())
    ));
  }


  private static boolean workpackModelHasSortBy(final WorkpackModel workpackModel) {
    return workpackModel != null && workpackModel.getSortBy() != null;
  }

  private CustomFilter findCustomFilterById(final Long idFilter) {
    return this.customFilterRepository.findByIdWithRelationships(idFilter)
      .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));
  }

  private WorkpackModel findById(final Long id) {
    return this.workpackModelRepository.findAllByIdWorkpackModel(id).orElseThrow(
      () -> new NegocioException(WORKPACKMODEL_NOT_FOUND));
  }

  public static class WorkpackSorterRequest {

    private final Long idPlan;
    private final Long idPlanModel;
    private final Long idWorkpackModel;
    private final Long idFilter;
    private final List<Workpack> workpacks;
    private final String term;

    public WorkpackSorterRequest(
      final Long idPlan,
      final Long idPlanModel,
      final Long idWorkpackModel,
      final Long idFilter,
      final List<Workpack> workpacks,
      final String term
    ) {
      this.idPlan = idPlan;
      this.idPlanModel = idPlanModel;
      this.idWorkpackModel = idWorkpackModel;
      this.idFilter = idFilter;
      this.workpacks = workpacks;
      this.term = term;
    }

    public Long getIdPlan() {
      return this.idPlan;
    }

    public Long getIdPlanModel() {
      return this.idPlanModel;
    }

    public Long getIdWorkpackModel() {
      return this.idWorkpackModel;
    }

    public Long getIdFilter() {
      return this.idFilter;
    }

    public List<Workpack> getWorkpacks() {
      return this.workpacks;
    }

    public String getTerm() {
      return this.term;
    }

  }

}
