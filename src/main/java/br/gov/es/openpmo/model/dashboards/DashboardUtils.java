package br.gov.es.openpmo.model.dashboards;

import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DashboardUtils {

  public static <T, R, S extends Collection<R>> void apply(
    @NonNull final Collection<T> objects,
    @NonNull final Function<T, R> mapper,
    @NonNull final Consumer<S> consumer,
    @NonNull final Supplier<S> supplier
  ) {
    final S collection = supplier.get();
    for(final T object : objects) {
      apply(object, mapper, collection::add);
    }
    consumer.accept(collection);
  }

  public static <T, R> void apply(
    final T object,
    @NonNull final Function<T, R> mapper,
    @NonNull final Consumer<R> consumer
  ) {
    if(object != null) {
      final R r = mapper.apply(object);
      if(r != null) consumer.accept(r);
    }
  }

}
