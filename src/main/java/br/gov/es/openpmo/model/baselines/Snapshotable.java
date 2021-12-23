package br.gov.es.openpmo.model.baselines;

import br.gov.es.openpmo.enumerator.CategoryEnum;

public interface Snapshotable<T> {

  T snapshot();

  Baseline getBaseline();

  void setBaseline(Baseline baseline);

  CategoryEnum getCategory();

  void setCategory(CategoryEnum category);

  boolean hasChanges(T other);

}
