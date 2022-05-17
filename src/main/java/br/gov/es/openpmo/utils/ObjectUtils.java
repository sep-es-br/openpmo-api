package br.gov.es.openpmo.utils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ObjectUtils {
    public static <T> void updateIfPresent(final Supplier<T> data, final Consumer<T> executor) {
        Optional.ofNullable(data).map(Supplier::get).ifPresent(executor);
    }
}
