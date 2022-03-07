package br.gov.es.openpmo.model.dashboards;

import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DashboardUtils {

    public static <T, R, S extends Collection<R>> void apply(
            @NonNull Collection<T> objectCollection,
            @NonNull Function<T, R> mapper,
            @NonNull Consumer<S> consumer,
            @NonNull Supplier<S> constructorSupplier
    ) {
        final S collection = constructorSupplier.get();
        objectCollection.forEach(object -> apply(object, mapper, collection::add));
        consumer.accept(collection);
    }

    public static <T, R> void apply(
            T object,
            @NonNull Function<T, R> mapper,
            @NonNull Consumer<R> consumer
    ) {
        Optional.ofNullable(object).map(mapper).ifPresent(consumer);
    }

}
