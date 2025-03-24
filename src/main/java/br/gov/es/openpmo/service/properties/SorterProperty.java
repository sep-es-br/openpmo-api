package br.gov.es.openpmo.service.properties;

import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.SortByDirectionEnum;
import br.gov.es.openpmo.model.properties.Property;

public class SorterProperty<T> {

  private final T value;
  private final SortByDirectionEnum direction;
  private final Property<?, T> sorterProperty;
  private final boolean hasDefaultFilterSelected;

  public SorterProperty(
    final T value,
    final SortByDirectionEnum direction,
    final Property<?, T> sorterProperty,
    final boolean hasDefaultFilterSelected
  ) {
    this.value = value;
    this.direction = direction;
    this.sorterProperty = sorterProperty;
    this.hasDefaultFilterSelected = hasDefaultFilterSelected;
  }

  public static <T> SorterProperty<T> definedByWorkpackModel(final Property<?, T> property) {
    return new SorterProperty<>(
      property.getValue(),
      SortByDirectionEnum.ASC,
      property,
      false
    );
  }

  public static <T> SorterProperty<T> definedByCustomFilter(
    final Property<?, T> property,
    final CustomFilter customFilter
  ) {
    return new SorterProperty<>(
      property.getValue(),
      customFilter.getDirection(),
      property,
      true
    );
  }

  public static SorterProperty<Void> empty() {
    return new SorterProperty<>(
      null,
      SortByDirectionEnum.ASC,
      null,
      false
    );
  }

  public T getValue() {
    return this.value;
  }

  public SortByDirectionEnum getDirection() {
    return this.direction;
  }

}
