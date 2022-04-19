package br.gov.es.openpmo.utils;

@FunctionalInterface
public interface TriPredicate<T, U, V> {

    boolean test(T first, U second, V third);

}
