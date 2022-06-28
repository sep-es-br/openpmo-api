package br.gov.es.openpmo.apis.edocs.interfaces;

import br.gov.es.openpmo.apis.organograma.OrganogramaApi;
import org.json.JSONObject;

import java.util.Optional;
import java.util.function.BiFunction;

@FunctionalInterface
public interface ModelFunction extends BiFunction<OrganogramaApi, JSONObject, Optional<String>> {
}
