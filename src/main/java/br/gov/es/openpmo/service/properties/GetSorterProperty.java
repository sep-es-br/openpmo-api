package br.gov.es.openpmo.service.properties;

import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.repository.PropertyRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetSorterProperty {

  private final PropertyRepository repository;
  private final CustomFilterRepository customFilterRepository;

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

    final Optional<CustomFilter> maybeDefaultCustomFilter = this.customFilterRepository.findDefaultByTypeAndWorkpackId(
      idPerson,
      idWorkpack
    );

    if (maybeDefaultCustomFilter.isPresent()) {

      final Optional<Property<?,?>> property = this.repository.findByWorkpackIdAndPropertyModelId(
        idWorkpack,
        Long.valueOf(maybeDefaultCustomFilter.get().getSortBy()
        )
      );

      if(!property.isPresent()) {
        return SorterProperty.empty();
      }
      return SorterProperty.definedByCustomFilter(property.get(), maybeDefaultCustomFilter.get());
    }

    return this.repository.findWorkpackModelSorterPropertyByWorkpackId(idWorkpack)
      .map(SorterProperty::definedByWorkpackModel)
      .orElse(SorterProperty.empty());
  }

}
