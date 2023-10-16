package br.gov.es.openpmo.service.properties;

import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.repository.PropertyRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
public class GetSorterProperty {

  private final PropertyRepository repository;
  private final CustomFilterRepository customFilterRepository;

  private static final Map<Long, List<CustomFilter>> customFiltersByPersonId = new HashMap<>();
  private static final Map<Long, List<Property>> propertiesByPropertyModelId = new HashMap<>();
  private static List<Property> sorterProperties = null;

  public GetSorterProperty(
    final PropertyRepository repository,
    final CustomFilterRepository customFilterRepository
  ) {
    this.repository = repository;
    this.customFilterRepository = customFilterRepository;
  }

  public SorterProperty<?> execute(
    final Long idWorkpack,
    final Long idPerson
  ) {
    final Optional<CustomFilter> maybeDefaultCustomFilter = getDefaultByTypeAndWorkpackId(idWorkpack, idPerson);
    if (maybeDefaultCustomFilter.isPresent()) {
      final CustomFilter customFilter = maybeDefaultCustomFilter.get();
      final Long idPropertyModel = Long.valueOf(customFilter.getSortBy());
      final Optional<Property> maybeProperty = getProperty(idWorkpack, idPropertyModel);
      if (maybeProperty.isPresent()) {
        final Property property = maybeProperty.get();
        return SorterProperty.definedByCustomFilter(property, customFilter);
      }
      return SorterProperty.empty();
    }
    final Optional<Property> maybeProperty = getSorterProperty(idWorkpack);
    if (maybeProperty.isPresent()) {
      final Property property = maybeProperty.get();
      return SorterProperty.definedByWorkpackModel(property);
    }
    return SorterProperty.empty();
  }

  private Optional<Property> getProperty(Long idWorkpack, Long idPropertyModel) {
    final List<Property> properties;
    if (propertiesByPropertyModelId.containsKey(idPropertyModel)) {
      properties = propertiesByPropertyModelId.get(idPropertyModel);
    } else {
      properties = this.repository.findAllByPropertyModelId(idPropertyModel);
      propertiesByPropertyModelId.put(idPropertyModel, properties);
    }
    return getProperty(idWorkpack, properties);
  }

  private Optional<Property> getSorterProperty(Long idWorkpack) {
    if (sorterProperties == null) {
      sorterProperties = this.repository.findAllSorterProperties();
    }
    return getProperty(idWorkpack, sorterProperties);
  }

  private static Optional<Property> getProperty(Long idWorkpack, List<Property> properties) {
    for (Property property : properties) {
      final Workpack workpack = property.getWorkpack();
      if (workpack != null) {
        if (Objects.equals(workpack.getId(), idWorkpack)) {
          return Optional.of(property);
        }
      }
    }
    return Optional.empty();
  }

  private Optional<CustomFilter> getDefaultByTypeAndWorkpackId(Long idWorkpack, Long idPerson) {
    final List<CustomFilter> customFilters;
    if (customFiltersByPersonId.containsKey(idPerson)) {
      customFilters = customFiltersByPersonId.get(idPerson);
    } else {
      customFilters = this.customFilterRepository.findAllByPersonId(idPerson);
      customFiltersByPersonId.put(idPerson, customFilters);
    }
    for (CustomFilter customFilter : customFilters) {
      final WorkpackModel workpackModel = customFilter.getWorkpackModel();
      final Set<? extends Workpack> instances = workpackModel.getInstances();
      if (instances == null) {
        continue;
      }
      for (Workpack instance : instances) {
        if (Objects.equals(instance.getId(), idWorkpack)) {
          return Optional.of(customFilter);
        }
      }
    }
    return Optional.empty();
  }

  public static void clear() {
    customFiltersByPersonId.clear();
    propertiesByPropertyModelId.clear();
    sorterProperties = null;
  }

}
