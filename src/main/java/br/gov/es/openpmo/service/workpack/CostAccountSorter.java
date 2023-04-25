package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.SortByDirectionEnum;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.workpacks.CostAccount;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.service.workpack.GetPropertyValue.getValueProperty;
import static br.gov.es.openpmo.service.workpack.PropertyComparator.compare;
import static br.gov.es.openpmo.utils.ApplicationMessage.CUSTOM_FILTER_NOT_FOUND;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACKMODEL_NOT_FOUND;

@Component
public class CostAccountSorter {

  private final CustomFilterRepository customFilterRepository;

  @Autowired
  public CostAccountSorter(final CustomFilterRepository customFilterRepository) {
    this.customFilterRepository = customFilterRepository;
  }

  public List<CostAccount> sort(final CostAccountSorterRequest request) {
    if(Objects.isNull(request.getIdFilter())) {
      return Collections.unmodifiableList(request.getCostAccounts());
    }

    final CustomFilter customFilter = this.findCustomFilterById(request.getIdFilter());

    return request.getCostAccounts().stream()
      .sorted((cc1, cc2) -> compareUsing(cc1, cc2, customFilter))
      .collect(Collectors.toList());
  }


  private static int compareUsing(
    final CostAccount cc1,
    final CostAccount cc2,
    final CustomFilter customFilter
  ) {

    final Property propertySorter1 = getPropertySorter(cc1, customFilter.getSortBy());
    final Property propertySorter2 = getPropertySorter(cc2, customFilter.getSortBy());

    final SortByDirectionEnum direction = customFilter.getDirection();
    return compare(getValueProperty(propertySorter1), getValueProperty(propertySorter2)) * direction.getOrder();
  }

  private static Property getPropertySorter(
    final CostAccount costAccount,
    final String sortBy
  ) {
    final Long propertyModelId = Long.parseLong(sortBy);
    return costAccount.getProperties().stream()
      .filter(property -> property.getPropertyModel().getId().equals(propertyModelId))
      .findFirst()
      .orElse(null);
  }

  private CustomFilter findCustomFilterById(final Long idFilter) {
    return this.customFilterRepository.findByIdWithRelationships(idFilter)
      .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));
  }


  public static class CostAccountSorterRequest {

    private final Long idFilter;
    private final List<CostAccount> costAccounts;

    public CostAccountSorterRequest(
      final Long idFilter,
      final List<CostAccount> costAccounts
    ) {
      this.idFilter = idFilter;
      this.costAccounts = costAccounts;
    }


    public Long getIdFilter() {
      return this.idFilter;
    }

    public List<CostAccount> getCostAccounts() {
      return this.costAccounts;
    }

  }

}
