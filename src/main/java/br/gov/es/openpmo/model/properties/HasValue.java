package br.gov.es.openpmo.model.properties;

public interface HasValue<V> {

  V getValue();

  void setValue(final V value);

}
