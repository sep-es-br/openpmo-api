package br.gov.es.openpmo.utils;

@FunctionalInterface
public interface TetraPredicate<T, U, V, W> {

  boolean test(
    T first,
    U second,
    V third,
    W fourth
  );

}
