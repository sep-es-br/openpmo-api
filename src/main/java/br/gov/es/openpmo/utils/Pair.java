package br.gov.es.openpmo.utils;

public class Pair<T, U> {

  private final T first;
  private final U second;

  public Pair(
    final T first,
    final U second
  ) {
    this.first = first;
    this.second = second;
  }

  public static <T, U> Pair<T, U> of(
    final T first,
    final U second
  ) {
    return new Pair<>(first, second);
  }

  public T getFirst() {
    return this.first;
  }

  public U getSecond() {
    return this.second;
  }

}
