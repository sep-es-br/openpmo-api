package br.gov.es.openpmo.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ObjectUtils {
  public static <T> void updateIfPresent(final Supplier<T> data, final Consumer<T> executor) {
    if(data.get() != null) {
      executor.accept(data.get());
    }
  }
}
